package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pw.chew.chanserv.util.MemberHelper;

import java.util.List;

public class UmodeCommand extends SlashCommand {
    public UmodeCommand() {
        this.name = "umode";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
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