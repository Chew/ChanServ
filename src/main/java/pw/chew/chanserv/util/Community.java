package pw.chew.chanserv.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public enum Community {
    GAMING("424379734483533845"),
    CRYPTO("424379772219424779"),
    PROGRAMMING("424379786006364161"),
    TECH("424379920924278784"),
    MUSIC("424379929539641345"),
    PETS("424379940604084224"),
    MEMES("424379961256968192"),
    LGBT("424663031482679316"),
    ANIME("425422889374842891");

    public final String id;
    public static Guild server;
    public static String categoryId = "424379618825338890";

    Community(String id) {
        this.id = id;
    }

    public static void setServer(@NotNull Guild server) {
        Community.server = server;
    }

    /**
     * Gets the TextChannel for this community
     * @return the corresponding channel for this community.
     */
    public TextChannel getAsChannel() {
        return server.getTextChannelById(id);
    }

    /**
     * The members currently in this community. This is technically every "Member" Permission Override
     * @return the community's member count
     */
    public int getMemberCount() {
        return getAsChannel().getMemberPermissionOverrides().size();
    }

    /**
     * Adds a member to this channel, grants them "Read Messages"
     * @param member the member to add
     */
    public void addMember(Member member) {
        getAsChannel().createPermissionOverride(member).setAllow(Permission.MESSAGE_READ).complete();
        getAsChannel().sendMessage(member.getAsMention() + " joined the channel!").queue();
    }

    /**
     * Removes a member from this channel, revokes the permission override
     * @param member the member to remove
     */
    public void removeMember(Member member) {
        try {
            getAsChannel().getPermissionOverride(member).delete().complete();
            getAsChannel().sendMessage(member.getAsMention() + " left the channel!").queue();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Could not find permission override!");
        }
    }
}
