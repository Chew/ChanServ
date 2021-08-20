package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class listens, stores, and checks for deleted or edited messages
 */
public class MessageModificationHandler extends ListenerAdapter {
    private final Map<String, Message> messages = new HashMap<>();
    private final String MESSAGE_EDIT_CHANNEL = "425062504293597215";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // Ignore bots
        if (event.getAuthor().isBot()) return;
        // Ignore dms
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;

        // Store the message
        messages.put(event.getMessageId(), event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        // grab message from map
        Message message = messages.get(event.getMessageId());

        if (message == null) {
            return;
        }

        String oldMessage = message.getContentRaw();
        String newMessage = event.getMessage().getContentRaw();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Someone just changed their message!")
            .addField("User", event.getAuthor().getAsMention() + "\n" + event.getAuthor().getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Message", String.format("[Jump](%s)", event.getMessage().getJumpUrl()), true)
            .addField("Old Content", oldMessage, false)
            .addField("New Content", newMessage, false)
            .setFooter("Message ID: " + event.getMessage().getId());

        event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        // grab message from map
        Message message = messages.get(event.getMessageId());

        // If it's not stored, do nothing
        if (message == null) return;

        String oldMessage = message.getContentRaw();
        User oldAuthor = message.getAuthor();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("A message was removed!")
            .addField("User", oldAuthor.getAsMention() + "\n" + oldAuthor.getId(), true)
            .addField("Channel", event.getChannel().getAsMention(), true)
            .addField("Content", oldMessage, false);

        event.getGuild().getTextChannelById(MESSAGE_EDIT_CHANNEL).sendMessageEmbeds(embed.build()).queue();
    }
}
