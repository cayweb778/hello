package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysGroup;
import org.boozsoft.domain.entity.group.GroupSysOrg;
import org.boozsoft.domain.vo.AccCount2Vo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysOrgRepository extends ReactiveCrudRepository<GroupSysOrg, String> {

    Mono<Long> countAllBy();

    Flux<GroupSysOrg> findFirstByOrgCode(String orgCode);

    Flux<GroupSysOrg> findFirstByOrgName(String orgName);
    Flux<GroupSysOrg> findAllByUniqueCodeIn(List<String> orgName);

    Mono<Long> countAllByOrgSuperior(String code);
    Mono<GroupSysOrg> findById(String id);
    Mono<GroupSysOrg> findByUniqueCode(String uniqueCode);

    Mono<Long> countAllByUniqueAccStandard(String uniqueAccStandard);


    @Query("select t.unique_acc_standard, t.id as template_id, a.ccode_level " +
            "from _app_group_sys_org a " +
            "  left join _app_group_acc_template t on a.unique_acc_standard = t.id " +
            "left join _app_group_acc_standard s on s.unique_acc_standard=t.unique_acc_standard\n" +
            "where a.unique_code =:groupId")
    Mono<AccCount2Vo> findAllByAccCountPartColumn(String groupId);


}
