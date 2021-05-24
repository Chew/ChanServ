package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.Community;

import java.util.ArrayList;
import java.util.List;

public class JoinCommand extends SlashCommand {

    public JoinCommand() {
        this.name = "join";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.help = "Join a specified community channel.";

        OptionData data = new OptionData(OptionType.STRING, "community", "The community to join.")
            .setRequired(true);

        for (Community community : Community.values()) {
            data.addChoice(community.name(), community.name());
        }

        List<OptionData> dataList = new ArrayList<>();
        dataList.add(data);

        this.options = dataList;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String channel = event.getOption("community").getAsString();

        Community community = Community.valueOf(channel);
        community.addMember(event.getMember());

        event.reply("Joined!").setEphemeral(true).queue();
    }
}