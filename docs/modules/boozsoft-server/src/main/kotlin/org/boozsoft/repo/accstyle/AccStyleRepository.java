package org.boozsoft.repo.accstyle;

import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccStyleRepository extends ReactiveCrudRepository<AccStyle, String> {

    @Query("select accst.* from _app_group_acc_style as accst where accst.unique=:unique order by accst.order")
    Flux<AccStyle> findAllStyleByUnique(String unique);
    @Query("select accst.* from _app_group_acc_style as accst order by accst.order")
    Flux<AccStyle> findAlls();

    @Query("select sty.* from _app_group_acc_style sty left join _app_group_acc_standard sta on sta.unique_acc_standard=sty.\"unique\" where sta.unique_acc_standard=:unique_acc_standard order by sty.\"order\"")
    Flux<AccStyle> findByUnique(String unique_acc_standard);

    @Query("select string_agg(sty.cclass, '-') from _app_group_sys_account\n" +
            "right join _app_group_acc_template temp on acc_standard=temp.id\n" +
            "right join _app_group_acc_standard standard on standard.unique_acc_standard=temp.unique_acc_standard\n" +
            "right join _app_group_acc_style sty on sty.\"unique\"=standard.acc_style_unique\n" +
            "where acc_id=:database")
    Mono<String> findByAccIdAccStyle(String database);

    @Query("select s.\"unique\" from _app_group_acc_style s group by s.\"unique\" order by s.\"unique\" ")
    Flux<String> groupByAccStyleUnique();

    @Query("select * from _app_group_acc_style order by \"unique\",\"order\"")
    Flux<AccStyle> findAllOrderByNum();

    @Query("select sty.cclass from _app_group_sys_account\n" +
            "right join _app_group_acc_template temp on acc_standard=temp.id\n" +
            "right join _app_group_acc_standard standard on standard.unique_acc_standard=temp.unique_acc_standard\n" +
            "right join _app_group_acc_style sty on sty.\"unique\"=standard.acc_style_unique\n" +
            "where acc_id=:accId order by sty.\"order\" ")
    Flux<String> findByAccIdAccStyleList(String accId);

    @Query("select sty.*\n" +
            "from _app_group_sys_account acc\n" +
            "         left join _app_group_acc_template t on acc.acc_standard = t.id\n" +
            "         left join _app_group_acc_standard s on s.unique_acc_standard = t.unique_acc_standard\n" +
            "         left join _app_group_acc_style sty on sty.\"unique\" = s.acc_style_unique\n" +
            "where acc.id =:accId  and sty.cclass in ('权益','损益')")
    Flux<AccStyle> findByAccIdSunYiAndQuanYi(String accId);
}

