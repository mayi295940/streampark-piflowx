package org.apache.streampark.console.flow.component.system.vo;

import org.apache.streampark.console.flow.base.utils.DateUtils;
import org.apache.streampark.console.flow.component.system.entity.SysRole;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String username;
  private String password;
  private String name;
  private Integer age;
  private String sex;
  private Date crtDttm;
  private Byte status;
  private String lastLoginIp;
  private SysRole role;

  public String getCreateTime() {
    return DateUtils.dateTimesToStr(crtDttm);
  }
}
