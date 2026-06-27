package com.hpms.prototype.domain;

public enum ProjectStatus {
  TESTING("테스트"),
  DISCUSSING("협의중"),
  PROCESSING("처리중"),
  RECEIVED("접수");

  private final String displayName;

  ProjectStatus(String displayName) {
    this.displayName = displayName;
  }

  public String displayName() {
    return displayName;
  }
}
