package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.Roles;

import java.awt.Color;
import java.util.Collections;

public class OwnerCommand extends SlashCommand {
    public OwnerCommand() {
        this.name = "owner";
        this.help = "Promote a user to Owner (requires Oper/NetAdmin)";

        this.options = Collections.singletonList(
            new OptionData(OptionType.USER, "user", "The user to promote to owner.").setRequired(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member user = event.getOption("user").getAsMember();
        event.getGuild().addRoleToMember(user, Roles.Rank.OWNER.getRole(event.getGuild())).queue(
            e -> {
                event.replyEmbeds(new EmbedBuilder()
                    .setTitle("**User Mode Changed Successfully**")
                    .setDescription(user.getAsMention() + " has been promoted to owner by " + event.getUser().getAsMention())
                    .setColor(Color.GREEN)
                    .build()).queue();
                AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), event.getGuild(), "+q");
            }
        );
    }
}
