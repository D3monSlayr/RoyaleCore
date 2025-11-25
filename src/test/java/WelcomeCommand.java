import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WelcomeCommand {

    public static LiteralCommandNode<CommandSourceStack> commandNode() {
        return Commands.literal("test").build();
    }

}
