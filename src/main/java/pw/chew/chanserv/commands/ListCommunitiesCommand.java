package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pw.chew.chanserv.util.Community;

import java.awt.Color;

public class ListCommunitiesCommand extends SlashCommand {

    public ListCommunitiesCommand() {
        this.name = "listcommunities";
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.cooldown = 30;
        this.cooldownScope = CooldownScope.CHANNEL;
        this.help = "Command to list communities.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Communities Available on " + event.getGuild().getName());

        e.addField("Gaming", "**Members**: " + Community.GAMING.getMemberCount() + "\n**Join**: `/join gaming`", true);
        e.addField("Crypto", "**Members**: " + Community.CRYPTO.getMemberCount() + "\n**Join**: `/join crypto`", true);
        e.addField("Programming", "**Members**: " + Community.PROGRAMMING.getMemberCount() + "\n**Join**: `/join programming`", true);
        e.addField("Tech", "**Members**: " + Community.TECH.getMemberCount() + "\n**Join**: `/join tech`", true);
        e.addField("Music", "**Members**: " + Community.MUSIC.getMemberCount() + "\n**Join**: `/join music`", true);
        e.addField("Pets", "**Members**: " + Community.PETS.getMemberCount() + "\n**Join**: `/join pets`", true);
        e.addField("Memes", "**Members**: " + Community.MEMES.getMemberCount() + "\n**Join**: `/join memes`", true);
        e.addField("LGBT", "**Members**: " + Community.LGBT.getMemberCount() + "\n**Join**: `/join lgbt`", true);
        e.addField("Anime", "**Members**: " + Community.ANIME.getMemberCount() + "\n**Join**: `/join anime`", true);

        e.setColor(Color.GREEN);

        event.replyEmbeds(e.build()).setEphemeral(true).queue();
    }
}
