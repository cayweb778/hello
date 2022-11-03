package org.boozsoft.rest;

import org.boozsoft.repo.NcmenuRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 *
 */
@RestController
@RequestMapping("ncMenu")
public class NcMenuController {
    @Autowired
    NcmenuRepository repository;

    @GetMapping("byUser")
    public Mono<R> byUser() {

        return repository.findById("1")
                .map(ncmenu -> ncmenu.getMenusJson())
                .map(json->R.ok().setResult(json))
                ;
    }
}
