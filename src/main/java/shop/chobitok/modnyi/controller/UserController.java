package shop.chobitok.modnyi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserLoggedIn;
import shop.chobitok.modnyi.entity.request.CheckTokenRequest;
import shop.chobitok.modnyi.entity.request.JwtRequest;
import shop.chobitok.modnyi.entity.request.LogInRequest;
import shop.chobitok.modnyi.entity.response.JwtResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.security.JwtTokenUtil;
import shop.chobitok.modnyi.security.JwtUserDetailsService;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.service.StatisticService;
import shop.chobitok.modnyi.service.UserEfficiencyService;
import shop.chobitok.modnyi.service.UserService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserEfficiencyService userEfficiencyService;
    private final CheckerService checkerService;
    private final StatisticService statisticService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, UserEfficiencyService userEfficiencyService, CheckerService checkerService, StatisticService statisticService, JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userEfficiencyService = userEfficiencyService;
        this.checkerService = checkerService;
        this.statisticService = statisticService;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
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
        return statisticService.checkMistakesInOrder(userId, from, to);
    }

    @GetMapping("checkAppOrdersBecameOrders")
    public StringResponse checkAppOrdersBecameOrders(@RequestParam(required = false) String from,
                                                     @RequestParam(required = false) String to,
                                                     @RequestParam(required = false) Long userId) {
        if (userId == 1L) {
            return checkerService.checkAppOrdersBecameOrdersForAllUsers(from, to);
        } else {
            return checkerService.checkAppOrdersBecameOrders(userId, from, to);
        }
    }

    @PostMapping(value = "/authenticate")
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        User user = userService.getUserByName(authenticationRequest.getUsername());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return new JwtResponse(user.getId(), token);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping(value = "/isTokenExpired")
    public ResponseEntity isTokenExpired(@RequestBody CheckTokenRequest jwtRequest) {
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.ok();
        if (jwtTokenUtil.isTokenExpired(jwtRequest.getJwtToken())) {
            responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        }
        return responseEntity.build();
    }
}