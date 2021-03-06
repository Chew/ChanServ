package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;
import java.util.Random;

public class RandomColorCommand extends SlashCommand {

    public RandomColorCommand() {
        this.name = "randomcolor";
        this.cooldownScope = CooldownScope.USER;
        this.guildOnly = true;
        this.guildId = "134445052805120001";
        this.cooldown = 21600;
        this.help = "Get a random color for your name (can only be used every 6 hours)";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        List<Role> role = event.getGuild().getRolesByName(event.getUser().getId(), true);
        Random r = new Random();
        int color = r.nextInt((16777215) + 1);
        Role current;
        if (role.isEmpty()) {
            current = event.getGuild().createRole().setName(event.getUser().getId()).setColor(color).complete();
            event.getGuild().modifyRolePositions(false).selectPosition(current).moveTo(3).complete();
        } else {
            current = role.get(0);
            current.getManager().setColor(color).complete();
        }
        if (!event.getMember().getRoles().contains(current)) {
            event.getGuild().addRoleToMember(event.getMember(), current).complete();
        }
        event.reply("Set your role color successfully!").setEphemeral(true).queue();
    }
}
