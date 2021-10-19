package pw.chew.chanserv.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * The MemberHelper class contains numerous methods to help with retrieving specific info about Members.
 */
public class MemberHelper {
    /**
     * Gets a user's rank in the fanclub.
     *
     * @param member The member
     * @return Their rank
     */
    public static Roles.Rank getRank(Member member) {
        if (member.getRoles().isEmpty())
            return Roles.Rank.MEMBER;

        Guild server = member.getGuild();

        List<Role> roles = member.getRoles();
        for (Roles.Rank rank : Roles.Rank.values()) {
            if (rank.containsRole(roles, server)) return rank;
        }

        return Roles.Rank.MEMBER;
    }

    /**
     * Gets a list containing all this member's user modes.
     *
     * @param member The member
     * @return The modes
     */
    public static List<String> getUserModes(Member member) {
        List<String> modes = new ArrayList<>();
        List<Role> roles = member.getRoles();

        Guild server = member.getGuild();

        for (Roles.UserMode mode : Roles.UserMode.values()) {
            if (mode.containsRole(roles, server)) modes.add(mode.name());
        }

        return modes;
    }
}
