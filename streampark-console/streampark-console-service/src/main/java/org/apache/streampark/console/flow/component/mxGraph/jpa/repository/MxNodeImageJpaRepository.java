package org.apache.streampark.console.flow.component.mxGraph.jpa.repository;

import java.io.Serializable;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxNodeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MxNodeImageJpaRepository
    extends JpaRepository<MxNodeImage, String>,
        JpaSpecificationExecutor<MxNodeImage>,
        Serializable {

  @Modifying
  @Query("update MxNodeImage c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);
}
