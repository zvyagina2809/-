package com.example;

import com.example.domain.JwtResponse;
import com.example.domain.User;
import com.example.domain.UserService;
import com.example.exception.AuthException;
import com.example.service.AuthService;
import com.example.service.JwtProvider;
import io.jsonwebtoken.Claims;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller("/ws/chat")
public class RegController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    public RegController(AuthService authService,
                          JwtProvider jwtProvider) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
    }

    @View("registration.html")
    @Get("/register")
    public HttpResponse<?> index(HttpServletRequest request){
        request.getSession(true);
        return HttpResponse.ok();
    }

    @Post(value = "/auth2", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> auth2(@QueryValue(value = "username") String username,
                                @QueryValue(value = "password") String password,
                                HttpServletRequest request) {


            UserService.addUser(username, password);

        Optional<User> login = UserService.getByLogin(username);
        HttpSession httpSession = request.getSession(false);
        if (login.isPresent() && httpSession != null) {
            JwtResponse jwtResponse = authService.login(new User(username, password));
            httpSession.setAttribute("access_token", jwtResponse.getAccessToken());
            httpSession.setAttribute("refresh_token", jwtResponse.getRefreshToken());
            return HttpResponse.redirect(URI.create("/ws/chat/dashboard"));
        } else {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
    }

    @Produces("text/html")
    @Error(exception = AuthException.class)
    public HttpResponse<?> error(HttpRequest<?> request, AuthException ex) {
        ModelAndView<Map<Object, Object>> modelAndView = new ModelAndView<>("error.html", CollectionUtils.mapOf("message", ex.getMessage()));
        return HttpResponse.status(HttpStatus.FORBIDDEN).body(modelAndView);
    }

}
