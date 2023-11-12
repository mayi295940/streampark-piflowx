package org.apache.streampark.console.flow.component.mxGraph.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.streampark.console.flow.base.BaseHibernateModelUUIDNoCorpAgentId;

@Setter
@Getter
@Entity
@Table(name = "MX_NODE_IMAGE")
public class MxNodeImage extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String imageName;
  private String imagePath;
  private String imageUrl;
  private String imageType;
}
