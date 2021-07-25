package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.Community;
import pw.chew.chewbotcca.util.ResponseHelper;

import java.util.Collections;

public class JoinCommand extends SlashCommand {

    public JoinCommand() {
        this.name = "join";
        this.help = "Join a specified community channel.";

        OptionData data = new OptionData(OptionType.STRING, "community", "The community to join.")
            .setRequired(true);

        for (Community community : Community.values()) {
            data.addChoice(community.name(), community.name());
        }

        this.options = Collections.singletonList(data);
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String channel = ResponseHelper.guaranteeStringOption(event, "community", "");

        Community community = Community.valueOf(channel);
        community.addMember(event.getMember());

        event.reply("Joined!").setEphemeral(true).queue();
    }
}