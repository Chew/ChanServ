package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import pw.chew.chanserv.util.Roles;
import pw.chew.chewbotcca.util.ResponseHelper;

import java.awt.Color;
import java.util.Collections;

public class TopicCommand extends SlashCommand {
    public TopicCommand() {
        this.name = "topic";
        this.help = "Change the topic of a channel (requires Half-op+)";
        this.enabledRoles = Roles.Rank.getRoleIdsHigherThan(2);
        this.defaultEnabled = false;

        this.options = Collections.singletonList(
            new OptionData(OptionType.STRING, "topic", "The topic to set.").setRequired(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
            String currentTopic = event.getTextChannel().getTopic();
            if (currentTopic == null) {
                currentTopic = fixTopic(event.getTextChannel());
            }
            String mode = currentTopic.split(" ")[0];
            event.getTextChannel().getManager().setTopic(mode + ' ' +ResponseHelper.guaranteeStringOption(event, "topic", "")).queue(channel -> {
                event.replyEmbeds(new EmbedBuilder()
                    .setTitle("**" + event.getUser().getName() + " set the topic**")
                    .setDescription(ResponseHelper.guaranteeStringOption(event, "topic", ""))
                    .setColor(Color.GREEN).build()
                ).queue();
            });
    }

    public static String fixTopic(TextChannel channel) {
        if (channel.getTopic() == null) {
            channel.getManager().setTopic("[+nt]").queue();
            return "[+nt]";
        } else {
            channel.getManager().setTopic("[+nt] " + channel.getTopic()).queue();
            return "[+nt] " + channel.getTopic();
        }
    }
}