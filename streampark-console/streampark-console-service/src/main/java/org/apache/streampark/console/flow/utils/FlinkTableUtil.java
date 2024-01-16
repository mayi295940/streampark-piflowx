package org.apache.streampark.console.flow.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.streampark.console.flow.component.flow.vo.StopsPropertyVo;
import org.apache.streampark.console.flow.model.flink.FlinkTableAsSelectStatement;
import org.apache.streampark.console.flow.model.flink.FlinkTableBaseInfo;
import org.apache.streampark.console.flow.model.flink.FlinkTableComputedColumn;
import org.apache.streampark.console.flow.model.flink.FlinkTableDefinition;
import org.apache.streampark.console.flow.model.flink.FlinkTableLikeStatement;
import org.apache.streampark.console.flow.model.flink.FlinkTableMetadataColumn;
import org.apache.streampark.console.flow.model.flink.FlinkTablePhysicalColumn;
import org.apache.streampark.console.flow.model.flink.FlinkTableWatermark;
import org.springframework.util.CollectionUtils;

public class FlinkTableUtil {

  private static final String COMMA = ",";

  public static String getDDL(List<StopsPropertyVo> propertiesVos) {

    String ddl = "";

    if (CollectionUtils.isEmpty(propertiesVos)) {
      return ddl;
    }

    FlinkTableDefinition tableDefinition = null;
    Map<String, String> properties = new HashMap<>();

    List<String> configList = new ArrayList<>();

    configList.add(String.format("'connector' = '%s'", "connector"));

    for (StopsPropertyVo propertiesVo : propertiesVos) {

      if ("tableDefinition".equalsIgnoreCase(propertiesVo.getName())) {
        tableDefinition =
            JSON.parseObject(propertiesVo.getCustomValue(), FlinkTableDefinition.class);
      } else if ("properties".equalsIgnoreCase(propertiesVo.getName())) {
        properties =
            JSON.parseObject(
                propertiesVo.getCustomValue(), new TypeReference<HashMap<String, String>>() {});
      } else {
        if (StringUtils.isNotBlank(propertiesVo.getCustomValue())) {
          String value = propertiesVo.getCustomValue();
          if (propertiesVo.getSensitive()) {
            value = "********";
          }
          configList.add(String.format("'%s' = '%s'", propertiesVo.getName(), value));
        }
      }
    }

    if (tableDefinition == null) {
      return ddl;
    }

    FlinkTableBaseInfo baseInfo = tableDefinition.getTableBaseInfo();
    List<FlinkTablePhysicalColumn> physicalColumns = tableDefinition.getPhysicalColumnDefinition();

    /*
     * <physical_column_definition>:
     * column_name column_type [ <column_constraint> ] [COMMENT column_comment]
     */
    if (!CollectionUtils.isEmpty(physicalColumns)) {

      StringBuilder columns = new StringBuilder();
      List<String> primaryKeyList = new ArrayList<>();
      List<String> partitionKeyList = new ArrayList<>();

      FlinkTableAsSelectStatement asSelectStatement = tableDefinition.getAsSelectStatement();
      FlinkTableLikeStatement likeStatement = tableDefinition.getLikeStatement();
      List<FlinkTableMetadataColumn> metadataColumns =
          tableDefinition.getMetadataColumnDefinition();
      List<FlinkTableComputedColumn> computedColumns =
          tableDefinition.getComputedColumnDefinition();
      FlinkTableWatermark watermark = tableDefinition.getWatermarkDefinition();

      for (FlinkTablePhysicalColumn column : physicalColumns) {
        if (StringUtils.isNotBlank(column.getColumnName())) {
          if (column.getLength() != null && column.getLength() > 0) {
            columns.append(
                String.format(
                    "`%s` %s(%d)",
                    column.getColumnName(), column.getColumnType(), column.getLength()));
          } else if (column.getPrecision() != null
              && column.getPrecision() > 0
              && column.getScale() != null
              && column.getScale() > 0) {
            columns.append(
                String.format(
                    "`%s` %s(%d, %d)",
                    column.getColumnName(),
                    column.getColumnType(),
                    column.getPrecision(),
                    column.getScale()));
          } else {
            columns.append(
                String.format("`%s` %s", column.getColumnName(), column.getColumnType()));
          }

          if (StringUtils.isNotBlank(column.getComment())) {
            columns.append(String.format(" COMMENT '%s'", column.getComment()));
          }

          columns.append(COMMA);

          if (column.getPrimaryKey()) {
            primaryKeyList.add(column.getColumnName());
          }
          if (column.getPartitionKey()) {
            partitionKeyList.add(column.getColumnName());
          }
        }
      }

      /*
       * <metadata_column_definition>:
       *  column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL ]
       */
      if (!CollectionUtils.isEmpty(metadataColumns)) {
        for (FlinkTableMetadataColumn column : metadataColumns) {
          if (StringUtils.isNotBlank(column.getColumnName())) {
            columns.append(
                String.format(" `%s` %s METADATA", column.getColumnName(), column.getColumnType()));
            if (StringUtils.isNotBlank(column.getFrom())) {
              columns.append(String.format(" FROM '%s' ", column.getFrom()));
            }
            if (column.getVirtual()) {
              columns.append(" VIRTUAL ");
            }
            columns.append(COMMA);
          }
        }
      }

      /*
       * <computed_column_definition>:
       * column_name AS computed_column_expression [COMMENT column_comment]
       */
      if (!CollectionUtils.isEmpty(computedColumns)) {
        computedColumns.forEach(
            column -> {
              if (StringUtils.isNotBlank(column.getColumnName())) {
                columns.append(
                    String.format(
                        " `%s` AS %s",
                        column.getColumnName(), column.getComputedColumnExpression()));

                if (StringUtils.isNotBlank(column.getComment())) {
                  columns.append(String.format(" COMMENT '%s'", column.getComment()));
                }
                columns.append(COMMA);
              }
            });
      }

      /*
       * <watermark_definition>:
       * WATERMARK FOR rowtime_column_name AS watermark_strategy_expression
       * WATERMARK FOR view_time as view_time - INTERVAL '5' SECOND
       */
      if (watermark != null && StringUtils.isNotBlank(watermark.getRowTimeColumnName())) {
        columns.append(
            String.format(
                " WATERMARK FOR %s AS %s - INTERVAL '%s' %s,",
                watermark.getRowTimeColumnName(),
                watermark.getRowTimeColumnName(),
                watermark.getTime(),
                watermark.getTimeUnit()));
      }

      if (!CollectionUtils.isEmpty(primaryKeyList)) {
        columns.append(
            String.format(" PRIMARY KEY (%s) NOT ENFORCED,", String.join(COMMA, primaryKeyList)));
      }

      String tableComment = "";
      if (baseInfo != null && StringUtils.isNotBlank(baseInfo.getRegisterTableComment())) {
        tableComment = String.format(" COMMENT '%s'", baseInfo.getRegisterTableComment());
      }

      String partitionStatement = "";
      if (!CollectionUtils.isEmpty(partitionKeyList)) {
        tableComment = String.format(" PARTITIONED BY (%s)", String.join(COMMA, partitionKeyList));
      }

      String columnsStatement = columns.toString();
      if (columnsStatement.endsWith(",")) {
        columnsStatement = columnsStatement.substring(0, columnsStatement.length() - 1);
      }

      String ifNotExists = "";
      if (baseInfo != null && baseInfo.getIfNotExists()) {
        ifNotExists = "IF NOT EXISTS";
      }

      String selectStatement = "";
      if (asSelectStatement != null
          && StringUtils.isNotBlank(asSelectStatement.getSelectStatement())) {
        selectStatement =
            String.format(" AS %s", String.join(COMMA, asSelectStatement.getSelectStatement()));
      }

      String like = "";
      if (likeStatement != null && StringUtils.isNotBlank(likeStatement.getLikeStatement())) {
        like = String.format(" LIKE %s", String.join(COMMA, likeStatement.getLikeStatement()));
      }

      String tmpTable = "";
      if (StringUtils.isEmpty(tableDefinition.getRegisterTableName())) {
        tmpTable = "tmp_table";
      } else {
        tmpTable += tableDefinition.getFullRegisterTableName();
      }

      if (properties != null && !properties.isEmpty()) {
        properties.forEach(
            (key, value) -> {
              if (StringUtils.isNotBlank(value)) {
                configList.add(String.format("'%s' = '%s',", key, value));
              }
            });
      }

      String conf = String.join(COMMA, configList);

      ddl =
          String.format(
              " CREATE TABLE %s %s "
                  + "( %s )"
                  + " %s "
                  + " %s "
                  + "WITH ("
                  + "%s"
                  + ") "
                  + " %s "
                  + " %s ",
              ifNotExists,
              tmpTable,
              columnsStatement,
              tableComment,
              partitionStatement,
              conf,
              selectStatement,
              like);
    }

    return ddl;
  }
}
