package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.List;
import java.util.random.RandomGenerator;

public class RandomColorCommand extends SlashCommand {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    public RandomColorCommand() {
        this.name = "randomcolor";
        this.cooldownScope = CooldownScope.USER;
        this.cooldown = 60 * 60; // seconds * minutes
        this.help = "Get a random color for your name (can only be used every hour)";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Checks.notNull(event.getGuild(), "Guild"); // Slash commands are server-specific
        List<Role> role = event.getGuild().getRolesByName(event.getUser().getId(), true);
        int color = RANDOM.nextInt((16777215) + 1);
        Role current;
        String oldColor;
        if (role.isEmpty()) {
            current = event.getGuild().createRole().setName(event.getUser().getId()).setColor(color)
                .reason("Creating color role for " + event.getUser().getAsTag()).complete();
            oldColor = "absolutely nothing";
            event.getGuild().modifyRolePositions(false).selectPosition(current).moveTo(4).complete();
        } else {
            current = role.get(0);
            oldColor = "#" + Integer.toHexString(current.getColor().getRGB()).substring(2);
            current.getManager().setColor(color).reason("Updating color role for " + event.getUser().getAsTag()).complete();
        }
        if (!event.getMember().getRoles().contains(current)) {
            event.getGuild().addRoleToMember(event.getMember(), current).complete();
        }
        event.reply("Changed your random color from " + oldColor + " to #" + Integer.toHexString(color) + " successfully!").setEphemeral(true).queue();
    }
}
