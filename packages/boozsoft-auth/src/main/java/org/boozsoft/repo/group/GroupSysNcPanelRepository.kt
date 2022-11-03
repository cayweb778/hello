package org.boozsoft.repo.group

import org.boozsoft.domain.entity.group.GroupSysNcPanel
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GroupSysNcPanelRepository: ReactiveCrudRepository<GroupSysNcPanel,String> {

    @Query(" select * from _app_group_sys_nc_panel order By panel_order ")
    fun findAllOederByPanelOrder(): Flux<GroupSysNcPanel>
    @Query(" update _app_group_sys_nc_panel set panel_flag=:flag,acc_id=:accId where id=:id ")
    fun editFlagAndAccId(flag:String,accId:String,id:String): Mono<Void>
    @Query(" update _app_group_sys_nc_panel set panel_order=:order,acc_id=:accId where id=:id ")
    fun editOrderAndAccId(order:Integer,accId:String,id:String): Mono<Void>
}