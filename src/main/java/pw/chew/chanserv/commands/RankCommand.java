package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import pw.chew.chanserv.util.MemberHelper;

public class RankCommand extends SlashCommand {
    public RankCommand() {
        this.name = "rank";
        this.help = "Get your current rank.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String rank = MemberHelper.getRank(event.getMember()).getRoleName();
        event.reply("Your rank is: " + rank).setEphemeral(true).queue();
    }

    @Override
    protected void execute(CommandEvent event) {
        String rank = MemberHelper.getRank(event.getMember()).getRoleName();
        event.reply("Your rank is: " + rank);
    }
}
