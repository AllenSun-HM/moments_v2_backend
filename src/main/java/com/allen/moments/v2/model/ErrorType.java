package com.allen.moments.v2.model;

import java.io.Serializable;

public enum ErrorType implements Serializable {

    // user related
    UNKNOWN_ERROR(100000, "unknown error"),
    USER_ALREADY_REGISTERED(100001, "user already registered"),
    USER_ALREADY_FOLLOWED(100002, "user already followed"),
    PASSWORD_ACCOUNT_MISMATCH(100003, "Password incorrect"),
    NO_FOLLOWING_RELATION(200001, "no following relationship exists"),
    USER_UNIDENTIFIED(200002, "cannot find user"),

    DIRTY_DATA(900000, "dirty data exists"),
    DML_ERR(900001, "DML error");
    public final int errNo;
    public final String message;

    ErrorType(int errNo, String message) {
        this.errNo = errNo;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + errNo +
                ", message='" + message + '\'' +
                '}';
    }
}

