package org.apache.streampark.console.flow.component.system.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.common.Eunm.SysRoleType;

@Entity
@Getter
@Setter
@Table(name = "SYS_ROLE")
public class SysRole implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private SysRoleType role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_SYS_USER_ID")
  private SysUser sysUser;
}
