package org.apache.streampark.console.flow.component.flow.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.component.flow.entity.Stops;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class PathsVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private FlowVo flowVo;

  private String from;

  private String outport;

  private String inport;

  private String to;

  private String port;

  private String pageId;

  private String flowFrom;

  private String flowTo;

  private Stops stopFrom;

  private Stops StopTo;

  private Date crtDttm;

  private String filterCondition;

  public String getCrtDttmString() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return crtDttm != null ? sdf.format(crtDttm) : "";
  }
}
