package com.hpms.prototype.api.dto;

public record TaskActionResponse(long taskId, long projectId, TaskActions actions) {
  public record TaskActions(boolean scheduleManagement, boolean wbsDetail) {}
}
