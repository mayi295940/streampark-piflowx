package org.apache.streampark.console.flow.component.mxGraph.jpa.repository;

import java.io.Serializable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGeometry;

public interface MxGeometryJpaRepository
    extends JpaRepository<MxGeometry, String>, JpaSpecificationExecutor<MxGeometry>, Serializable {

  @Transactional
  @Modifying
  @Query("update MxGeometry c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);
}
