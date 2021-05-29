package pw.chew.chanserv.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Roles {
    public enum Rank {
        OPER("708085586489245757", "Oper", 7),
        NETADMIN("424381316688117760", "NetAdmin", 6),
        OWNER("708085624514543728", "Owner", 5),
        ADMIN("428372679356055574", "Admins", 4),
        OP("319161007010480129", "Ops", 3),
        HALFOP("319160664822120451", "Half ops", 2),
        VOICED("708088239310897263", "Voiced", 1),
        MEMBER("134445052805120001", "Member", 0);

        private final String roleId;
        private final String roleName;
        private final int priority;

        Rank(String roleId, String roleName, int priority) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.priority = priority;
        }

        public String getRoleId() {
            return roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public int getPriority() {
            return priority;
        }

        /**
         * Gets a list of role IDs whose priority is higher than (and include) the priority.
         * @param priority the priority to check >= to
         * @return the roles
         */
        public static String[] getRoleIdsHigherThan(int priority) {
            List<String> roles = new ArrayList<>();
            for (Rank rank : values()) {
                if (rank.getPriority() >= priority) {
                    roles.add(rank.getRoleId());
                }
            }
            return roles.toArray(new String[0]);
        }

        /**
         * Gets a list of role IDs whose priority is higher than (and include) the priority.
         * @param rank the rank to start against
         * @return a list of role IDs
         */
        public static String[] getRankIdsGreaterThanOrEqualTo(Rank rank) {
            return getRoleIdsHigherThan(rank.getPriority());
        }
        
        public Role getRole(Guild guild) {
            Role role = guild.getRoleById(getRoleId());
            if (role == null) {
                LoggerFactory.getLogger(Roles.Rank.class).error("Role is null? wtf?");
                return guild.getPublicRole();
            } else {
                return role;
            }
        }
    }

    public enum UserMode {
        k("420725473602174996", "+k", false),
        B("363809388697354253", "+B", true),
        Q("575490034061279239", "+Q", true),
        d("423987790854881290", "+d", true),
        e("424364607893930005", "+e", true),
        m("424008605902045185", "+m", true),
        n("451196023554048011", "+n", true),
        r("478028312472453122", "+r", true),
        w("437705588064124948", "+w", true),
        u("753537736253636618", "+u", true),
        b("754048518259474543", "+b", false),
        E("756549473429356555", "+E", true);

        private final String roleId;
        private final String roleName;
        private final boolean canGive;

        UserMode(String roleId, String roleName, boolean canGive) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.canGive = canGive;
        }

        public String getRoleId() {
            return roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public Role getAsRole(Guild server) {
            return server.getRoleById(getRoleId());
        }

        public boolean canGive() {
            return canGive;
        }

        public void assignMode(Member member) {
            if (canGive) {
                member.getGuild().addRoleToMember(member, getAsRole(member.getGuild())).queue();
            }
        }

        public void removeMode(Member member) {
            if (canGive) {
                member.getGuild().removeRoleFromMember(member, getAsRole(member.getGuild())).queue();
            }
        }
    }
}
