package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.chew.chanserv.util.AuditLogManager;

public class UnbanHandler extends ListenerAdapter {
    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        AuditLogManager.logEntry(AuditLogManager.LogType.UN_BAN, event.getUser(), null, event.getGuild());
    }
}
