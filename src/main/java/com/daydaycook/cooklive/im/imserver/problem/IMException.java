package com.daydaycook.cooklive.im.imserver.problem;

/**
 * Created by creekhan on 7/13/16.
 */
public class IMException extends RuntimeException {

    private String message;

    public IMException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
