package cn.piflow.bundle.flink.model;

import java.io.Serializable;

/**
 * <physical_column_definition>: column_name column_type [ <column_constraint> ] [COMMENT
 * column_comment]
 */
public class FlinkTablePhysicalColumn implements Serializable {

  private String columnName;

  private String columnType;

  private Integer length;

  private Integer precision;

  private Integer scale;

  private Boolean nullable;
  private Boolean primaryKey;
  private Boolean partitionKey;

  private String comment;

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

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  public Integer getScale() {
    return scale;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }

  public Boolean getNullable() {
    return nullable;
  }

  public void setNullable(Boolean nullable) {
    this.nullable = nullable;
  }

  public Boolean getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  public Boolean getPartitionKey() {
    return partitionKey;
  }

  public void setPartitionKey(Boolean partitionKey) {
    this.partitionKey = partitionKey;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
