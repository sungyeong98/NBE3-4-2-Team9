package com.backend.global.security.handler;

import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.response.GenericResponse;
import com.backend.standard.util.AuthResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) throws IOException {
        AuthResponseUtil.failLogin(
                response,
                GenericResponse.of(false, GlobalErrorCode.KAKAO_LOGIN_FAIL.getCode(), GlobalErrorCode.KAKAO_LOGIN_FAIL.getMessage()),
                HttpServletResponse.SC_BAD_REQUEST,
                objectMapper
        );
    }

}
