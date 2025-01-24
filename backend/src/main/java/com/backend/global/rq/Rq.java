package com.backend.global.rq;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.Optional;

@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {

    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final UserService userService;

    public void setLogin(SiteUser siteUser) {
        // TODO (추후 작업 예정)
    }

    // TODO
//    public SiteUser getUser() {
//        return Optional.ofNullable(
//                        SecurityContextHolder
//                                .getContext()
//                                .getAuthentication()
//                )
//                .map(Authentication::getPrincipal)
//                .filter(principal -> principal instanceof SecurityUser)
//                .map(principal -> (SecurityUser) principal)
//                .map(securityUser -> new Member(securityUser.getId(), securityUser.getUsername()))
//                .orElse(null);
//    }

    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain("localhost")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .build();
        resp.addHeader("Set-Cookie", cookie.toString());
    }

    public String getCookieValue(String name) {
        return Optional
                .ofNullable(req.getCookies())
                .stream()
                .flatMap(cookies -> Arrays.stream(cookies))
                .filter(cookie -> cookie.getName().equals(name))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);
    }

    public void deleteCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .path("/")
                .domain("localhost")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .maxAge(0)
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }

    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }

    public String getHeader(String name) {
        return req.getHeader(name);
    }

    // TODO
//    public Optional<SiteUser> findByActor() {
//        SiteUser user = getActor();
//
//        if (user == null) return Optional.empty();
//
//        return userService.findById(user.getId());
//    }

}
