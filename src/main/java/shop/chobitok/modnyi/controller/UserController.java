package shop.chobitok.modnyi.controller;


import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserLoggedIn;
import shop.chobitok.modnyi.entity.request.LogInRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.UserEfficiencyService;
import shop.chobitok.modnyi.service.UserService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private UserEfficiencyService userEfficiencyService;
    private CheckerService checkerService;

    public UserController(UserService userService, UserEfficiencyService userEfficiencyService, CheckerService checkerService) {
        this.userService = userService;
        this.userEfficiencyService = userEfficiencyService;
        this.checkerService = checkerService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping
    public UserLoggedIn logIn(@RequestBody LogInRequest request) {
        return userService.logIn(request);
    }

    @GetMapping("userLoggedIn")
    public UserLoggedIn checkIfUserLoggedIn(@RequestParam Long id) {
        return userService.checkIfUserIsLogged(id);
    }

    @GetMapping("getFirstItemShouldBeProcessed")
    public Object getFirstItemShouldBeProcessed(@RequestParam Long userId) {
        return userService.getFirstItemToProcessByUserId(userId);
    }

    @GetMapping("getUserEfficiency")
    public StringResponse getUserEfficiency(@RequestParam(required = false) String from, @RequestParam Long userId) {
        return userEfficiencyService.showUserEfficiency(from, userId);
    }

    @GetMapping("getOrderMistakes")
    public StringResponse getOrderMistakes(@RequestParam(required = false) String from,
                                           @RequestParam(required = false) String to,
                                           @RequestParam(required = false) Long userId) {
        return checkerService.checkMistakesInOrder(userId, from, to);
    }

    @GetMapping("checkAppOrdersBecameOrders")
    public StringResponse checkAppOrdersBecameOrders(@RequestParam(required = false) String from,
                                                     @RequestParam(required = false) String to,
                                                     @RequestParam(required = false) Long userId) {
        return checkerService.checkAppOrdersBecameOrders(userId, from, to);
    }
}