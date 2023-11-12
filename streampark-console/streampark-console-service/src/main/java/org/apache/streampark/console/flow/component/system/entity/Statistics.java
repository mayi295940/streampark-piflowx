package org.apache.streampark.console.flow.component.system.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(name = "Statistics")
public class Statistics {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  @GeneratedValue(generator = "idGenerator")
  @Column(length = 40)
  private String id;

  private String loginIp;
  private String loginUser;
  private Date loginTime;
}
