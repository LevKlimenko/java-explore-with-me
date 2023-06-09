package ru.practicum.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Date;

@AllArgsConstructor
@Value
class ErrorResponse {
    Date timestamp;
    int status;
    String error;
    String path;
}