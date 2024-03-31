package cn.piflow.bundle.flink.model;

import java.io.Serializable;

public class FlinkTableLikeStatement implements Serializable {

  private String likeStatement;

  public String getLikeStatement() {
    return likeStatement;
  }

  public void setLikeStatement(String likeStatement) {
    this.likeStatement = likeStatement;
  }
}
