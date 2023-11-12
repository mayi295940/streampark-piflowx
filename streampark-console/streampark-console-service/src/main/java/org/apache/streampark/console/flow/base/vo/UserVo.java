package org.apache.streampark.console.flow.base.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.common.Eunm.SysRoleType;
import org.apache.streampark.console.flow.component.system.entity.SysRole;
import org.apache.streampark.console.flow.component.system.vo.SysMenuVo;

@Getter
@Setter
public class UserVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String username;
  private String password;
  private String name;
  private Integer age;
  private List<SysMenuVo> sysMenuVoList;
  private List<SysRole> roles = new ArrayList<>();

  public UserVo() {}

  // Write a constructor that can create uservo directly using user
  public UserVo(String id, String username, SysRoleType role, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
    role = null == role ? SysRoleType.USER : role;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isAccountNonExpired() {
    return true;
  }

  public boolean isAccountNonLocked() {
    return true;
  }

  public boolean isCredentialsNonExpired() {
    return true;
  }

  public boolean isEnabled() {
    return true;
  }
}
