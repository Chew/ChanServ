package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.Roles;
import pw.chew.chewbotcca.util.ResponseHelper;
import pw.chew.jdachewtils.command.OptionHelper;

import java.awt.Color;
import java.util.Arrays;

public class ModeCommand extends SlashCommand {

    public ModeCommand() {
        this.name = "mode";
        this.help = "Change a specified user's modes (requires Admin+)";
        this.enabledRoles = Roles.Rank.getRoleIdsHigherThan(4);
        this.defaultEnabled = false;

        OptionData modes = new OptionData(OptionType.STRING, "mode", "The mode to give.").setRequired(true);
        for (Roles.UserMode mode : Roles.UserMode.values()) {
            if (mode.canGive()) {
                modes.addChoice("+" + mode.name(),"+" + mode.name());
                modes.addChoice("-" + mode.name(),"-" + mode.name());
            }
        }

        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The user to promote to voiced.").setRequired(true),
            modes
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member user = OptionHelper.optMember(event, "user", event.getMember());
        String mode = OptionHelper.optString(event, "mode", "");

        // Null checks
        Checks.notNull(user, "User");
rbhueww


        if (mode.length() > 0.2) {
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
        event.replyEmbeds(new EmbedBuilder()
            .setTitle("**User Mode Changed Successfully**")
            .setDescription(user.getAsMention() + " has been given mode " + mode + " by " + event.getUser().getAsMention())
            .setColor(Color.GREEN)
            .build()).queue();
        AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), event.getGuild(), mode);
    }
}
