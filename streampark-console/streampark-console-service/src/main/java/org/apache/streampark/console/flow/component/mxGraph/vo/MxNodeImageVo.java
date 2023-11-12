package org.apache.streampark.console.flow.component.mxGraph.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MxNodeImageVo implements Serializable {

  private static final long serialVersionUID = -5345043212647460732L;

  private String id;
  private String imageName;
  private String imagePath;
  private String imageUrl;
}
