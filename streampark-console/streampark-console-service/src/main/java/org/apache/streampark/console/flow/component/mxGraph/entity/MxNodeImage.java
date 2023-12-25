package org.apache.streampark.console.flow.component.mxGraph.entity;

import org.apache.streampark.console.flow.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MxNodeImage extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String imageName;
  private String imagePath;
  private String imageUrl;
  private String imageType;
}
