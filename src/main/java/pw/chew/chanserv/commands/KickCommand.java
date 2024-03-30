package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;

public class KickCommand extends SlashCommand {

    public KickCommand() {
        this.name = "kick";
        this.help = "Kick a specified user (requires Half-op+)";

        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The user to kick.").setRequired(true),
            new OptionData(OptionType.STRING, "reason", "The reason for the kick")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member user = event.optMember("user", null);
        String reason = event.optString("reason", "*No reason provided*");

        if (user == null) {
            event.reply("User not found.").setEphemeral(true).queue();
            return;
        }

        user.kick().reason(reason).queue(userid -> {
            event.replyEmbeds(new EmbedBuilder()
                .setTitle("**User Kicked Successfully**")
                .setDescription("Say goodbye to that user " + user.getUser().getName())
                .build()).queue();
        });
    }
}
