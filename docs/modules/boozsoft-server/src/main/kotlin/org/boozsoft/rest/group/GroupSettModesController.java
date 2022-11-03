package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysSettModes;
import org.boozsoft.repo.group.GroupSysSettModesRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/groupSettModes")
public class GroupSettModesController {

    @Autowired
    GroupSysSettModesRepository settModesRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return settModesRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return settModesRepository.findByFlagOrderById("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String settModesCode){
        return settModesRepository.findBySettModesCodeOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysSettModes object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag()==null || object.getFlag().equals("")) {
            object.setFlag("1");
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            object.setCreateDate(time);
        }
        return settModesRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysSettModes object){
        return settModesRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupSysSettModes object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag().equals("1")){
            object.setStopDate(time);
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return settModesRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

}
