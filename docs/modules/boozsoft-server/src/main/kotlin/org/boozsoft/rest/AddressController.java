package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.AdministrativeZone;
import org.boozsoft.repo.AdministrativeZoneRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/address")
public class AddressController {

//    @Autowired
//    OAuth2ClientProperties oAuth2ClientProperties;
    @Autowired
    AdministrativeZoneRepository administrativeZoneRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll( Pageable pageable){

        return administrativeZoneRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }


    @GetMapping("findByZoneId")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findByZoneId(String id){
        Mono mono = administrativeZoneRepository.findById(id);
        return mono;
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody AdministrativeZone object){
//        String s = IdUtil.fastSimpleUUID();
//        String s1 = IdUtil.objectId();
        if (object.getUniqueCode()==null){
            object.setUniqueCode(IdUtil.objectId());
        }
        return administrativeZoneRepository.save(object).map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(String id){
        return administrativeZoneRepository.deleteById(id).then(Mono.just(R.ok()));
    }

}
