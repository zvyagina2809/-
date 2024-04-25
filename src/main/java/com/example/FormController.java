package com.example;

import com.example.domain.JwtResponse;
import com.example.domain.User;
import com.example.domain.UserService;
import com.example.exception.AuthException;
import com.example.service.AuthService;
import com.example.service.JwtProvider;
import io.jsonwebtoken.Claims;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller("/ws/chat")
public class FormController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    public FormController(AuthService authService,
                          JwtProvider jwtProvider) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
    }

    @View("index.html")
    @Get("/form")
    public HttpResponse<?> index(HttpServletRequest request){
        request.getSession(true);
        return HttpResponse.ok();
    }

    @Post(value = "/auth", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> auth(@QueryValue(value = "username") String username,
                                @QueryValue(value = "password") String password,
                                HttpServletRequest request) {
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

    @View("dashboard.html")
    @Get( "/dashboard")
    public HttpResponse<?> dashboard(HttpServletRequest request){
        HttpSession httpSession = request.getSession(false);
        Optional<Object> accessToken = Optional.ofNullable(httpSession.getAttribute("access_token"));
        if (accessToken.isPresent() && jwtProvider.validateAccessToken(accessToken.get().toString())) {
            Claims accessClaims = jwtProvider.getAccessClaims(accessToken.get().toString());
            return HttpResponse.ok(CollectionUtils.mapOf("username", accessClaims.get("sub", String.class)));
        } else {
            return HttpResponse.redirect(URI.create("/form"));
        }
    }

    @Produces("text/html")
    @Error(status = HttpStatus.FORBIDDEN, exception = AuthException.class)
    public HttpResponse<?> error() {
        ModelAndView<Map<Object, Object>> modelAndView = new ModelAndView<>("error.html", Collections.emptyMap());
        return HttpResponse.status(HttpStatus.FORBIDDEN).body(modelAndView);
    }
}
