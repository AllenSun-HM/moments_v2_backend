package com.allen.moments.v2.utils;

import com.allen.moments.v2.model.ErrorType;

public class ApplicationException extends RuntimeException {
    private String message;
    private Object[] args;
    private int errNo = ErrorType.UNKNOWN_ERROR.errNo; // default err_no represents unknown error

    public ApplicationException(String message)
    {
        super(message);
        this.message = message;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message,  Object ... args)
    {
        super(message);
        this.args = args;
    }

    public ApplicationException(int errNo, String message) {
        super(message);
        this.errNo = errNo;
    }

    public ApplicationException(String message, Throwable cause, Object ... args)
    {
        super(message, cause);
        this.args = args;
    }

    @Override
    public String getMessage()
    {
        return this.message;
    }

    public Object[] getArgs()
    {
        return this.args;
    }

    public int getErrNo() {
        return this.errNo;
    }
}


