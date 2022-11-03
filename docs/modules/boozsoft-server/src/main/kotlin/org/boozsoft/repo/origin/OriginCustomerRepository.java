package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OrgCustomer;
import org.boozsoft.domain.vo.CustomerVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OriginCustomerRepository extends ReactiveCrudRepository<OrgCustomer, String> {
    @Query("SELECT c.*,\n" +
            "       cc.cus_cclass_name                                                                           as unique_custclass_name,\n" +
            "       org.org_name,\n" +
            "       (select s.cust_name from _app_origin_supplier s where s.unique_code = c.unique_code_supplier) as sup_name,\n" +
            "       (select c2.cust_name from _app_origin_customer c2 where c2.unique_code = c.unique_code_ccus)  as parent_name\n" +
            "from _app_origin_customer c\n" +
            "         left join _app_group_customer_class cc on cc.unique_custclass = c.unique_custclass\n" +
            "         left join _app_group_sys_org org on org.unique_code = c.org_unique\n" +
            "where c.org_unique=:orgUnique \n" +
            "order by c.cust_code")
    Flux<CustomerVo> findAllByOrgUnique(String orgUnique);
}
