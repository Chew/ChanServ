package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.CooldownScope;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.Checks;
import pw.chew.chanserv.util.MiscUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class RandomColorCommand extends SlashCommand {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    public RandomColorCommand() {
        this.name = "randomcolor";
        this.cooldownScope = CooldownScope.USER;
        this.cooldown = 60 * 60; // seconds * minutes
        this.help = "Get a random color for your name (can only be used every hour)";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Checks.notNull(event.getGuild(), "Guild"); // Slash commands are server-specific
        List<Role> role = event.getGuild().getRolesByName(event.getUser().getId(), true);
        // Generate a random HSV
        float hue = RANDOM.nextFloat();
        float saturation = 0.5f + RANDOM.nextFloat() / 2.0f;
        float brightness = 0.5f + RANDOM.nextFloat() / 2.0f;
        // Convert to RGB
        Color color = Color.getHSBColor(hue, saturation, brightness);
        Role current;
        String oldColor;
        if (role.isEmpty()) {
            current = event.getGuild().createRole().setName(event.getUser().getId()).setColor(color)
                .reason("Creating color role for " + event.getUser().getAsTag()).complete();
            oldColor = "absolutely nothing";
            event.getGuild().modifyRolePositions(false).selectPosition(current).moveTo(4).complete();
        } else {
            current = role.get(0);
            oldColor = "#" + Integer.toHexString(current.getColor().getRGB()).substring(2);
            current.getManager().setColor(color).reason("Updating color role for " + event.getUser().getAsTag() + " from " + oldColor).complete();
        }
        // Check for similar colors
        List<String> similars = new ArrayList<>();
        for (Role r : event.getGuild().getRoles()) {
            Color c = r.getColor();
            if (c == null) continue;

            float percentage = MiscUtil.colorSimilarityPercentage(color, c);
            String per = pw.chew.chewbotcca.util.MiscUtil.formatPercent(percentage);

            if (percentage > 0.9) {
                similars.add(r.getAsMention() + " (" + per + ")");
            }
        }

        if (!similars.isEmpty()) {
            similars.add(0, "\n\nWow! Your color is very similar to the following roles:");
        }

        if (!event.getMember().getRoles().contains(current)) {
            event.getGuild().addRoleToMember(event.getMember(), current).complete();
        }
        event.reply("Changed your random color from " + oldColor + " to #" + Integer.toHexString(color.getRGB()).substring(2) + " successfully!"
                + String.join("\n", similars))
            .setEphemeral(true).queue();

        // If they're a booster, they only need a 60s cooldown
        if (event.getMember().isBoosting()) {
            event.getClient().applyCooldown(this.getCooldownKey(event), 60);
        }
    }
}
