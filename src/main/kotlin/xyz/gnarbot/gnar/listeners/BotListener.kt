package xyz.gnarbot.gnar.listeners

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.*
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.BotLoader
import xyz.gnarbot.gnar.commands.Context

import java.util.concurrent.TimeUnit

class BotListener(private val bot: Bot) : ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent?) {
        Bot.LOG.info("✅ Joined `" + event!!.guild.name + "`")
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent?) {
        if (event!!.author.isBot) {
            if (event.author === event.jda.selfUser) {
                if (bot.options.ofGuild(event.guild).command.isAutoDelete) {
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                }
            }
            return
        }

//        if ((event.message.contentRaw.startsWith('_') && event.message.contentRaw.endsWith('_')) || (event.message.contentRaw.startsWith(BotLoader.BOT.configuration.prefix) && (event.message.contentRaw.endsWith(BotLoader.BOT.configuration.prefix))))   {
//            return
//        } //Prevent markdown responses

        bot.commandDispatcher.handle(Context(bot, event))
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent?) {
        if (bot.isLoaded) {
            val options = bot.options.ofGuild(event!!.guild)

            // Autorole
            if (options.roles.autoRole != null) {
                val role = event.guild.getRoleById(options.roles.autoRole)

                // If role is null then unset
                if (role == null) {
                    options.roles.setAutoRole(null)
                    return
                }

                // If bot cant interact with role then unset
                if (!event.guild.selfMember.canInteract(role) || !event.guild.selfMember.hasPermission(Permission.MANAGE_ROLES)) {
                    options.roles.setAutoRole(null)
                    return
                }

                // Add the role to the member
                event.guild.controller.addSingleRoleToMember(event.member, role).reason("Auto-role").queue()
            }

            //LogHelper.tryLogging("${event.member.effectiveName} has joined the server! (Member #${event.guild.members.size})", options, event.guild, "UserJoin")
        }
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        bot.players.destroy(event!!.guild)
        Bot.LOG.info("❌ Left `" + event.guild.name + "`")
    }

    override fun onReady(event: ReadyEvent?) {
        Bot.LOG.info("JDA " + (if (event!!.jda.shardInfo != null) event.jda.shardInfo.shardId else "instance") + " is ready.")
    }

    override fun onResume(event: ResumedEvent?) {
        Bot.LOG.info("JDA " + event!!.jda.shardInfo.shardId + " has resumed.")
    }

    override fun onReconnect(event: ReconnectedEvent?) {
        Bot.LOG.info("JDA " + event!!.jda.shardInfo.shardId + " has reconnected.")
    }

    override fun onDisconnect(event: DisconnectEvent) {
        if (event.isClosedByServer) {
            Bot.LOG.info("JDA " + event.jda.shardInfo.shardId + " has disconnected (closed by server). "
                    + "Code: " + event.serviceCloseFrame.closeCode + " " + event.closeCode)
        } else {
            if (event.clientCloseFrame != null) {
                Bot.LOG.info("JDA " + event.jda.shardInfo.shardId + " has disconnected. "
                        + "Code: " + event.clientCloseFrame.closeCode + " " + event.clientCloseFrame.closeReason)
            } else {
                Bot.LOG.info("JDA " + event.jda.shardInfo.shardId + " has disconnected. CCF Null. Code: " + event.closeCode)
            }
        }

        // Clean up all of the MusicManagers associated with that shard.
        val iterator = bot.players.registry.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value == null) {
                iterator.remove()
                Bot.LOG.warn("Null manager for id " + entry.key)
            } else if (entry.value.guild.jda === event.jda) {
                entry.value.destroy()
                iterator.remove()
            }
        }

    }

    override fun onException(event: ExceptionEvent?) {
        if (!event!!.isLogged) Bot.LOG.error("Error thrown by JDA.", event.cause)
    }
}
