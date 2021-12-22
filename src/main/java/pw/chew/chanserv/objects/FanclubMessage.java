package pw.chew.chanserv.objects;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import pw.chew.chanserv.ChanServ;

import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;

public class FanclubMessage implements Serializable {
    private final String id;
    private final String message;
    private final String channel;
    private final String author;

    public FanclubMessage(Message message) {
        this.id = message.getId();
        this.message = message.getContentRaw();
        this.channel = message.getChannel().getId();
        this.author = message.getAuthor().getId();
    }

    private FanclubMessage(String id, String message, String channel, String author) {
        this.id = id;
        this.message = message;
        this.channel = channel;
        this.author = author;
    }

    public String getContent() {
        return message;
    }

    public String getContentRaw() {
        return message;
    }

    public String getAuthorId() {
        return author;
    }

    public User getAuthor() {
        return ChanServ.getJDA().retrieveUserById(author, false).complete();
    }

    public String getId() {
        return id;
    }

    public String getChannelId() {
        return channel;
    }

    public OffsetDateTime getTimeCreated() {
        return TimeUtil.getTimeCreated(MiscUtil.parseSnowflake(id));
    }

    public static class EntrySerializer implements Serializer<FanclubMessage>, Serializable {
        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull FanclubMessage value) throws IOException {
            out.writeUTF(value.getId());
            out.writeUTF(value.getContent());
            out.writeUTF(value.getChannelId());
            out.writeUTF(value.getAuthorId());
        }

        @Override
        public FanclubMessage deserialize(@NotNull DataInput2 input, int available) throws IOException {
            return new FanclubMessage(input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF());
        }
    }
}
