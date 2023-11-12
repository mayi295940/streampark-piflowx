package org.apache.streampark.console.flow.component.template.jpa.repository;

import java.io.Serializable;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.apache.streampark.console.flow.component.template.entity.FlowTemplate;

public interface FlowTemplateJpaRepository
    extends JpaRepository<FlowTemplate, String>,
        JpaSpecificationExecutor<FlowTemplate>,
        Serializable {

  @Transactional
  @Modifying
  @Query("update FlowTemplate c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);

  @Query(
      "select fgt from FlowTemplate fgt where fgt.enableFlag=true and fgt.crtUser=:crtUser order by fgt.crtDttm desc ")
  List<FlowTemplate> getFlowTemplateByCrtUser(@Param("crtUser") String crtUser);

  /**
   * Paging query
   *
   * @return
   */
  @Query(
      "select fgt from FlowTemplate fgt where fgt.enableFlag=true and (fgt.name like CONCAT('%',:param,'%'))")
  Page<FlowTemplate> getFlowTemplateListPageByParam(
      @Param("param") String param, Pageable pageable);

  /**
   * Paging query by user
   *
   * @return
   */
  @Query(
      "select fgt from FlowTemplate fgt where fgt.enableFlag=true and fgt.crtUser=:username and (fgt.name like CONCAT('%',:param,'%'))")
  Page<FlowTemplate> getFlowTemplateListPageByParamAndCrtUser(
      @Param("username") String username, @Param("param") String param, Pageable pageable);
}
