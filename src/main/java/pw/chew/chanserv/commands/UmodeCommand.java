package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import pw.chew.chanserv.util.MemberHelper;

import java.util.List;

public class UmodeCommand extends Command {
    public UmodeCommand() {
        this.name = "umode";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> modes = MemberHelper.getUserModes(event.getMember());
        if (modes.isEmpty()) {
            event.reply("You have no user modes.");
        } else {
            event.reply("Your user modes are: +" + String.join("", modes));
        }
    }
}