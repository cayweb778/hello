package org.boozsoft.repo;

import org.boozsoft.domain.entity.AccPanel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccPanelRepository extends ReactiveCrudRepository<AccPanel, String> {
    @Query("select * from acc_panel where panel_flag=:flag and panel_iyear=:panelIyear and panel_type=:panelType order by panel_order")
    Flux<AccPanel> findAllByPanelFlagAndPanelIyear(String flag,String panelIyear,String panelType);

    @Query("select * from acc_panel where panel_iyear=:panelIyear and panel_type=:panelType ")
    Flux<AccPanel> findAllByPanelIyear(String panelIyear,String panelType);

    Mono<Long>countByPanelName(String name);
}
