package org.apache.streampark.console.flow.component.flow.jpa.repository;

import java.io.Serializable;
import java.util.List;
import org.apache.streampark.console.flow.component.flow.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyJpaRepository
    extends JpaRepository<Property, String>, JpaSpecificationExecutor<Property>, Serializable {

  @Query(
      nativeQuery = true,
      value =
          "select * from flow_stops_property fsp where fsp.enable_flag = 1 and fsp.fk_stops_id = (:stopId)")
  List<Property> getPropertyListByStopsId(@Param("stopId") String stopId);

  @Modifying
  @Query(
      nativeQuery = true,
      value =
          "update flow_stops_property fsp set fsp.enable_flag=0 where fsp.is_old_data=1 and fsp.fk_stops_id = (:stopId)")
  int deletePropertiesByIsOldDataAndStopsId(@Param("stopId") String stopId);
}
