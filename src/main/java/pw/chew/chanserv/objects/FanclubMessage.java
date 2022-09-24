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

public record FanclubMessage(String id, String content, String channelId, String authorId) implements Serializable {
    public FanclubMessage(Message message) {
        this(message.getId(), message.getContentRaw(), message.getChannel().getId(), message.getAuthor().getId());
    }

    public User getAuthor() {
        return ChanServ.getJDA().retrieveUserById(authorId).complete();
    }

    public OffsetDateTime getTimeCreated() {
        return TimeUtil.getTimeCreated(MiscUtil.parseSnowflake(id));
    }

    public static class EntrySerializer implements Serializer<FanclubMessage>, Serializable {
        @Override
        public void serialize(@NotNull DataOutput2 out, @NotNull FanclubMessage value) throws IOException {
            out.writeUTF(value.id());
            out.writeUTF(value.content());
            out.writeUTF(value.channelId());
            out.writeUTF(value.authorId());
        }

        @Override
        public FanclubMessage deserialize(@NotNull DataInput2 input, int available) throws IOException {
            return new FanclubMessage(input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF());
        }
    }
}
