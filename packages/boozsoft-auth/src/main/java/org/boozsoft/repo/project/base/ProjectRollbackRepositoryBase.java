package org.boozsoft.repo.project.base;

import org.boozsoft.domain.entity.share.project.base.ProjectRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProjectRollbackRepositoryBase extends ReactiveCrudRepository<ProjectRollback,String> {

}
