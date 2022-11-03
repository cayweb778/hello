package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.TempCgdhd;
import org.boozsoft.repo.stock.TempCgdhdRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/temp/cgdhd")
public class TempCgdhdController {

    @Autowired
    private TempCgdhdRepository tempCgdhdRepository;

    @PostMapping("save")
    public Mono<R> save(@RequestBody List<TempCgdhd> object) {
        return tempCgdhdRepository.saveAll(object).collectList().map(a->R.ok().setResult(a));
    }


}
