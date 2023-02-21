package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pw.chew.chanserv.util.AuditLogManager;

public class AuditLogListener extends ListenerAdapter {
    @Override
    public void onGuildAuditLogEntryCreate(@NotNull GuildAuditLogEntryCreateEvent event) {
        var entry = event.getEntry();
        switch (entry.getType()) {
            case BAN -> AuditLogManager.fromAuditLog(AuditLogManager.LogType.BAN, entry);
            case KICK -> AuditLogManager.fromAuditLog(AuditLogManager.LogType.KICK, entry);
            case UNBAN -> AuditLogManager.fromAuditLog(AuditLogManager.LogType.UN_BAN, entry);
        }
    }
}
