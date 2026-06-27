package com.hpms.prototype.api.dto;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public record ApiResponse<T>(T data, OffsetDateTime timestamp) {

  private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(data, OffsetDateTime.now(SEOUL));
  }
}
