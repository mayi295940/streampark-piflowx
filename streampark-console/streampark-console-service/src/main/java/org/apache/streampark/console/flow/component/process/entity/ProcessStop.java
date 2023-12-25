package org.apache.streampark.console.flow.component.process.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import org.apache.streampark.console.flow.common.Eunm.ComponentFileType;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.common.Eunm.StopState;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessStop extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private Process process;
  private String name;
  private String bundle;
  private String groups;
  private String owner;
  private String description;
  private String inports;
  private PortType inPortType;
  private String outports;
  private PortType outPortType;
  private StopState state = StopState.INIT;
  private Date startTime;
  private Date endTime;
  private String pageId;
  private List<ProcessStopProperty> processStopPropertyList = new ArrayList<>();
  private List<ProcessStopCustomizedProperty> processStopCustomizedPropertyList = new ArrayList<>();
  private Boolean isDataSource = false;

  private String dockerImagesName; // docker image name,not save in process_stop
  private ComponentFileType componentType; // component type,not save in process_stop
}
