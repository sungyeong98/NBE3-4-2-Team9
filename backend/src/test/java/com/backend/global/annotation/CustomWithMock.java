package com.backend.global.annotation;

import com.backend.global.config.CustomWithSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomWithSecurityContextFactory.class)
public @interface CustomWithMock {

    long id() default 1L;

    String email() default "test@test.com";

    String password() default "test";

    String name() default "test";

    String role() default "ROLE_USER";

}
