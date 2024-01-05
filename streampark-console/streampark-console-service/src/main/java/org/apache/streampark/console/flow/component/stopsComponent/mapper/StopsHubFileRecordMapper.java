package org.apache.streampark.console.flow.component.stopsComponent.mapper;

import org.apache.streampark.console.flow.component.stopsComponent.entity.StopsHubFileRecord;
import org.apache.streampark.console.flow.component.stopsComponent.mapper.provider.StopsHubFileRecordMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface StopsHubFileRecordMapper {

  /**
   * 添加算法包的具体文件记录
   *
   * @param record record
   */
  @InsertProvider(type = StopsHubFileRecordMapperProvider.class, method = "addStopsHubFileRecord")
  int addStopsHubFileRecord(StopsHubFileRecord record);

  @Select("select * from stops_hub_file_record where stops_hub_id = #{hubId}")
  @Results({
    @Result(column = "file_path", property = "filePath"),
    @Result(
        column = "file_path",
        property = "stopsComponent",
        one =
            @One(
                select =
                    "org.apache.streampark.console.flow.component.stopsComponent.mapper.StopsComponentMapper.getOnlyStopsComponentByBundle",
                fetchType = FetchType.LAZY))
  })
  List<StopsHubFileRecord> getStopsHubFileRecordByHubId(@Param("hubId") String hubId);

  /**
   * 根据id查询算法包的具体文件记录
   *
   * @param id id
   */
  @Select("select * from stops_hub_file_record where id = #{id}")
  StopsHubFileRecord getStopsHubFileRecordById(@Param("id") String id);

  /**
   * 根据id查询算法包的具体文件记录
   *
   * @param stopBundle stopBundle
   */
  @Select("select * from stops_hub_file_record where file_Path = #{stopBundle}")
  StopsHubFileRecord getStopsHubFileRecordByBundle(@Param("stopBundle") String stopBundle);

  /**
   * 根据id删除算法包的具体文件记录
   *
   * @param id id
   */
  @Delete("delete from stops_hub_file_record where id = #{id}")
  int deleteStopsHubFileRecord(@Param("id") String id);
}
