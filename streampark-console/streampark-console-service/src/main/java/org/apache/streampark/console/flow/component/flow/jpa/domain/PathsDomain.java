package org.apache.streampark.console.flow.component.flow.jpa.domain;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.streampark.console.flow.component.flow.entity.Paths;
import org.apache.streampark.console.flow.component.flow.jpa.repository.PathsJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PathsDomain {

  @Autowired private PathsJpaRepository pathsJpaRepository;

  private Specification<Paths> addEnableFlagParam() {
    Specification<Paths> specification =
        new Specification<Paths>() {

          private static final long serialVersionUID = 1L;

          @Override
          public Predicate toPredicate(
              Root<Paths> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            // root.get("enableFlag") means to get the field name of enableFlag
            return criteriaBuilder.equal(root.get("enableFlag"), 1);
          }
        };
    return specification;
  }

  public Paths getPathsById(String id) {
    Paths paths = pathsJpaRepository.getOne(id);
    if (null != paths && !paths.getEnableFlag()) {
      paths = null;
    }
    return paths;
  }

  public List<Paths> getPathsList() {
    return pathsJpaRepository.findAll(addEnableFlagParam());
  }

  public Paths saveOrUpdate(Paths paths) {
    return pathsJpaRepository.save(paths);
  }

  public List<Paths> saveOrUpdate(List<Paths> pathsList) {
    return pathsJpaRepository.saveAll(pathsList);
  }

  public int updateEnableFlagById(String id, boolean enableFlag) {
    return pathsJpaRepository.updateEnableFlagById(id, enableFlag);
  }
}
