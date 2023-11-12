package org.apache.streampark.console.flow.component.process.jpa.repository;

import java.io.Serializable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.apache.streampark.console.flow.component.process.entity.ProcessPath;

public interface ProcessPathJpaRepository
    extends JpaRepository<ProcessPath, String>,
        JpaSpecificationExecutor<ProcessPath>,
        Serializable {

  @Transactional
  @Modifying
  @Query("update ProcessPath c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);
}
