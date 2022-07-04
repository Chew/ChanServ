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

public class OpCommand extends SlashCommand {
    public OpCommand() {
        this.name = "op";
        this.help = "Promote a user to Op (requires Admin+)";

        this.options = Collections.singletonList(
            new OptionData(OptionType.USER, "user", "The user to promote to operator.").setRequired(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member user = event.getOption("user").getAsMember();
        event.getGuild().addRoleToMember(user, Roles.Rank.OP.getRole(event.getGuild())).queue(
            e -> {
                event.replyEmbeds(new EmbedBuilder()
                    .setTitle("**User Mode Changed Successfully**")
                    .setDescription(user.getAsMention() + " has been opped by " + event.getUser().getAsMention())
                    .setColor(Color.GREEN)
                    .build()).queue();
                AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), event.getGuild(), "+o");
            }
        );
    }
}
