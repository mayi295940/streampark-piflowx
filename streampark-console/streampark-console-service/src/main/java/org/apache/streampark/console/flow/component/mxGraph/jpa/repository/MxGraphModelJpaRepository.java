package org.apache.streampark.console.flow.component.mxGraph.jpa.repository;

import java.io.Serializable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.apache.streampark.console.flow.component.mxGraph.entity.MxGraphModel;

public interface MxGraphModelJpaRepository
    extends JpaRepository<MxGraphModel, String>,
        JpaSpecificationExecutor<MxGraphModel>,
        Serializable {

  @Transactional
  @Modifying
  @Query("update MxGraphModel c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);

  @Query(
      nativeQuery = true,
      value =
          "select * from mx_graph_model s where s.enable_flag = 1 and s.fk_flow_group_id = :flowGroupId")
  MxGraphModel getMxGraphModelByFlowGroupId(@Param("flowGroupId") String flowGroupId);
}
