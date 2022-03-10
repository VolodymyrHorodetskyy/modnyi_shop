package shop.chobitok.modnyi.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Role;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userService.getUserByName(name);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + name);
        }
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                toSimpleGrantedAuthorityList(user.getRoles()));
    }

    private List<SimpleGrantedAuthority> toSimpleGrantedAuthorityList(List<Role> roles) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorityList = new ArrayList<>();
        for (Role role : roles) {
            simpleGrantedAuthorityList.add(new SimpleGrantedAuthority(role.name()));
        }
        return simpleGrantedAuthorityList;
    }
}
