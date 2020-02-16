package io.swagger.oas.models;

import io.swagger.oas.annotations.media.Schema;

public class ReadOnlyModel {
  private Integer id;
  private Integer readWriteId;

  @Schema(readOnly = true)
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(readOnly = false)
  public Integer getReadWriteId() {
    return readWriteId;
  }
  public void setReadWriteId(Integer readWriteId) {
    this.readWriteId = readWriteId;
  }
}