package xyz.gnarbot.gnar.commands.executors.fun;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.util.Map;
import java.util.WeakHashMap;

@Command(
        id = 7,
        aliases = "pbot",
        usage = "(words...)",
        description = "Talk with a bot, you lonely thing.",
        category = Category.FUN
)
public class PandoraBotCommand extends CommandExecutor {
    private static final ChatterBotFactory factory = new ChatterBotFactory();
    private static ChatterBot bot = null;

    private static final Map<Long, ChatterBotSession> sessions = new WeakHashMap<>();

    @Override
    public void execute(Context context, String label, String[] args) {
        if (bot == null) {
            try {
                bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (args.length > 0) {
            if ("reset".equals(args[0])) {
                sessions.put(context.getGuild().getIdLong(), null);
            }
        }

        try {
            ChatterBotSession session = sessions.computeIfAbsent(context.getGuild().getIdLong(), k -> {
                context.send().info("Pandora-Bot session created for the server.").queue();
                return context.getBot().createSession();
            });

            String input = StringUtils.join(args, " ");

            String output = session.think(input);
            context.send().embed()
                    .setDescription(output)
                    .action().queue();
        } catch (Exception e) {
            context.send().error("PandoraBot has encountered an exception. Resetting PandoraBot.").queue();
            bot = null;
        }
    }

}
