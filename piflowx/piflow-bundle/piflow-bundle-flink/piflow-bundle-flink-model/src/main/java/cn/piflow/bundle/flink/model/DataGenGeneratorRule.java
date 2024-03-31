package cn.piflow.bundle.flink.model;

import java.io.Serializable;
import java.time.Duration;

/**
 * <a
 * href="https://nightlies.apache.org/flink/flink-docs-release-1.17/zh/docs/connectors/table/datagen/">DataGen字段生成策略</a>
 *
 * @author mayi
 */
public class DataGenGeneratorRule implements Serializable {

  /** 字段名 */
  private String columnName;

  /** 指定 '#' 字段的生成器。可以是 'sequence' 或 'random'。 */
  private String kind;

  /** 随机生成器的最小值，适用于数字类型。 */
  private Double min;

  /** 随机生成器的最大值，适用于数字类型。 */
  private Double max;

  /** 随机生成器生成字符的长度，适用于 char、varchar、binary、varbinary、string。 */
  private Integer length;

  /** 序列生成器的起始值 */
  private String start;

  /** 序列生成器的结束值 */
  private String end;

  /** 随机生成器生成相对当前时间向过去偏移的最大值，适用于 timestamp 类型。 */
  private Duration maxPast;

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public Double getMin() {
    return min;
  }

  public void setMin(Double min) {
    this.min = min;
  }

  public Double getMax() {
    return max;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public Duration getMaxPast() {
    return maxPast;
  }

  public void setMaxPast(Duration maxPast) {
    this.maxPast = maxPast;
  }
}
