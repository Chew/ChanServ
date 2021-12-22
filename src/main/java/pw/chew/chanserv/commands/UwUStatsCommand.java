package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import pw.chew.chanserv.listeners.MessageModificationHandler;
import pw.chew.chanserv.objects.FanclubMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UwUStatsCommand extends SlashCommand {
    public UwUStatsCommand() {
        this.name = "uwustats";
        this.help = "uwu stats";
        this.guildOnly = false;
        this.cooldown = 60;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Map<String, Integer> most = new HashMap<>();

        var cache = MessageModificationHandler.getCache();

        int total = 0;

        for (FanclubMessage message : cache.values()) {
            if (!message.getChannelId().equals("751903362794127470")) continue;

            Integer amount = most.getOrDefault(message.getAuthorId(), 0);
            amount++;
            most.put(message.getAuthorId(), amount);
            total++;
        }

        most = sortByValue(most);

        List<String> output = new ArrayList<>();

        boolean iAmTop10 = false;
        int i = 0;
        for (String userId : most.keySet()) {
            i++;
            User user = event.getJDA().getUserById(userId);
            String tag;
            if (user == null) {
                tag = "Unknown User";
            } else {
                tag = user.getAsTag();

                if (user.getIdLong() == event.getUser().getIdLong() && i <= 10) {
                    iAmTop10 = true;
                    output.add("**#" + i + ": " + tag + " - " + most.get(userId) + " uwus**");
                    continue;
                }
            }

            output.add("#" + i + ": " + tag + " - " + most.get(userId) + " uwus");
        }

        output = output.subList(0, 10);

        int your = most.getOrDefault(event.getUser().getId(), 0);

        if (!iAmTop10) {
            output.add("...");
            String tag = event.getUser().getAsTag();
            String userId = event.getUser().getId();
            int position = getPosition(most, userId) + 1;
            if (your == 0) {
                output.add("**#" + (i + 1) + ": " + tag + " - " + 0 + " uwus**");
            } else {
                output.add("**#" + position + ": " + tag + " - " + most.get(userId) + " uwus**");
            }
        }

        float channelAgeInDays = (float) ((System.currentTimeMillis() - event.getJDA().getTextChannelById("751903362794127470").getTimeCreated().toInstant().toEpochMilli()) / 86400000);
        float uwusPerDay = total / channelAgeInDays;

        output.add(0, "Total: " + total);
        output.add(1, "UwU/day: " + uwusPerDay);
        output.add(2, "");

        MessageEmbed embed = new EmbedBuilder()
            .setTitle("Top 10 UwU Leaderboard")
            .setDescription(String.join("\n", output))
            .build();

        event.replyEmbeds(embed).queue();
    }

    public int getPosition(Map<String, Integer> map, String key) {
        int i = 0;
        for (String k : map.keySet()) {
            if (k.equals(key)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
