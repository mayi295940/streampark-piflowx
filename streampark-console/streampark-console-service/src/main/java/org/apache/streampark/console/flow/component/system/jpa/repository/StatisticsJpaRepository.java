package org.apache.streampark.console.flow.component.system.jpa.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.apache.streampark.console.flow.component.system.entity.Statistics;

public interface StatisticsJpaRepository
    extends JpaRepository<Statistics, String>, JpaSpecificationExecutor<Statistics>, Serializable {}
