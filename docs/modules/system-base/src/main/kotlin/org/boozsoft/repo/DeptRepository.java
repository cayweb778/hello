package org.boozsoft.repo;


import org.boozsoft.repo.entity.Jigou;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DeptRepository extends ReactiveCrudRepository<Jigou,String> {

}
