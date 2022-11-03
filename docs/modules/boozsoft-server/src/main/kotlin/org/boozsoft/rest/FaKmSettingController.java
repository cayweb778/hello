package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FaCardColumn;
import org.boozsoft.domain.entity.FaKmGdjz;
import org.boozsoft.domain.entity.FaKmLjzj;
import org.boozsoft.domain.entity.fa.FaChange;
import org.boozsoft.domain.vo.FaKmGdjzVo;
import org.boozsoft.domain.vo.group.GroupExpenditureClassVo;
import org.boozsoft.repo.*;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/sys/faKmSetting")
public class FaKmSettingController {

    @Autowired
    FaKmGdjzRepository faKmGdjzRepository;
    @Autowired
    FaKmDeptRepository faKmDeptRepository;
    @Autowired
    FaKmLjzjRepository faKmLjzjRepository;
    @Autowired
    FaKmProjectRepository faKmProjectRepository;

    @PostMapping("getGdzcList")
    public Mono<R> getGdzcList(@RequestBody Map maps){
        String flag = maps.get("flag").toString();
        Mono mono = null;
        if("0".equals(flag)){
            mono =  faKmGdjzRepository.findAllGdjz().collectList();
        }else if("1".equals(flag)){
            mono =  faKmLjzjRepository.findAllLjzj().collectList();
        }else if("2".equals(flag)){
            mono =  faKmDeptRepository.findAllDept().collectList();
        }else if("3".equals(flag)){
            mono =  faKmProjectRepository.findAllProject().collectList();
        }
        return mono.map(o-> R.ok().setResult(o));
    }

    @PostMapping("save")
    public Mono<R> save(@RequestBody FaKmGdjzVo obj){
        Mono mono = null;
        String flag = obj.getFlg();
        if("0".equals(flag)){
            FaKmGdjz gdjz =  new FaKmGdjz();
            if(Objects.nonNull(obj.getId())) {
                gdjz.setId(obj.getId());
            }
            gdjz.setCcode(obj.getKmcode());
            gdjz.setAssetTypeId(obj.getEcode());
            gdjz.setCreateDate(LocalDate.now().toString());
            gdjz.setUniqueCodeUser("dev");
            mono =  faKmGdjzRepository.save(gdjz);
        }else if("1".equals(flag)){
            FaKmLjzj gdjz =  new FaKmLjzj();
            if(Objects.nonNull(obj.getId())) {
                gdjz.setId(obj.getId());
            }
            gdjz.setCcode(obj.getKmcode());
            gdjz.setAssetTypeId(obj.getEcode());
            gdjz.setCreateDate(LocalDate.now().toString());
            gdjz.setUniqueCodeUser("dev");
            mono =  faKmLjzjRepository.save(gdjz);
        }else if("2".equals(flag)){
            FaKmLjzj gdjz =  new FaKmLjzj();
            if(Objects.nonNull(obj.getId())) {
                gdjz.setId(obj.getId());
            }
            gdjz.setCcode(obj.getKmcode());
            gdjz.setAssetTypeId(obj.getEcode());
            gdjz.setCreateDate(LocalDate.now().toString());
            gdjz.setUniqueCodeUser("dev");
            mono =  faKmDeptRepository.findAllDept().collectList();
        }else if("3".equals(flag)){
            mono =  faKmProjectRepository.findAllProject().collectList();
        }
        return mono.thenReturn(R.ok());
    }

}
