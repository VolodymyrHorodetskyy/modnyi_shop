package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserLoggedIn;
import shop.chobitok.modnyi.entity.request.LogInRequest;
import shop.chobitok.modnyi.repository.UserLoggedInRepository;
import shop.chobitok.modnyi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserLoggedInRepository userLoggedInRepository;

    public UserService(UserRepository userRepository, UserLoggedInRepository userLoggedInRepository) {
        this.userRepository = userRepository;
        this.userLoggedInRepository = userLoggedInRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public UserLoggedIn logIn(LogInRequest request) {
        User user = userRepository.findOneByIdAndPassword(request.getId(), request.getPassword());
        LocalDateTime beginningOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        UserLoggedIn userLoggedIn = null;
        if (user != null) {
            userLoggedIn = userLoggedInRepository.findOneByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
                    beginningOfDay, endOfDay);
            if (userLoggedIn == null) {
                userLoggedIn = userLoggedInRepository.save(new UserLoggedIn(user));
            } else {
                userLoggedIn.setActive(true);
                userLoggedIn = userLoggedInRepository.save(userLoggedIn);
            }
        }
        return userLoggedIn;
    }

    public boolean checkIfUserIsLogged(Long id) {
        boolean logged = false;
        UserLoggedIn userLoggedIn = userLoggedInRepository.findById(id).orElse(null);
        if (userLoggedIn != null) {
            logged = userLoggedIn.isActive();
        }
        return logged;
    }

}
