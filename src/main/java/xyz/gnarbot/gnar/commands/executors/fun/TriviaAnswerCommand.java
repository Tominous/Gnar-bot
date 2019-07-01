package xyz.gnarbot.gnar.commands.executors.fun;

import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;
import xyz.gnarbot.gnar.utils.TriviaQuestions;

@Command(
        id = 12,
        aliases = "answer",
        category = Category.FUN
)
public class TriviaAnswerCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (TriviaQuestions.isNotSetup()) {
            TriviaQuestions.init();
        }

        try {
            context.send().info(TriviaQuestions.getAnswer(Integer.valueOf(args[0]))).queue();
        } catch (Exception e) {
            context.send().error("Please enter a number.").queue();
        }
    }

}
