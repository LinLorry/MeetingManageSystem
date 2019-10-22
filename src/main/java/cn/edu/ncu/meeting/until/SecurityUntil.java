package cn.edu.ncu.meeting.until;

import cn.edu.ncu.meeting.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUntil {
    public static User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static long getUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAuthorities();
    }
}
