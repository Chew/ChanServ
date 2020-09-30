package pw.chew.chanserv.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

public class MemberJoinHandler extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        TextChannel channel = event.getGuild().getTextChannelById("134445052805120001");
        if (channel == null) {
            LoggerFactory.getLogger(MemberJoinHandler.class).error("Default channel not found!");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("User Joined the Server!");
        embed.setColor(0xd084);
        embed.setDescription("Please welcome " + event.getMember().getAsMention() + " to the server!");

        embed.setFooter("Member Count: " + event.getGuild().getMemberCount());

        channel.sendMessage(embed.build()).queue();
    }
}
