package org.boozsoft.rest.group;

import org.boozsoft.domain.entity.group.GroupAccPrint;
import org.boozsoft.domain.entity.group.GroupCodeImportTemplate;
import org.boozsoft.repo.group.GroupCodeImportTemplateRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/codeimporttemplate")
public class GroupCodeImportTemplateController {

    @Autowired
    GroupCodeImportTemplateRepository groupCodeImportTemplateRepository;


    @PostMapping("countByTname")
    public Mono<R> countByTname(String tname) {
        return groupCodeImportTemplateRepository.countByTname(tname).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findAll")
    public Mono<R> findAll() {
        return groupCodeImportTemplateRepository.findAllorderflag().collectList().map(o -> R.ok().setResult(o));
    }


    @PostMapping("save")
    public Mono<R> countByTname(@RequestBody GroupCodeImportTemplate t) {
        return groupCodeImportTemplateRepository.save(t).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByIdDel")
    public Mono<R> findByIdDel(@RequestBody Map map) {
        String id=map.get("id").toString();
        return groupCodeImportTemplateRepository.findAllorderflag(Arrays.asList(id.split(","))).then(Mono.just(R.ok()));
    }
}
