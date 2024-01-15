package org.apache.streampark.console.flow.model.flink;

/**
 * <computed_column_definition>: column_name AS computed_column_expression [COMMENT column_comment]
 */
public class FlinkTableComputedColumn {

  private String columnName;

  private String computedColumnExpression;

  private String comment;

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getComputedColumnExpression() {
    return computedColumnExpression;
  }

  public void setComputedColumnExpression(String computedColumnExpression) {
    this.computedColumnExpression = computedColumnExpression;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
