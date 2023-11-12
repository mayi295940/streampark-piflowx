package org.apache.streampark.console.flow.component.template.jpa.repository;

import java.io.Serializable;
import java.util.List;
import javax.transaction.Transactional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.apache.streampark.console.flow.component.template.entity.FlowGroupTemplate;

public interface FlowGroupTemplateJpaRepository
    extends JpaRepository<FlowGroupTemplate, String>,
        JpaSpecificationExecutor<FlowGroupTemplate>,
        Serializable {

  @Transactional
  @Modifying
  @Query("update FlowGroupTemplate c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);

  @Query("select fgt from FlowGroupTemplate fgt where fgt.enableFlag=true and fgt.crtUser=:crtUser")
  List<FlowGroupTemplate> getFlowGroupTemplateByCrtUser(@Param("crtUser") String crtUser);

  /**
   * Paging query
   *
   * @return
   */
  @Query(
      "select fgt from FlowGroupTemplate fgt where fgt.enableFlag=true and (fgt.name like CONCAT('%',:param,'%'))")
  Page<FlowGroupTemplate> getFlowGroupTemplateListPageByParam(
      @Param("param") String param, Pageable pageable);

  /**
   * Paging query by user
   *
   * @return
   */
  @Query(
      "select fgt from FlowGroupTemplate fgt where fgt.enableFlag=true and fgt.crtUser=:username and (fgt.name like CONCAT('%',:param,'%'))")
  Page<FlowGroupTemplate> getFlowGroupTemplateListPageByParamAndCrtUser(
      @Param("username") String username, @Param("param") String param, Pageable pageable);
}
