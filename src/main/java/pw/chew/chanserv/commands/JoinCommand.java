package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import pw.chew.chanserv.util.Community;

public class JoinCommand extends Command {

    public JoinCommand() {
        this.name = "join";
        this.aliases = new String[]{"j"};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String channel = event.getArgs().toUpperCase().replace(" ", "");
        event.getMessage().delete().queue();

        Community community;
        try {
            community = Community.valueOf(channel);
        } catch (IllegalArgumentException e) {
            event.reply("Not a valid community!");
            return;
        }

        community.addMember(event.getMember());
    }
}