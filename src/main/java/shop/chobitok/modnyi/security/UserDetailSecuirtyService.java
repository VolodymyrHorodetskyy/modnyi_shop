package shop.chobitok.modnyi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.repository.UserRepository;

@Service
public class UserDetailSecuirtyService implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailSecuirtyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new UserPrincipal(userRepository.findByName(s));
    }

}
