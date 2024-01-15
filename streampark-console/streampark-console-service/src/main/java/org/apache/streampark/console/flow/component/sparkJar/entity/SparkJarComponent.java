package org.apache.streampark.console.flow.component.sparkJar.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.SparkJarState;

import lombok.Getter;
import lombok.Setter;

/** Stop component table */
@Setter
@Getter
public class SparkJarComponent extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String mountId;
  private String jarName;
  private String jarUrl;
  private SparkJarState status;
}
