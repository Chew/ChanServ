package pw.chew.chanserv.util;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;

public class RankUtil {
    /**
     * Promotes a user to a rank
     *
     * @param event the slash command event this is being called from
     * @param rank the rank to promote to
     */
    public static void promoteTo(SlashCommandEvent event, Roles.Rank rank) {
        Member user = event.optMember("user");
        Guild server = event.getGuild();
        Roles.UserMode mode = Roles.UserMode.fromRoleId(rank.getRoleId());
        if (user == null || server == null || mode == null) {
            event.reply("User not found.").setEphemeral(true).queue();
            return;
        }

        server.addRoleToMember(user, rank.getRole(server)).reason("rank promotion").queue(e -> {
            event.replyEmbeds(buildRankChangeEmbed(rank.getFriendlyName(), user, event.getUser())).queue();
            AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), "+" + mode.name());
        });
    }

    public static MessageEmbed buildRankChangeEmbed(String rank, IMentionable target, IMentionable actor) {
        return new EmbedBuilder()
            .setTitle("**User Mode Changed Successfully**")
            .setDescription("%s has been promoted to %s by %s".formatted(target.getAsMention(), rank, actor.getAsMention()))
            .setColor(Color.GREEN)
            .build();
    }
}
