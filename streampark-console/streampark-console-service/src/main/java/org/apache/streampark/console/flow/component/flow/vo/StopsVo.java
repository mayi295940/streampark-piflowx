package org.apache.streampark.console.flow.component.flow.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.common.Eunm.PortType;
import org.apache.streampark.console.flow.component.dataSource.vo.DataSourceVo;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Stop component table */
@Setter
@Getter
public class StopsVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private FlowVo flowVo;

  private String name;

  private String bundle;

  private String groups;

  private String owner;

  private String description;

  private String pageId;

  private String inports;

  private PortType inPortType;

  private String outports;

  private PortType outPortType;

  private Boolean isCheckpoint;

  private Long version;

  private Boolean isCustomized = false;

  private Date crtDttm = new Date();

  private String language;

  private DataSourceVo dataSourceVo;

  private List<StopsPropertyVo> propertiesVo = new ArrayList<>();

  private List<StopsPropertyVo> oldPropertiesVo = new ArrayList<>();

  private List<StopsCustomizedPropertyVo> stopsCustomizedPropertyVoList = new ArrayList<>();

  private Boolean isDataSource = false;

  private Boolean isDisabled = false;

  public String getCrtDttmString() {
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_ss);
    return crtDttm != null ? sdf.format(crtDttm) : "";
  }
}
