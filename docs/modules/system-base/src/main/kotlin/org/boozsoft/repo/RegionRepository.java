package org.boozsoft.repo;


import org.boozsoft.repo.entity.Region;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RegionRepository<T,ID> extends ReactiveCrudRepository<Region,String> {

}
