package org.boozsoft.rest;

import org.boozsoft.domain.entity.fa.FaStyle;
import org.boozsoft.domain.entity.fa.FaStyleHead;
import org.boozsoft.repo.fa.FaStyleHeadRepository;
import org.boozsoft.repo.fa.FaStyleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/faStyle")
public class FaStyleController {

    @Autowired
    FaStyleHeadRepository faStyleHeadRepository;
    @Autowired
    FaStyleRepository faStyleRepository;

    @GetMapping("findFaStyleHeadAll")
    public Mono<R> findFaStyleHeadAll(){
        return faStyleHeadRepository.findAll()
                .collectList()
                .map(R::page);
    }

    @PostMapping("saveFaStyleHead")
    public Mono<R> saveFaStyleHead(@RequestBody FaStyleHead faStyleHead){
        return faStyleHeadRepository.save(faStyleHead)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaStyle")
    public Mono<R> saveFaStyle(@RequestBody FaStyle faStyle){
        return faStyleRepository.save(faStyle)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaStyleList")
    public Mono<R> saveFaStyleList(@RequestBody List<FaStyle> faStyleList){
        return faStyleRepository.saveAll(faStyleList)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
