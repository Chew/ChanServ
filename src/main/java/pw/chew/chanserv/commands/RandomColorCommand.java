package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class RandomColorCommand extends Command {

    public RandomColorCommand() {
        this.name = "randomcolor";
        this.cooldownScope = CooldownScope.USER;
        this.guildOnly = true;
        this.cooldown = 21600;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();
        List<Role> role = event.getGuild().getRolesByName(event.getAuthor().getId(), true);
        Random r = new Random();
        int color = r.nextInt((16777215) + 1);
        Role current;
        if (role.isEmpty()) {
            current = event.getGuild().createRole().setName(event.getAuthor().getId()).setColor(color).complete();
            event.getGuild().modifyRolePositions(false).selectPosition(current).moveTo(3).complete();
        } else {
            current = role.get(0);
            current.getManager().setColor(color).complete();
        }
        if (!event.getMember().getRoles().contains(current)) {
            event.getGuild().addRoleToMember(event.getMember(), current).complete();
        }
        event.reply("Set your role color successfully!");
    }
}
