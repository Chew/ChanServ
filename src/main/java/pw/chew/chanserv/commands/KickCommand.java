package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.MemberHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class KickCommand extends SlashCommand {

    public KickCommand() {
        this.name = "kick";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.help = "Kick a specified user (requires Half-op+)";

        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.USER, "user", "The user to kick.").setRequired(true));
        data.add(new OptionData(OptionType.STRING, "reason", "The reason for the kick"));
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (MemberHelper.getRank(event.getMember()).getPriority() < 2) {
            event.reply(
                new EmbedBuilder()
                    .setTitle("**Permission Error**")
                    .setDescription("You do not have the proper user modes to do this! You must have +h (half-op) or higher.")
                    .setColor(Color.RED)
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        Member user = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") == null ? "*No reason provided*" : event.getOption("reason").getAsString();

        user.kick(reason).queue(userid -> {
            AuditLogManager.logEntry(AuditLogManager.LogType.KICK, user.getUser(), event.getMember(), event.getGuild());
            event.reply(new EmbedBuilder()
                .setTitle("**User Kicked Successfully**")
                .setDescription("Say goodbye to that user " + user.getUser().getAsTag())
                .build()).queue();
        });
    }
}
