package shop.chobitok.modnyi.controller;


import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserLoggedIn;
import shop.chobitok.modnyi.entity.request.LogInRequest;
import shop.chobitok.modnyi.service.UserService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping
    public UserLoggedIn logIn(@RequestBody LogInRequest request) {
        return userService.logIn(request);
    }

}
