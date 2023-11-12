package org.apache.streampark.console.flow.component.process.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ProcessParentType;
import org.apache.streampark.console.flow.common.Eunm.ProcessState;
import org.apache.streampark.console.flow.common.Eunm.RunModeType;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;

@Getter
@Setter
@Entity
@Table(name = "FLOW_PROCESS_GROUP")
public class ProcessGroup extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @Column(columnDefinition = "varchar(255) COMMENT 'Process name'")
  private String name;

  @Column(columnDefinition = "text COMMENT 'Process view xml string'")
  private String viewXml;

  @Column(columnDefinition = "varchar(1024) COMMENT 'description'")
  private String description;

  @Column(name = "page_id")
  private String pageId;

  @Column(columnDefinition = "varchar(255) COMMENT 'flowId'")
  private String flowId;

  @Column(columnDefinition = "varchar(255) COMMENT 'The id returned when calling runProcess'")
  private String appId;

  @Column(columnDefinition = "varchar(255) COMMENT 'third parentProcessId'")
  private String parentProcessId;

  @Column(columnDefinition = "varchar(255) COMMENT 'third processId'")
  private String processId;

  @Column(columnDefinition = "varchar(255) COMMENT 'Process status'")
  @Enumerated(EnumType.STRING)
  private ProcessState state;

  @Column(columnDefinition = "datetime  COMMENT 'Process startup time'")
  private Date startTime;

  @Column(columnDefinition = "datetime  COMMENT 'End time of the process'")
  private Date endTime;

  @Column(columnDefinition = "varchar(255) COMMENT 'Process progress'")
  private String progress;

  @Column(columnDefinition = "varchar(255) COMMENT 'Process RunModeType'")
  @Enumerated(EnumType.STRING)
  private RunModeType runModeType = RunModeType.RUN;

  @Column(columnDefinition = "varchar(255) COMMENT 'Process parent type'")
  @Enumerated(EnumType.STRING)
  private ProcessParentType processParentType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_FLOW_PROCESS_GROUP_ID")
  private ProcessGroup processGroup;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "processGroup")
  @Where(clause = "enable_flag=1")
  private MxGraphModel mxGraphModel;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "processGroup")
  @Where(clause = "enable_flag=1")
  @OrderBy(clause = "lastUpdateDttm desc")
  private List<Process> processList = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "processGroup")
  @Where(clause = "enable_flag=1")
  @OrderBy(clause = "lastUpdateDttm desc")
  private List<ProcessGroupPath> processGroupPathList = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "processGroup")
  @Where(clause = "enable_flag=1")
  @OrderBy(clause = "lastUpdateDttm desc")
  private List<ProcessGroup> processGroupList = new ArrayList<>();
}
