package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.Roles;
import pw.chew.chewbotcca.util.ResponseHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeCommand extends SlashCommand {

    public ModeCommand() {
        this.name = "mode";
        this.help = "Change a specified user's modes (requires Admin+)";

        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The user to promote to voiced.", true),
            new OptionData(OptionType.STRING, "mode", "The mode to give.", true, true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member user = event.optMember("user", event.getMember());
        String mode = event.optString("mode", "");

        // Null checks
        Checks.notNull(user, "User");
        Checks.notNull(event.getGuild(), "Server");

        if (MemberHelper.getRank(user).getPriority() >= MemberHelper.getRank(event.getMember()).getPriority()) {
            event.replyEmbeds(ResponseHelper.generateFailureEmbed("Error Moment!", "You cannot change the mode of a user with a higher or equal rank.")).setEphemeral(true).queue();
            return;
        }

        // Figure out what the current input is
        // We have an input such as +abc-xyz
        // This means add a, b, and c, and remove x, y, and z
        List<String> modesToAdd = new ArrayList<>();
        List<String> modesToRemove = new ArrayList<>();

        // Now, let's split the input into a list of modes
        // We want to handle a string like +ab-cd+ef-gh
        // This means we add a, b, and remove c, d, and add e, f, and remove g, h
        // So, we split on the + and - and then add the modes to the list
        String[] splitInput = mode.split("(?=[+-])"); // will give us ["+ab", "-cd", "+ef", "-gh"]
        for (String input : splitInput) {
            // If the input is empty, skip it
            if (input.isEmpty()) {
                continue;
            }

            // If the input starts with a +, add the modes
            if (input.startsWith("+")) {
                modesToAdd.addAll(Arrays.asList(input.substring(1).split("")));
            }

            // If the input starts with a -, remove the modes
            if (input.startsWith("-")) {
                modesToRemove.addAll(Arrays.asList(input.substring(1).split("")));
            }
        }

        // Now we can add the modes
        for (String modeToAdd : modesToAdd) {
            // Get the mode from the string
            Roles.UserMode userMode = Roles.UserMode.valueOf(modeToAdd);

            // Add the role
            userMode.assignMode(user);
        }

        // Now we can remove the modes
        for (String modeToRemove : modesToRemove) {
            // Get the mode from the string
            Roles.UserMode userMode = Roles.UserMode.valueOf(modeToRemove);

            // Remove the role
            userMode.removeMode(user);
        }

        event.replyEmbeds(new EmbedBuilder()
            .setTitle("**User Mode Changed Successfully**")
            .setDescription(user.getAsMention() + " has modes updated to " + mode + " by " + event.getUser().getAsMention())
            .setColor(Color.GREEN)
            .build()).queue();
        AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), mode);
    }

    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        // So we want to be fancy?

        // Get all assignable user modes
        Roles.UserMode[] modes = Roles.UserMode.values();

        // Get the current input
        String input = event.getOption("mode").getAsString();

        // If the input is empty, we can just return
        if (input.isEmpty()) {
            event.replyChoices(new Command.Choice("Start Typing!", "n/a")).queue();
            return;
        }

        // If the input doesn't start with + or -, we can just return
        if (!input.startsWith("+") && !input.startsWith("-")) {
            event.replyChoices(new Command.Choice("Invalid input! Start with + or -", "n/a")).queue();
            return;
        }

        // Get only letters from the input
        String[] split = input.replaceAll("[^a-zA-Z]", "").split("");
        // Add the letters to the list
        List<String> modesInput = new ArrayList<>(Arrays.asList(split));

        // Now we can start suggesting!
        List<Command.Choice> choices = new ArrayList<>();
        // We're adding modes, so we need to suggest modes that aren't already added
        for (Roles.UserMode mode : modes) {
            // If the mode is already added, we can skip it
            if (modesInput.contains(mode.name())) continue;

            // If the mode can't be given, we can skip it
            if (!mode.canGive()) continue;

            // If we made it this far, we can add the mode to the list of choices. We need to handle our current input, tho
            choices.add(new Command.Choice(input + mode.name(), input + mode.name()));
        }

        // Now we can reply with the choices
        event.replyChoices(choices.toArray(new Command.Choice[0])).queue();
    }
}
