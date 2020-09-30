package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import pw.chew.chanserv.util.Community;

public class LeaveCommand extends Command {

    public LeaveCommand() {
        this.name = "leave";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().delete().queue();
        if (event.getTextChannel().getParent().getId().equals(Community.categoryId)) {
            Community.valueOf(event.getChannel().getName().toUpperCase()).removeMember(event.getMember());
        }
    }
}
