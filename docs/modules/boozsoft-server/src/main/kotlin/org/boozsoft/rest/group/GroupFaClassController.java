package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupFaClass;
import org.boozsoft.domain.entity.group.GroupFaProperty;
import org.boozsoft.repo.group.GroupFaClassRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/groupFaClass")
public class GroupFaClassController {

    @Autowired
    GroupFaClassRepository groupFaClassRepository;

    @GetMapping("findAll")
    public Mono<R> findAll(){
        return groupFaClassRepository.findAll().collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody GroupFaClass entity) {
        return groupFaClassRepository.save(entity).map(o -> R.ok().setResult(o));
    }
}
