package org.apache.streampark.console.flow.component.sparkJar.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.SparkJarState;

/** Stop component table */
@Getter
@Setter
@Entity
@Table(name = "SPARK_JAR")
public class SparkJarComponent extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @Column(columnDefinition = "varchar(1000) COMMENT 'jar mount id'")
  private String mountId;

  @Column(columnDefinition = "varchar(1000) COMMENT 'jar name'")
  private String jarName;

  @Column(columnDefinition = "varchar(1000) COMMENT 'jar url'")
  private String jarUrl;

  @Column(columnDefinition = "varchar(255) COMMENT 'Spark jar status'")
  @Enumerated(EnumType.STRING)
  private SparkJarState status;
}
