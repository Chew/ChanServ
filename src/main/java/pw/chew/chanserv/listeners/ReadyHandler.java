package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.chew.chanserv.util.Community;

public class ReadyHandler extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        Community.setServer(event.getJDA().getGuildById("134445052805120001"));
    }
}
