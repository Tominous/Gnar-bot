package xyz.gnarbot.gnar.commands.executors.admin;

import xyz.gnarbot.gnar.commands.BotInfo;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.Context;
import xyz.gnarbot.gnar.commands.template.CommandTemplate;
import xyz.gnarbot.gnar.commands.template.annotations.Description;
import xyz.gnarbot.gnar.db.PremiumKey;
import xyz.gnarbot.gnar.db.Redeemer;
import xyz.gnarbot.gnar.db.guilds.GuildData;
import xyz.gnarbot.gnar.utils.Utils;

import java.util.StringJoiner;
import java.util.UUID;

@Command(
        aliases = {"key", "keys"}
)
@BotInfo(
        id = 1,
        admin = true,
        category = Category.NONE
)
public class PremiumKeyCommand extends CommandTemplate {
    @Description("Generate a premium key.")
    public void gen(Context context, int number, PremiumKey.Type type, String durationTxt) {
        long duration = Utils.parseTime(durationTxt);

        if (duration < 0) {
            context.send().error("Negative duration, we get it you vape.").queue();
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            PremiumKey key = new PremiumKey(UUID.randomUUID().toString(), type, duration);
            builder.append(key.getId()).append('\n');
            key.save();
        }

        context.getUser().openPrivateChannel().queue(it -> it.sendMessage("```\n" + builder.toString() + "```").queue());
    }

    @Description("Revoke a premium key.")
    public void revoke(Context context, String idString) {
        String[] ids = idString.split(",\\s*|\n");

        StringJoiner joiner = new StringJoiner("\n");

        for (String id : ids) {
            if (id.isEmpty()) continue;

            PremiumKey key = context.getBot().db().getPremiumKey(id);

            joiner.add("**Key** `" + id + "`");

            if (key == null) {
                joiner.add("Doesn't exist in the database.\n");
                continue;
            }

            Redeemer redeemer = key.getRedeemer();

            if (redeemer != null) {
                switch (redeemer.getType()) {
                    case GUILD:
                        GuildData guildData = context.getBot().db().getGuildData(redeemer.getId());

                        if (guildData != null) {
                            guildData.getPremiumKeys().remove(key.getId());
                            guildData.save();

                            joiner.add("Revoked the key from guild ID `" + guildData.getId() + "`.");
                        } else {
                            joiner.add("Guild ID `" + redeemer.getId() + "` redeemed the key but no longer exists in the DB.");
                        }

                        break;
                    default:
                        joiner.add("Unknown redeemer type.");
                        break;
                }
            } else {
                joiner.add("Not redeemed.");
            }

            key.delete();

            joiner.add("Deleted from the database.\n");
        }

        context.send().info(joiner.toString()).queue();
    }
}
