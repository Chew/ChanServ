package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.AuditLogManager;
import pw.chew.chanserv.util.MemberHelper;
import pw.chew.chanserv.util.Roles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class OpCommand extends SlashCommand {
    public OpCommand() {
        this.name = "op";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.help = "Promote a user to Op (requires Admin+)";

        List<OptionData> data = new ArrayList<>();
        data.add(
            new OptionData(OptionType.USER, "user", "The user to promote to operator.").setRequired(true)
        );
        this.options = data;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (MemberHelper.getRank(event.getMember()).getPriority() < 4) {
            event.reply(
                new EmbedBuilder()
                    .setTitle("**Permission Error**")
                    .setDescription("You do not have the proper user modes to do this! You must have +a (Admin) or higher.")
                    .setColor(Color.RED)
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        Member user = event.getOption("user").getAsMember();
        event.getGuild().addRoleToMember(user, Roles.Rank.OP.getRole(event.getGuild())).queue(
            e -> {
                event.reply(new EmbedBuilder()
                    .setTitle("**User Mode Changed Successfully**")
                    .setDescription(user.getAsMention() + " has been opped by " + event.getUser().getAsMention())
                    .setColor(Color.GREEN)
                    .build()).queue();
                AuditLogManager.logEntry(AuditLogManager.LogType.MODE_CHANGE, user.getUser(), event.getMember(), event.getGuild(), "+o");
            }
        );
    }
}