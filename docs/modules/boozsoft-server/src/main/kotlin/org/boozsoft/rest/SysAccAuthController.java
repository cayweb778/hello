package org.boozsoft.rest;

import org.boozsoft.repo.SysAccAuthRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sysAccAuth")
public class SysAccAuthController {

    @Autowired
    SysAccAuthRepository repository;

    @GetMapping("findByUserIdAndAccIdAndYear")
    public Mono<R> findByUserIdAndAccIdAndYear(String userId, String accId, String year){
        return repository.findByUserNumAndAccIdAndIyear(userId, accId, year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByCode")
    public Mono<R> findAllByCode(String userId, String accId, String year){
        return repository.findAllCodesByCondition(userId, accId, year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
