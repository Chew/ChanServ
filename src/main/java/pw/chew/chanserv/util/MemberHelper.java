package pw.chew.chanserv.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class MemberHelper {
    public static Roles.Rank getRank(Member member) {
        if (member.getRoles().isEmpty())
            return Roles.Rank.MEMBER;

        List<Role> roles = member.getRoles();
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.OPER.getRoleId()))) return Roles.Rank.OPER;
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.NETADMIN.getRoleId()))) return Roles.Rank.NETADMIN;
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.OWNER.getRoleId()))) return Roles.Rank.OWNER;
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.ADMIN.getRoleId()))) return Roles.Rank.ADMIN;
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.OP.getRoleId()))) return Roles.Rank.OP;
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.HALFOP.getRoleId()))) return Roles.Rank.HALFOP;
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.VOICED.getRoleId()))) return Roles.Rank.VOICED;

        return Roles.Rank.MEMBER;
    }

    public static List<String> getUserModes(Member member) {
        List<String> modes = new ArrayList<>();
        List<Role> roles = member.getRoles();

        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.OPER.getRoleId()))) modes.add("Y");
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.OWNER.getRoleId()))) modes.add("q");
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.ADMIN.getRoleId()))) modes.add("a");
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.OP.getRoleId()))) modes.add("o");
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.HALFOP.getRoleId()))) modes.add("h");
        if (roles.contains(member.getGuild().getRoleById(Roles.Rank.VOICED.getRoleId()))) modes.add("v");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.B.getRoleId()))) modes.add("B");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.Q.getRoleId()))) modes.add("Q");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.k.getRoleId()))) modes.add("k");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.d.getRoleId()))) modes.add("d");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.m.getRoleId()))) modes.add("m");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.e.getRoleId()))) modes.add("e");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.w.getRoleId()))) modes.add("w");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.n.getRoleId()))) modes.add("n");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.r.getRoleId()))) modes.add("r");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.b.getRoleId()))) modes.add("b");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.u.getRoleId()))) modes.add("u");
        if (roles.contains(member.getGuild().getRoleById(Roles.UserMode.E.getRoleId()))) modes.add("E");

        return modes;
    }

    public boolean hasUserMode(Member member, Roles.UserMode mode) {
        return member.getRoles().contains(member.getGuild().getRoleById(mode.getRoleId()));
    }
}
