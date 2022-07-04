package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class RoryListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Ensure this is the rory fanclub
        if (!event.isFromGuild() || !event.getGuild().getId().equals("134445052805120001")) return;

        if (event.getMessage().getContentRaw().toLowerCase(Locale.ROOT).contains("rory")) {
            addReaction(event.getMessage(), event);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        // Ensure this is the rory fanclub
        if (!event.isFromGuild() || !event.getGuild().getId().equals("134445052805120001")) return;

        if (event.getMessage().getContentRaw().toLowerCase(Locale.ROOT).contains("rory")) {
            addReaction(event.getMessage(), event);
        }
    }

    /**
     * Adds a Rory reaction to the message
     * @param message The message
     * @param event The event to get the server from
     */
    public void addReaction(Message message, GenericMessageEvent event) {
        // Ensure this is the rory fanclub
        if (!event.isFromGuild() || !event.getGuild().getId().equals("134445052805120001")) return;
        // Get all emotes
        List<RichCustomEmoji> emotes = event.getGuild().getEmojiCache().stream().filter(emote -> emote.getName().toLowerCase(Locale.ROOT).contains("rory")).toList();

        // Get a random emote from the list using a random number generator
        int random = (int) (Math.random() * emotes.size());
        Emoji emote = emotes.get(random);
        message.addReaction(emote).queue();
    }
}
