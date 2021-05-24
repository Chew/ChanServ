package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pw.chew.chanserv.util.Community;

public class LeaveCommand extends SlashCommand {

    public LeaveCommand() {
        this.name = "leave";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (event.getTextChannel().getParent().getId().equals(Community.categoryId)) {
            Community.valueOf(event.getChannel().getName().toUpperCase()).removeMember(event.getMember());
        }
        event.acknowledge().queue();
    }
}
