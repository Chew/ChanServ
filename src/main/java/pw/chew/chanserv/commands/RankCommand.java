package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import pw.chew.chanserv.util.MemberHelper;

public class RankCommand extends Command {
    public RankCommand() {
        this.name = "rank";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String rank = MemberHelper.getRank(event.getMember()).getRoleName();
        event.reply("Your rank is: " + rank);
    }
}
