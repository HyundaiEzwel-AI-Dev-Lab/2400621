package com.hpms.prototype.api;

import com.hpms.prototype.api.dto.ErrorResponse;
import com.hpms.prototype.application.InvalidMyWorkRequestException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

  @ExceptionHandler(InvalidMyWorkRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidRequest(InvalidMyWorkRequestException ex) {
    return new ErrorResponse("MY_WORK_BAD_REQUEST", ex.getMessage(), OffsetDateTime.now(SEOUL));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
    return new ErrorResponse("MY_WORK_NOT_FOUND", ex.getMessage(), OffsetDateTime.now(SEOUL));
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleException(Exception ex) {
    return new ErrorResponse("MY_WORK_ERROR", "요청을 처리할 수 없습니다.", OffsetDateTime.now(SEOUL));
  }
}
