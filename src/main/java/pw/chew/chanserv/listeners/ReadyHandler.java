package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

public class ReadyHandler extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        // Get server
        Guild fanclub = event.getJDA().getGuildById("134445052805120001");
        if (fanclub == null) {
            LoggerFactory.getLogger(ReadyHandler.class).error("Could not find server!");
            event.getJDA().shutdown();
        }
    }
}
