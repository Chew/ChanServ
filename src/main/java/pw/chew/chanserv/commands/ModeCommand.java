package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.Roles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ModeCommand extends SlashCommand {

    public ModeCommand() {
        this.name = "mode";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.help = "Change a specified user's modes (requires Admin+)";

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "The user to promote to voiced.").setRequired(true));

        OptionData modes = new OptionData(OptionType.STRING, "mode", "The mode to give.").setRequired(true);
        for (Roles.UserMode mode : Roles.UserMode.values()) {
            if (mode.canGive()) {
                modes.addChoice("+" + mode.name(),"+" + mode.name());
                modes.addChoice("-" + mode.name(),"-" + mode.name());
            }
        }
        data.add(modes);

        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (MemberHelper.getRank(event.getMember()).getPriority() < 4) {
            event.reply(
                new EmbedBuilder()
                    .setTitle("**Permission Error**")
                    .setDescription("You do not have the proper user modes to do this! You must have +a (Admin) or higher.")
                    .setColor(Color.RED)
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        Member user = event.getOption("user").getAsMember();
        String mode = event.getOption("mode").getAsString();
        if (mode.length() > 2) {
            boolean add = mode.charAt(0) == '+';
            String modetemp = mode.substring(1);
            String[] modes = modetemp.split("");
            if (add) {
                for (String theMode : modes) {
                    Roles.UserMode to_add = Roles.UserMode.valueOf(theMode);
                    to_add.assignMode(user);
                }
            } else {
                for (String theMode : modes) {
                    Roles.UserMode to_add = Roles.UserMode.valueOf(theMode);
                    to_add.removeMode(user);
                }
            }
        } else if (mode.length() <= 1) {
            return;
        } else if (mode.charAt(0) == '+'){
            Roles.UserMode to_add = Roles.UserMode.valueOf(String.valueOf(mode.charAt(1)));
            to_add.assignMode(user);
        } else if (mode.charAt(0) == '-') {
            Roles.UserMode to_add = Roles.UserMode.valueOf(String.valueOf(mode.charAt(1)));
            to_add.removeMode(user);
        } else {
            return;
        }
        event.reply(new EmbedBuilder()
            .setTitle("**User Mode Changed Successfully**")
            .setDescription(user.getAsMention() + " has been given mode " + mode + " by " + event.getUser().getAsMention())
            .setColor(Color.GREEN)
            .build()).queue();
        AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), event.getGuild(), mode);
    }
}