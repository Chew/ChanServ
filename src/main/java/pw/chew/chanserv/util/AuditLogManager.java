package pw.chew.chanserv.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import pw.chew.chanserv.listeners.BanHandler;

import java.awt.Color;
import java.util.List;

public class AuditLogManager {
    public static void logEntry(@NotNull LogType type, @NotNull User target, @Nullable Member actor, @NotNull Guild server, @Nullable String extra) {
        String fileName = "cases.txt";
        List<String> cases = FileManager.getLines(fileName);
        if(cases == null) {
            LoggerFactory.getLogger(BanHandler.class).error("??? Cases go poof?");
            return;
        }
        TextChannel auditLogChannel = server.getTextChannelById("210174983278690304");
        if (auditLogChannel == null) {
            LoggerFactory.getLogger(BanHandler.class).error("??? Audit Log channel go poof?");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(type.name + " | Case #" + cases.size());
        embed.setColor(switch (type) {
            case BAN, KICK -> Color.RED;
            case UN_BAN -> Color.GREEN;
            case MODE_CHANGE -> Color.ORANGE;
        });
        embed.addField("User", target.getAsTag() + " (" + target.getAsMention() + ")", true);
        if (type == LogType.MODE_CHANGE && extra != null)
            embed.addField("Mode", extra, true);
        if (actor == null)
            embed.addField("Responsible Staff", "[Unknown]", true);
        else
            embed.addField("Responsible Staff", actor.getAsMention(), true);
        embed.addField("Reason", "Responsible staff please add reason by `/reason case# [reason]`", true);

        auditLogChannel.sendMessageEmbeds(embed.build()).queue(msg -> FileManager.appendLine(fileName, msg.getId() + "\n"));
    }

    public static void logEntry(@NotNull LogType type, @NotNull User target, @Nullable Member actor, @NotNull Guild server) {
        logEntry(type, target, actor, server, null);
    }

    public static List<String> getEntries() {
        return FileManager.getLines("cases.txt");
    }

    public enum LogType {
        KICK("Kick"),
        BAN("Ban"),
        UN_BAN("Un-Ban"),
        MODE_CHANGE("User Mode Updated");

        public final String name;

        LogType(String string) {
            this.name = string;
        }
    }
}
