package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import pw.chew.chanserv.util.MemberHelper;

import java.util.List;

public class UmodeCommand extends SlashCommand {
    public UmodeCommand() {
        this.name = "umode";
        this.help = "Gets your user modes.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        List<String> modes = MemberHelper.getUserModes(event.getMember());
        if (modes.isEmpty()) {
            event.reply("You have no user modes.").setEphemeral(true).queue();
        } else {
            event.reply("Your user modes are: +" + String.join("", modes)).setEphemeral(true).queue();
        }
    }
}
