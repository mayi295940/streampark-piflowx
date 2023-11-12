package org.apache.streampark.console.flow;

import java.util.List;
import javax.annotation.Resource;

import org.apache.streampark.console.flow.base.util.CheckPathUtils;
import org.apache.streampark.console.flow.base.util.LoggerUtil;
import org.apache.streampark.console.flow.base.util.QuartzUtils;
import org.apache.streampark.console.flow.common.Eunm.ScheduleState;
import org.apache.streampark.console.flow.common.constant.SysParamsCache;
import org.apache.streampark.console.flow.component.system.entity.SysSchedule;
import org.apache.streampark.console.flow.component.system.mapper.SysScheduleMapper;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(value = 1)
public class StartLoader implements ApplicationRunner {

  Logger logger = LoggerUtil.getLogger();

  @Resource private SysScheduleMapper sysScheduleMapper;

  @Resource private Scheduler scheduler;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    checkStoragePath();
    // todo 取消注释
    // startStatusRunning();
  }

  private void checkStoragePath() {
    String storagePathHead = System.getProperty("user.dir");
    logger.warn(storagePathHead);
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/image/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/video/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/xml/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/csv/");
    SysParamsCache.setImagesPath(storagePathHead + "/storage/image/");
    SysParamsCache.setVideosPath(storagePathHead + "/storage/video/");
    SysParamsCache.setXmlPath(storagePathHead + "/storage/xml/");
    SysParamsCache.setCsvPath(storagePathHead + "/storage/csv/");
  }

  private void startStatusRunning() {
    List<SysSchedule> sysScheduleByStatusList =
        sysScheduleMapper.getSysScheduleListByStatus(true, ScheduleState.RUNNING);
    if (null != sysScheduleByStatusList && sysScheduleByStatusList.size() > 0) {
      for (SysSchedule sysSchedule : sysScheduleByStatusList) {
        QuartzUtils.createScheduleJob(scheduler, sysSchedule);
      }
    }
  }
}
