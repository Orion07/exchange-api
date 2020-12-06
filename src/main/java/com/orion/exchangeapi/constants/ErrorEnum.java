package com.orion.exchangeapi.constants;

public enum ErrorEnum {
    RATE_NOT_FOUND("Rate not found."),
    CURRENCY_CAN_NOT_NULL("Currency cannot null."),
    INTERNAL_SERVER_ERROR("Internal Server Error");

    private String message;

    ErrorEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
