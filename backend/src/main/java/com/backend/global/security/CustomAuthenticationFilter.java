package com.backend.global.security;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.service.UserService;
import com.backend.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final Rq rq;

    record AuthTokens(String apiKey, String accessToken) {
    }

    private AuthTokens getAuthTokensFromRequest() {
        String authorization = rq.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            String[] tokenBits = token.split(" ", 2);

            if (tokenBits.length == 2)
                return new AuthTokens(tokenBits[0], tokenBits[1]);
        }

        String apiKey = rq.getCookieValue("apiKey");
        String accessToken = rq.getCookieValue("accessToken");

        if (apiKey != null && accessToken != null)
            return new AuthTokens(apiKey, accessToken);

        return null;
    }


    private void refreshAccessToken(SiteUser siteUser) {
        String newAccessToken = userService.genAccessToken(siteUser);

        rq.setHeader("Authorization", "Bearer " + siteUser.getApiKey() + " " + newAccessToken);
        rq.setCookie("accessToken", newAccessToken);
    }

    private SiteUser refreshAccessTokenByApiKey(String apiKey) {
        Optional<SiteUser> opMemberByApiKey = userService.findByApiKey(apiKey);

        if (opMemberByApiKey.isEmpty()) {
            return null;
        }

        SiteUser member = opMemberByApiKey.get();

        refreshAccessToken(member);

        return member;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (List.of("/api/v1/members/login", "/api/v1/members/logout", "/api/v1/members/join").contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthTokens authTokens = getAuthTokensFromRequest();

        if (authTokens == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = authTokens.apiKey;
        String accessToken = authTokens.accessToken;

        SiteUser siteUser = userService.getUserFromAccessToken(accessToken);

        if (siteUser == null)
            siteUser = refreshAccessTokenByApiKey(apiKey);

        if (siteUser != null)
            rq.setLogin(siteUser);

        filterChain.doFilter(request, response);
    }

}
