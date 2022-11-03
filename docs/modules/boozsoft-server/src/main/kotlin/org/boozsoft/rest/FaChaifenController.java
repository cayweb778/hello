package org.boozsoft.rest;

import org.boozsoft.domain.entity.fa.FaChaifenHead;
import org.boozsoft.repo.fa.FaCardMasterRepository;
import org.boozsoft.repo.fa.FaChaifenHeadRepository;
import org.boozsoft.repo.fa.FaChangeRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/faChaifen")
public class FaChaifenController {

    @Autowired
    FaChaifenHeadRepository faChaifenHeadRepository;
    @Autowired
    FaCardMasterRepository faCardMasterRepository;
    @Autowired
    FaChangeRepository faChangeRepository;

    @GetMapping("findCardByLessCdateList")
    public Mono<R> findCardByLessCdateList(String manageCode,String cdate,String jianshao,String chaifen,String jiechu){
        String iyear = cdate.substring(0,4);
        String imonth = cdate.substring(5,7);
        return faCardMasterRepository.findCardByLessCdateList(manageCode,cdate,jianshao,chaifen,jiechu,iyear,imonth)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findAllList")
    public Mono<R> findAllList(String manageCode){
        return faChaifenHeadRepository.findAllByManageCodeOrderByChaifenCode(manageCode)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findAllOrderByChaifenCode")
    public Mono<R> findAllOrderByChaifenCode(String manageCode){
        return faChaifenHeadRepository.findAllOrderByChaifenCode(manageCode)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findMaxChaifenCode")
    public Mono<R> findMaxChaifenCode(String manageCode,String cdate){
        String iyear = cdate.substring(0,4);
        String imonth = cdate.substring(5,7);
        return faChaifenHeadRepository.findMaxChaifenCode(manageCode,iyear,imonth)
                .map(o -> R.ok().setResult(o));
    }


    @PostMapping("saveFaChaifenHead")
    public Mono<R> saveFaChaifenHead(@RequestBody FaChaifenHead faChaifenHead){
        return faChaifenHeadRepository.save(faChaifenHead)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteChaifenByCardUnique")
    public Mono<R> deleteChaifenByCardUnique(String cardUnique){
        return faChaifenHeadRepository.deleteByCardUnique(cardUnique)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findCardMasterBySourceCardUniqueOrderByCardCode")
    public Mono<R> findCardMasterBySourceCardUniqueOrderByCardCode(String cardUnique){
        return faCardMasterRepository.findBySourceCardUniqueOrderByCardCode(cardUnique)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteChangeByCardUniqueAndIyearAndImonth")
    public Mono<R> deleteChangeByCardUniqueAndIyearAndImonth(String cardUnique,String iyear,String imonth){
        return faChangeRepository.deleteByCardUniqueAndIyearAndImonth(cardUnique,iyear,imonth)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findChaifenByCardUnique")
    public Mono<R> findChaifenByCardUnique(String cardUnique){
        return faChaifenHeadRepository.findByCardUnique(cardUnique)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCardByCardUnique")
    public Mono<R> findCardByCardUnique(String cardUnique){
        return faCardMasterRepository.findCardByCardUnique(cardUnique)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCardBySourceCardUnique")
    public Mono<R> findCardBySourceCardUnique(String cardUnique){
        return faCardMasterRepository.findCardBySourceCardUnique(cardUnique)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
