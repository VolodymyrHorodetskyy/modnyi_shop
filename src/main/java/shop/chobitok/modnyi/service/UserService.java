package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserLoggedIn;
import shop.chobitok.modnyi.entity.request.LogInRequest;
import shop.chobitok.modnyi.repository.UserLoggedInRepository;
import shop.chobitok.modnyi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static shop.chobitok.modnyi.util.DateHelper.makeDateBeginningOfDay;
import static shop.chobitok.modnyi.util.DateHelper.makeDateEndOfDay;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserLoggedInRepository userLoggedInRepository;
    private AppOrderService appOrderService;

    public UserService(UserRepository userRepository, UserLoggedInRepository userLoggedInRepository, AppOrderService appOrderService) {
        this.userRepository = userRepository;
        this.userLoggedInRepository = userLoggedInRepository;
        this.appOrderService = appOrderService;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public UserLoggedIn logIn(LogInRequest request) {
        User user = userRepository.findOneByNameAndPassword(request.getName(), request.getPassword());
        LocalDateTime beginningOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        UserLoggedIn userLoggedIn = null;
        if (user != null) {
            userLoggedIn = userLoggedInRepository.findOneByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqualAndUserId(
                    beginningOfDay, endOfDay, user.getId());
            if (userLoggedIn == null) {
                userLoggedIn = userLoggedInRepository.save(new UserLoggedIn(user));
                setShouldBeProcessed(beginningOfDay, endOfDay);
            } else {
                if (!userLoggedIn.isActive()) {
                    setShouldBeProcessed(beginningOfDay, endOfDay);
                    userLoggedIn.setActive(true);
                    userLoggedIn = userLoggedInRepository.save(userLoggedIn);
                }
            }
        }
        return userLoggedIn;
    }

    public void setShouldBeProcessed(LocalDateTime beginningOfDay, LocalDateTime endOfDay) {
        appOrderService.setShouldBeProcessedAppOrderDateAndAssignToUser(
                userLoggedInRepository.findAllByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
                        beginningOfDay, endOfDay
                ).stream().map(userLoggedIn1 -> userLoggedIn1.getUser()).collect(toList()));
    }

    public UserLoggedIn checkIfUserIsLogged(Long id) {
        UserLoggedIn userLoggedIn = userLoggedInRepository
                .findOneByCreatedDateGreaterThanEqualAndCreatedDateLessThanEqualAndUserId(makeDateBeginningOfDay(LocalDateTime.now()),
                        makeDateEndOfDay(LocalDateTime.now()), id);
        if (userLoggedIn != null && userLoggedIn.isActive()) {
            return userLoggedIn;
        }
        return null;
    }

    public Object getFirstItemToProcessByUserId(Long userId) {
        return appOrderService.findFirstShouldBeProcessedAppOrderByUserId(userId);
    }

    public void makeUsersInactive() {
        List<UserLoggedIn> allLoggedActiveUsers = userLoggedInRepository.findAllByActiveTrue();
        for (UserLoggedIn userLoggedIn : allLoggedActiveUsers) {
            userLoggedIn.setActive(false);
        }
        userLoggedInRepository.saveAll(allLoggedActiveUsers);
    }

}
