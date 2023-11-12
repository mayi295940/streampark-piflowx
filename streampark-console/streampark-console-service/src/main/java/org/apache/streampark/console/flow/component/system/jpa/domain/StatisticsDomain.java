package org.apache.streampark.console.flow.component.system.jpa.domain;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.streampark.console.flow.component.system.entity.Statistics;
import org.apache.streampark.console.flow.component.system.jpa.repository.StatisticsJpaRepository;

@Component
public class StatisticsDomain {

  @Autowired private StatisticsJpaRepository statisticsJpaRepository;

  public Statistics getStatisticsById(String id) {
    return statisticsJpaRepository.getOne(id);
  }

  public List<Statistics> getStatisticsList() {
    return statisticsJpaRepository.findAll();
  }

  public Statistics saveOrUpdate(Statistics statistics) {
    return statisticsJpaRepository.save(statistics);
  }

  public List<Statistics> saveOrUpdate(List<Statistics> statisticsList) {
    return statisticsJpaRepository.saveAll(statisticsList);
  }
}
