package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pw.chew.chanserv.util.AuditLogManager;

public class BanHandler extends ListenerAdapter {
    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        AuditLogManager.logEntry(AuditLogManager.LogType.BAN, event.getUser(), null, event.getGuild());
    }
}
