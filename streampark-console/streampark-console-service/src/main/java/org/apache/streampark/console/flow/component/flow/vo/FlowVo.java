package org.apache.streampark.console.flow.component.flow.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.component.mxGraph.vo.MxGraphModelVo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FlowVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String engineType;
  private String uuid;
  private String description;
  private String driverMemory;
  private String executorNumber;
  private String executorMemory;
  private String executorCores;
  private Date crtDttm;
  private String pageId;
  private int stopQuantity;
  private MxGraphModelVo mxGraphModelVo; // Drawing board information
  private List<StopsVo> stopsVoList = new ArrayList<>(); // Current stream all stops
  private List<PathsVo> pathsVoList = new ArrayList<>(); // Current stream all paths

  public String getCrtDttmString() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return crtDttm != null ? sdf.format(crtDttm) : "";
  }
}
