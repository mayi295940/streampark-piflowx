package org.apache.streampark.console.flow.component.system.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Use JPA to define users. Implement the UserDetails interface, the user entity is the user used by
 * springSecurity.
 */
@Getter
@Setter
public class SysUser extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String username;
  private String password;
  private String name;
  private Byte status;
  private Integer age;
  private String sex;
  private String lastLoginIp;
  private String developerAccessKey;
  private List<SysRole> roles;
}
