package org.apache.streampark.console.flow.component.flow.jpa.repository;

import java.io.Serializable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.apache.streampark.console.flow.component.flow.entity.FlowGroupPaths;

public interface FlowGroupPathsJpaRepository
    extends JpaRepository<FlowGroupPaths, String>,
        JpaSpecificationExecutor<FlowGroupPaths>,
        Serializable {

  @Transactional
  @Modifying
  @Query("update FlowGroupPaths c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);
}
