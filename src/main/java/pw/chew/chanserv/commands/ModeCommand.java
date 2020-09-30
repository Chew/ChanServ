package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.Roles;

import java.awt.Color;

public class ModeCommand extends Command {

    public ModeCommand() {
        this.name = "mode";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (MemberHelper.getRank(event.getMember()).getPriority() < 4) {
            event.reply(
                new EmbedBuilder()
                    .setTitle("**Permission Error**")
                    .setDescription("You do not have the proper user modes to do this! You must have +a (Admin) or higher.")
                    .setColor(Color.RED)
                    .build()
            );
            return;
        }

        String id = event.getArgs().split(" ")[0];
        String mode = event.getArgs().split(" ")[1];

        Member user = event.getGuild().getMemberById(id.replace("<@!", "").replace(">", ""));
        if (user == null) {
            event.reply("Member could not be found. How? did they leave when you pinged? wtf. if you see this, something went bad"); // or you"re just browsing github
            return;
        }
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
        AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), event.getGuild(), mode);
    }
}