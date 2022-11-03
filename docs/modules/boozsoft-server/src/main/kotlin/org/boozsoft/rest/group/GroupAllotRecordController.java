package org.boozsoft.rest.group;

import org.boozsoft.repo.group.GroupAllotRecordRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/groupAllot")
public class GroupAllotRecordController {

    @Autowired
    GroupAllotRecordRepository groupAllotRecordRepository;

    @PostMapping("finAllotByAccId")
    public Mono<R> finAllotByAccId(String accId,String dataType){
        return groupAllotRecordRepository.findAllByAccIdAndDataType(accId,dataType).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("del")
    public Mono<R> del(String id){
    return groupAllotRecordRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @PostMapping("editAcclotType")
    public Mono<R> editAcclotType(String accId,String dataUnique,String acclotType){
        return groupAllotRecordRepository.editAcclotType(accId,dataUnique,acclotType).then(Mono.just(R.ok()));
    }
}
