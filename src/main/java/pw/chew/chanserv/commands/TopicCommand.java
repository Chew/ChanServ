package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import pw.chew.chanserv.util.MemberHelper;

import java.awt.*;

public class TopicCommand extends Command {
    public TopicCommand() {
        this.name = "topic";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (MemberHelper.getRank(event.getMember()).getPriority() >= 2) {
            String currentTopic = event.getTextChannel().getTopic();
            if (currentTopic == null) {
                currentTopic = fixTopic(event.getTextChannel());
            }
            String mode = currentTopic.split(" ")[0];
            event.getTextChannel().getManager().setTopic(mode + ' ' + event.getArgs()).queue(channel -> {
                event.reply(new EmbedBuilder()
                    .setTitle("**" + event.getAuthor().getName() + " set the topic**")
                    .setDescription(event.getArgs())
                    .setColor(Color.GREEN).build()
                );
            });
        }
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