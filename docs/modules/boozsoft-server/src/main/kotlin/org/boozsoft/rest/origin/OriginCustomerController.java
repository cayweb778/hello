package org.boozsoft.rest.origin;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.origin.OrgCustomer;
import org.boozsoft.domain.entity.origin.OrgCustomerClass;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.repo.origin.OriginCustomerClassRepository;
import org.boozsoft.repo.origin.OriginCustomerRepository;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/originCustomer")
public class OriginCustomerController {

    @Autowired
    OriginCustomerRepository originCustomerRepository;
    @Autowired
    OriginCustomerClassRepository originCustomerClassRepository;



    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable) {
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        String flag=map.get("flag").toString();
        String orgUnique=map.get("orgUnique").toString();
        String uniqueCustclass=map.get("uniqueCustclass").toString();
        // 当前页
        int page= Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return originCustomerRepository.findAllByOrgUnique(orgUnique).collectList()
            .flatMap(item->{
                long total=item.size();
                List<CustomerVo> list=item.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                if(StringUtils.isNotBlank(flag)){
                    list=item.stream().filter(v->v.getFlag().equals(flag)).collect(Collectors.toList());
                    total=list.size();
                }
                if(!"0".equals(uniqueCustclass)){
                    list=item.stream().filter(v->StringUtils.isNotBlank(v.getUniqueCustclass())&&v.getUniqueCustclass().equals(uniqueCustclass)).collect(Collectors.toList());
                    total=list.size();
                }
                totalAR.set(total);
                return Mono.just(
                        list.stream().filter(v->{
                            if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                                String value = searchMap.get("value");
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), v);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                            return true;
                        })
                );
            }).map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    @PostMapping("saveAll")
    public Mono<R> saveAll(@RequestBody List<OrgCustomer> list) {
        List<OrgCustomer> collect = list.stream().distinct().collect(Collectors.toList());
        return originCustomerRepository.saveAll(collect).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delById")
    public Mono<R> delById(@RequestBody List<OrgCustomer> list){
        return originCustomerRepository.deleteAll(list).then(Mono.just(R.ok()));
    }
}
