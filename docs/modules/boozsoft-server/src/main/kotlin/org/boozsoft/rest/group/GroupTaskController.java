package org.boozsoft.rest.group;

import org.boozsoft.domain.entity.group.GroupTask;
import org.boozsoft.repo.group.GroupTaskRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/groupTask")
public class GroupTaskController {

    @Autowired
    GroupTaskRepository groupTaskRepository;

    @PostMapping("groupTaskSave")
    public Mono groupTaskSave(@RequestBody GroupTask object) {
        object.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return groupTaskRepository.save(object).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByFunctionModuleAndIyearAndRecordNum")
    public Mono findByFunctionModuleAndIyearAndRecordNum(String functionModule,String iyear,String orgUnique) {
        return groupTaskRepository.findByFunctionModuleAndIyearAndRecordNum(functionModule,iyear,orgUnique).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("groupTaskDelByUserIdAndOrgUniqueAndFunctionModule")
    public Mono groupTaskDelByUserIdAndOrgUniqueAndFunctionModule(String functionModule,String iyear,String orgUnique,String userId) {
        return groupTaskRepository.groupTaskDelByUserIdAndOrgUniqueAndFunctionModule(functionModule,iyear,orgUnique,userId).then(Mono.just(R.ok()));
    }
}
