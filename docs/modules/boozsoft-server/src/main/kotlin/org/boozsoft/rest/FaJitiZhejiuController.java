package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.fa.FaDepreciation;
import org.boozsoft.domain.entity.fa.FaDepreciationDept;
import org.boozsoft.domain.entity.fa.FaDepreciationProject;
import org.boozsoft.domain.entity.group.GroupFaAccPeriod;
import org.boozsoft.repo.fa.FaCardMasterRepository;
import org.boozsoft.repo.fa.FaDepreciationDeptRepository;
import org.boozsoft.repo.fa.FaDepreciationProjectRepository;
import org.boozsoft.repo.fa.FaDepreciationRepository;
import org.boozsoft.repo.group.GroupFaAccPeriodRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/faJitiZhejiu")
public class FaJitiZhejiuController {

    @Autowired
    FaDepreciationRepository faDepreciationRepository;
    @Autowired
    FaDepreciationDeptRepository faDepreciationDeptRepository;
    @Autowired
    FaDepreciationProjectRepository faDepreciationProjectRepository;
    @Autowired
    GroupFaAccPeriodRepository groupFaAccPeriodRepository;

    @GetMapping("findPeriodByUniqueCode")
    public Mono<R> findPeriodByUniqueCode(String code,String year){
        return groupFaAccPeriodRepository.findByUniqueCodeAndIyearOrderByImonth(code,year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByIyearAndImonth")
    public Mono<R> findByIyearAndImonth(String code,String year,String month){
        return faDepreciationRepository.findByIyearAndImonth(code, year, month)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByManageCodeAndIyearAndImonth")
    public Mono<R> findByManageCodeAndIyearAndImonth(String code,String year,String month){
        return faDepreciationRepository.findByManageCodeAndIyearAndImonth(code, year, month)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findDeptByManageCodeAndIyearAndImonth")
    public Mono<R> findDeptByManageCodeAndIyearAndImonth(String code,String year,String month){
        return faDepreciationDeptRepository.findByManageCodeAndIyearAndImonth(code, year, month)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findProjectByManageCodeAndIyearAndImonth")
    public Mono<R> findProjectByManageCodeAndIyearAndImonth(String code,String year,String month){
        return faDepreciationProjectRepository.findByManageCodeAndIyearAndImonth(code, year, month)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaDepreciation")
    public Mono<R> saveFaDepreciation(@RequestBody FaDepreciation faDepreciation){
        return faDepreciationRepository.save(faDepreciation)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaDepreciationList")
    public Mono<R> saveFaDepreciationList(@RequestBody List<FaDepreciation> faDepreciation){
        return faDepreciationRepository.saveAll(faDepreciation)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteByManageCodeAndIyearAndImonth")
    public Mono<R> deleteByManageCodeAndIyearAndImonth(String code,String year,String month){
        Mono mono1 = faDepreciationRepository.deleteByManageCodeAndIyearAndImonth(code, year, month);
        Mono mono2 = faDepreciationDeptRepository.deleteByManageCodeAndIyearAndImonth(code, year, month);
        Mono mono3 = faDepreciationProjectRepository.deleteByManageCodeAndIyearAndImonth(code, year, month);
        return Mono.zip(mono1,mono2,mono3)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("saveFaPeriod")
    public Mono<R> saveFaPeriod(@RequestBody GroupFaAccPeriod period){
        return groupFaAccPeriodRepository.save(period)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaDepreciationDept")
    public Mono<R> saveFaDepreciationDept(@RequestBody FaDepreciationDept faDepreciationDept){
        return faDepreciationDeptRepository.save(faDepreciationDept)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaDepreciationDeptList")
    public Mono<R> saveFaDepreciationDeptList(@RequestBody List<FaDepreciationDept> faDepreciationDept){
        return faDepreciationDeptRepository.saveAll(faDepreciationDept)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaDepreciationProject")
    public Mono<R> saveFaDepreciationProject(@RequestBody FaDepreciationProject faDepreciationProject){
        return faDepreciationProjectRepository.save(faDepreciationProject)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaDepreciationProjectList")
    public Mono<R> saveFaDepreciationProjectList(@RequestBody List<FaDepreciationProject> faDepreciationProject){
        return faDepreciationProjectRepository.saveAll(faDepreciationProject)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("/getTotalData")
    @ApiOperation(value = "获取所有数据总条数", notes = "获取所有数据总条数")
    public Mono getTotalData(){
        return faDepreciationProjectRepository.findAll()
                .collectList()
                .map(v-> v.size())
                .map(v-> R.ok().setResult(v));
    }

}
