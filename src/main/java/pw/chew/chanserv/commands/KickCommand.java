package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.Roles;
import pw.chew.chewbotcca.util.ResponseHelper;

import java.util.Arrays;

public class KickCommand extends SlashCommand {

    public KickCommand() {
        this.name = "kick";
        this.help = "Kick a specified user (requires Half-op+)";
        this.enabledRoles = Roles.Rank.getRoleIdsHigherThan(2);
        this.defaultEnabled = false;

        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "The user to kick.").setRequired(true),
            new OptionData(OptionType.STRING, "reason", "The reason for the kick")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member user = event.getOption("user").getAsMember();
        String reason = ResponseHelper.guaranteeStringOption(event, "reason", "*No reason provided*");

        user.kick(reason).queue(userid -> {
            AuditLogManager.logEntry(AuditLogManager.LogType.KICK, user.getUser(), event.getMember(), event.getGuild());
            event.replyEmbeds(new EmbedBuilder()
                .setTitle("**User Kicked Successfully**")
                .setDescription("Say goodbye to that user " + user.getUser().getAsTag())
                .build()).queue();
        });
    }
}
