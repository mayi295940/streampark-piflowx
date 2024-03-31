package cn.piflow.bundle.flink.model;

import java.io.Serializable;

public class FlinkTableAsSelectStatement implements Serializable {

  private String selectStatement;

  public String getSelectStatement() {
    return selectStatement;
  }

  public void setSelectStatement(String selectStatement) {
    this.selectStatement = selectStatement;
  }
}
