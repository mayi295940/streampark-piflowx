package cn.piflow.bundle.flink.model;

import java.io.Serializable;

public class FlinkTableWatermark implements Serializable {

  private String rowTimeColumnName;

  private Double time;

  private String timeUnit;

  public String getRowTimeColumnName() {
    return rowTimeColumnName;
  }

  public void setRowTimeColumnName(String rowTimeColumnName) {
    this.rowTimeColumnName = rowTimeColumnName;
  }

  public Double getTime() {
    return time;
  }

  public void setTime(Double time) {
    this.time = time;
  }

  public String getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(String timeUnit) {
    this.timeUnit = timeUnit;
  }
}
