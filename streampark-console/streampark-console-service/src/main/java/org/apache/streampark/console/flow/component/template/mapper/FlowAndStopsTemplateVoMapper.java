package org.apache.streampark.console.flow.component.template.mapper;

import org.apache.streampark.console.flow.component.template.entity.PropertyTemplateModel;
import org.apache.streampark.console.flow.component.template.entity.StopTemplateModel;
import org.apache.streampark.console.flow.component.template.mapper.provider.FlowAndStopsTemplateVoMapperProvider;
import org.apache.streampark.console.flow.component.template.vo.FlowTemplateModelVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.FetchType;

/** Stop component table */
@Mapper
public interface FlowAndStopsTemplateVoMapper {

  /**
   * Add a single stops
   *
   * @param stops stops
   */
  @InsertProvider(type = FlowAndStopsTemplateVoMapperProvider.class, method = "addStops")
  int addStops(StopTemplateModel stops);

  /**
   * add Flow
   *
   * @param flow flow
   */
  @InsertProvider(type = FlowAndStopsTemplateVoMapperProvider.class, method = "addFlow")
  int addFlow(FlowTemplateModelVo flow);

  /**
   * Insert list<PropertyVo> Note that the method of spelling sql must use Map to connect Param
   * content to key value.
   *
   * @param propertyList (Content: The key is propertyList and the value is List<PropertyVo>)
   */
  @InsertProvider(type = FlowAndStopsTemplateVoMapperProvider.class, method = "addPropertyList")
  int addPropertyList(List<PropertyTemplateModel> propertyList);

  /**
   * Invalid or delete stop according to templateId
   *
   * @param templateId templateId
   */
  @Update("update stops_template set enable_flag = 0 where fk_template_id = #{templateId} ")
  int deleteStopTemByTemplateId(String templateId);

  /**
   * Modify invalid or delete stop attribute information according to stopId
   *
   * @param stopId stopId
   */
  @Update("update property_template set enable_flag = 0 where fk_stops_id = #{stopId} ")
  int deleteStopPropertyTemByStopId(String stopId);

  /**
   * Query all stop information according to the template id
   *
   * @param templateId templateId
   */
  @Select("select * from stops_template where enable_flag = 1  and fk_template_id = #{templateId};")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "properties",
        many =
            @Many(
                select =
                    "cn.cnic.component.template.mapper.FlowAndStopsTemplateVoMapper.getPropertyListByStopsId",
                fetchType = FetchType.EAGER))
  })
  List<StopTemplateModel> getStopsListByTemPlateId(@Param("templateId") String templateId);

  /**
   * Query all stop attribute information according to stopId
   *
   * @param stopsId stopsId
   */
  @Select("select * from property_template where fk_stops_id = #{stopsId} and enable_flag = 1 ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  List<PropertyTemplateModel> getPropertyListByStopsId(@Param("stopsId") String stopsId);
}
