package org.boozsoft.rest.group;

import org.boozsoft.domain.entity.group.GroupServerConfigure;
import org.boozsoft.domain.entity.group.GroupTask;
import org.boozsoft.repo.group.GroupServerConfigureRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/group/config")
public class GroupServerConfigureController {

    @Autowired
    private GroupServerConfigureRepository serverConfigureRepository;

    @PostMapping
    public Mono save(@RequestBody GroupServerConfigure object) {
       return serverConfigureRepository.findAll().collectList().flatMap(list->{
            if (list.size()>0)return serverConfigureRepository.deleteAll(list).thenReturn("");
            return Mono.just("");
        }).flatMap(s->serverConfigureRepository.save(object).map(R::ok));
    }

    @GetMapping
    public Mono get() {
        return serverConfigureRepository.findAll().collectList().map(R::ok);
    }

}
