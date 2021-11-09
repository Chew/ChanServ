package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class RoryListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // Ensure this is the rory fanclub
        if (!event.getGuild().getId().equals("134445052805120001")) return;

        if (event.getMessage().getContentRaw().toLowerCase(Locale.ROOT).contains("rory")) {
            // Get all emotes
            List<Emote> emotes = event.getGuild().getEmoteCache().stream().filter(emote -> emote.getName().toLowerCase(Locale.ROOT).contains("rory")).collect(java.util.stream.Collectors.toList());

            // Get a random emote from the list using a random number generator
            int random = (int) (Math.random() * emotes.size());
            Emote emote = emotes.get(random);
            event.getMessage().addReaction(String.format("%s:%s", emote.getName(), emote.getId())).queue();
        }
    }
}
