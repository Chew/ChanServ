package pw.chew.chanserv.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import pw.chew.chanserv.listeners.MessageModificationHandler;
import pw.chew.chanserv.objects.FanclubMessage;
import pw.chew.chewbotcca.util.MiscUtil;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UwUStatsCommand extends SlashCommand {
    public UwUStatsCommand() {
        this.name = "uwustats";
        this.help = "uwu stats";
        this.children = new SlashCommand[]{new TopUwUStatsSubCommand(), new UwUGraphSubCommand()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        // unused
    }

    public class TopUwUStatsSubCommand extends SlashCommand {
        public TopUwUStatsSubCommand() {
            this.name = "top";
            this.help = "top uwuers";
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.replyEmbeds(buildLeaderboardEmbed(event)).queue();
        }
    }

    public static class UwUGraphSubCommand extends SlashCommand {
        public UwUGraphSubCommand() {
            this.name = "graph";
            this.help = "uwu graph";
            this.cooldown = 60;
            this.options = Collections.singletonList(
                new OptionData(OptionType.STRING, "kind", "The kind of graph")
                    .addChoice("UwUs Per Day", "day")
                    .addChoice("Cumulative UwUs", "cumulative")
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            var choice = event.getOption("kind");
            String name = choice == null ? "UwUs Per Day" : choice.getName();
            String kind = choice == null ? "day" : choice.getAsString();

            // Gather a hash map of date to uwu count
            Map<String, Integer> uwuCounts = new TreeMap<>();
            var cache = MessageModificationHandler.getCache();

            for (FanclubMessage message : cache.values()) {
                if (!message.getChannelId().equals("751903362794127470")) continue;
                String date = message.getTimeCreated().toLocalDate().toString();
                // Get the amount of days since 09/05/2020
                if (uwuCounts.containsKey(date)) {
                    uwuCounts.put(date, uwuCounts.get(date) + 1);
                } else {
                    uwuCounts.put(date, 1);
                }
            }

            if (kind.equals("cumulative")) {
                // Iterate over each entry in the map, and use the cumulative sum to get the total uwus on that day
                int total = 0;
                for (String date : uwuCounts.keySet()) {
                    total += uwuCounts.get(date);
                    uwuCounts.put(date, total);
                }
            }

            double[] x = uwuCounts.keySet().stream()
                .map(value -> LocalDate.parse(value).until(LocalDate.of(2020, 9, 5), ChronoUnit.DAYS) * -1)
                .mapToDouble(value -> value)
                .toArray();
            double[] y = uwuCounts.values().stream().mapToDouble(value -> value).toArray();

            // Build a line graph using XChart
            XYChart chart = QuickChart.getChart(name + " Graph", "Day", "UwUs", "uwus/day", x, y);
            chart.getStyler().setChartBackgroundColor(new Color(0x36393f));
            chart.getStyler().setYAxisTickLabelsColor(Color.WHITE);
            chart.getStyler().setXAxisTickLabelsColor(Color.WHITE);
            chart.getStyler().setChartFontColor(Color.WHITE);
            chart.getStyler().setPlotBackgroundColor(new Color(0x2f3136));
            chart.getStyler().setSeriesColors(new Color[]{new Color(0x5865F2)});
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setPlotGridLinesColor(new Color(0x8e9297));
            chart.getStyler().setChartTitleFont(new Font("Whitney", Font.BOLD, 20));
            chart.getStyler().setBaseFont(new Font("Whitney", Font.PLAIN, 14));
            chart.getStyler().setxAxisTickLabelsFormattingFunction(val -> LocalDate.of(2020, 9, 5).plusDays(val.intValue()).toString());

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(BitmapEncoder.getBufferedImage(chart), "png", os);
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            event.reply("Here's your graph!").addFile(is, "bruh.png").queue();
        }
    }

    private MessageEmbed buildLeaderboardEmbed(SlashCommandEvent event) {
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

        most = MiscUtil.sortByValue(most);

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

        return new EmbedBuilder()
            .setTitle("Top 10 UwU Leaderboard")
            .setDescription(String.join("\n", output))
            .build();
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
}
