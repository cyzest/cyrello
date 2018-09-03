package com.cyzest.cyrello.dto;

import com.cyzest.cyrello.dao.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class DefaultAuthUser extends org.springframework.security.core.userdetails.User {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    private static final boolean ENABLED = true;
    private static final boolean ACCOUNT_NON_EXPIRED = true;
    private static final boolean CREDENTIALS_NON_EXPIRED = true;
    private static final boolean ACCOUNT_NON_LOCKED = true;

    public DefaultAuthUser(User user) {
        super(user.getId(), user.getPassword(),
                ENABLED, ACCOUNT_NON_EXPIRED, CREDENTIALS_NON_EXPIRED, ACCOUNT_NON_LOCKED,
                Collections.singletonList(new SimpleGrantedAuthority(DEFAULT_ROLE))
        );
    }

}
