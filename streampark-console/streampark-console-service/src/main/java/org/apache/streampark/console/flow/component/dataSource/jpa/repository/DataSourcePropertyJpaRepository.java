package org.apache.streampark.console.flow.component.dataSource.jpa.repository;

import java.io.Serializable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSource;
import org.apache.streampark.console.flow.component.dataSource.entity.DataSourceProperty;

public interface DataSourcePropertyJpaRepository
    extends JpaRepository<DataSourceProperty, String>,
        JpaSpecificationExecutor<DataSourceProperty>,
        Serializable {

  @Transactional
  @Modifying
  @Query("update DataSourceProperty c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);

  @Transactional
  @Modifying
  @Query(
      "update DataSourceProperty dsp set dsp.dataSource = null,dsp.enableFlag = :enableFlag where dsp.dataSource = :dataSource")
  int updateEnableFlagByDatasourceId(
          @Param("dataSource") DataSource dataSource, @Param("enableFlag") boolean enableFlag);
}
