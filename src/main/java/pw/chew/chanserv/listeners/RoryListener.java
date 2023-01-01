package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
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

        this.handleMessage(event.getMessage());
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        // Ensure this is the rory fanclub
        if (!event.isFromGuild() || !event.getGuild().getId().equals("134445052805120001")) return;

        this.handleMessage(event.getMessage());
    }

    /**
     * Handles a Rory Fanclub message
     *
     * @param msg the message to handle
     */
    public void handleMessage(Message msg) {
        String message = msg.getContentRaw().toLowerCase(Locale.ROOT);
        if (message.contains("rory")) {
            addReaction(msg, "rory");
        } else if (message.contains("lory") || message.contains("lorelai")) {
            addReaction(msg, "lory");
        }
    }

    /**
     * Adds a Rory reaction to the message
     * @param message The message
     */
    public void addReaction(Message message, String reaction) {
        // Get all emotes
        List<RichCustomEmoji> emotes = message.getGuild().getEmojiCache().stream().filter(emote -> emote.getName().toLowerCase(Locale.ROOT).contains(reaction)).toList();

        // Get a random emote from the list using a random number generator
        int random = (int) (Math.random() * emotes.size());
        Emoji emote = emotes.get(random);
        message.addReaction(emote).queue();
    }
}
