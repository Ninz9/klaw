package com.kafkamgt.uiapi.model;

public enum AclOperation {
  CREATE("Create"),
  DELETE("Delete");

  public final String value;

  private AclOperation(String value) {
    this.value = value;
  }
}
