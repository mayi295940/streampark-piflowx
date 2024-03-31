package cn.piflow.bundle.flink.model;

import java.io.Serializable;

/**
 * <metadata_column_definition>: column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL ]
 */
public class FlinkTableMetadataColumn implements Serializable {

  private String columnName;
  private String columnType;

  private String from;
  private Boolean virtual;

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public Boolean getVirtual() {
    return virtual;
  }

  public void setVirtual(Boolean virtual) {
    this.virtual = virtual;
  }
}
