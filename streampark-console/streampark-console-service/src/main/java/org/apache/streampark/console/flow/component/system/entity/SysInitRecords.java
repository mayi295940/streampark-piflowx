package org.apache.streampark.console.flow.component.system.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(name = "SYS_INIT_RECORDS")
public class SysInitRecords implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  @GeneratedValue(generator = "idGenerator")
  @Column(length = 40)
  private String id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date initDate = new Date();

  @Column(nullable = false)
  private Boolean isSucceed = Boolean.TRUE;
}
