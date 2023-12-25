package org.apache.streampark.console.flow.component.system.entity;

import org.apache.streampark.console.flow.common.Eunm.SysRoleType;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysRole implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private SysRoleType role;
  private SysUser sysUser;
}
