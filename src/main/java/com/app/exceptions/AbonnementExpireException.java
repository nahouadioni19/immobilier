package com.app.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AbonnementExpireException extends AuthenticationException {

    public AbonnementExpireException() {
        super("ABONNEMENT_EXPIRE");
    }
}


/*public class AbonnementExpireException extends AuthenticationException {

    public AbonnementExpireException(String msg) {
        super(msg);
    }
}*/