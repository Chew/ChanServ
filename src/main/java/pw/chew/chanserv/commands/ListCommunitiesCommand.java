package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import pw.chew.chanserv.util.Community;

import java.awt.Color;

public class ListCommunitiesCommand extends Command {

    public ListCommunitiesCommand() {
        this.name = "listcommunities";
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.cooldown = 30;
        this.cooldownScope = CooldownScope.CHANNEL;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().delete().queue();
        EmbedBuilder e = new EmbedBuilder();
        e.setTitle("Communities Available on " + event.getGuild().getName());

        e.addField("Gaming", "**Members**: " + Community.GAMING.getMemberCount() + "\n**Join**: `;j gaming`", true);
        e.addField("Crypto", "**Members**: " + Community.CRYPTO.getMemberCount() + "\n**Join**: `;j crypto`", true);
        e.addField("Programming", "**Members**: " + Community.PROGRAMMING.getMemberCount() + "\n**Join**: `;j programming`", true);
        e.addField("Tech", "**Members**: " + Community.TECH.getMemberCount() + "\n**Join**: `;j tech`", true);
        e.addField("Music", "**Members**: " + Community.MUSIC.getMemberCount() + "\n**Join**: `;j music`", true);
        e.addField("Pets", "**Members**: " + Community.PETS.getMemberCount() + "\n**Join**: `;j pets`", true);
        e.addField("Memes", "**Members**: " + Community.MEMES.getMemberCount() + "\n**Join**: `;j memes`", true);
        e.addField("LGBT", "**Members**: " + Community.LGBT.getMemberCount() + "\n**Join**: `;j lgbt`", true);
        e.addField("Anime", "**Members**: " + Community.ANIME.getMemberCount() + "\n**Join**: `;j anime`", true);

        e.setColor(Color.GREEN);

        event.reply(e.build());
    }
}
