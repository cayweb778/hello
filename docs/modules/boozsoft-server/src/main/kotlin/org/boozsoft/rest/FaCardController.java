package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.fa.FaCardMaster;
import org.boozsoft.domain.entity.fa.FaChange;
import org.boozsoft.domain.entity.fa.FaChangeDept;
import org.boozsoft.domain.entity.fa.FaChangeProject;
import org.boozsoft.repo.*;
import org.boozsoft.repo.fa.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/faCard")
public class FaCardController {

    @Autowired
    FaCardMasterRepository faCardMasterRepository;
    @Autowired
    FaChangeRepository faChangeRepository;
    @Autowired
    FaChangeDeptRepository faChangeDeptRepository;
    @Autowired
    FaChangeProjectRepository faChangeProjectRepository;
    @Autowired
    FaAssetTypeRepository faAssetTypeRepository;
    @Autowired
    FaAddCutModeRepository faAddCutModeRepository;
    @Autowired
    FaPropertyRepository faPropertyRepository;
    @Autowired
    SysUnitOfMeaRepository sysUnitOfMeaRepository;
    @Autowired
    FaUsageStatusRepository faUsageStatusRepository;
    @Autowired
    FaZhejiuMethodRepository faZhejiuMethodRepository;
    @Autowired
    FaLocationRepository faLocationRepository;
    @Autowired
    FaDepreciationRepository faDepreciationRepository;
    @Autowired
    FaDepreciationDeptRepository faDepreciationDeptRepository;
    @Autowired
    FaDepreciationProjectRepository faDepreciationProjectRepository;

    @GetMapping("findCardList")
    public Mono<R> findCardList(String manageCode,String cdate,String jianshao,String chaifen,String jiechu){
        String iyear = cdate.substring(0,4);
        String imonth = cdate.substring(5,7);
        return faCardMasterRepository.findCardList(manageCode,cdate,jianshao,chaifen,jiechu,iyear,imonth)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findCardMasterAll")
    public Mono<R> findCardMasterAll(){
        return faCardMasterRepository.findAllByOrderBySysId()
                .collectList()
                .map(R::page);
    }

    @GetMapping("findCardMasterEffectiveAll")
    public Mono<R> findCardMasterEffectiveAll(){
        return faCardMasterRepository.findAllByOrderBySysId()
                .collectList()
                .map(R::ok);
    }

    @GetMapping("findCardMasterByCardUnique")
    public Mono<R> findCardMasterByCardUnique(String cardUnique){
        return faCardMasterRepository.findByCardUniqueOrderBySysId(cardUnique)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCardMasterByIymonth")
    public Mono<R> findCardMasterByIymonth(String iymonth){
        return faCardMasterRepository.findByIymonth(iymonth)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findChangeByCardUnique")
    public Mono<R> findChangeByCardUnique(String cardUnique){
        return faChangeRepository.findFirstByCardUniqueOrderByCdateDesc(cardUnique)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findChangeDeptByCardUnique")
    public Mono<R> findChangeDeptByCardUnique(String cardUnique){
        return faChangeDeptRepository.findByCardUniqueAndNewCdate(cardUnique)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findChangeProjectByCardUnique")
    public Mono<R> findChangeProjectByCardUnique(String cardUnique){
        return faChangeProjectRepository.findByCardUniqueAndNewCdate(cardUnique)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxSysId")
    @ApiOperation(value = "获取最大的系统编号", notes = "获取最大的系统编号")
    public Mono<R> findMaxSysId(){
        return faCardMasterRepository.findMaxSysId()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBySysId")
    @ApiOperation(value = "查询系统编号是否重复", notes = "查询系统编号是否重复")
    public Mono<R> findBySysId(String sysId){
        return faCardMasterRepository.findBySysId(sysId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCardCode")
    @ApiOperation(value = "查询卡片编码是否重复", notes = "查询卡片编码是否重复")
    public Mono<R> findByCardCode(String cardCode){
        return faCardMasterRepository.findByCardCode(cardCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaCardMaster")
    public Mono<R> saveFaCardMaster(@RequestBody FaCardMaster faCardMaster) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(faCardMaster.getCreatTime());
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        //计算第一次折旧年月
        rightNow.add(Calendar.MONTH, Integer.parseInt(faCardMaster.getJitiType()));
        Date dt1 = rightNow.getTime();
        faCardMaster.setFirstTime(sdf.format(dt1));
        /*rightNow.setTime(dt);
        //计算折旧到期日
        rightNow.add(Calendar.MONTH, Integer.parseInt(faCardMaster.getJitiType())+Integer.parseInt(faCardMaster.getLife())-1);
        Date dt2 = rightNow.getTime();
        faCardMaster.setJzdqrTime(sdf.format(dt2));*/
        return faCardMasterRepository.save(faCardMaster)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaChange")
    public Mono<R> saveFaChange(@RequestBody FaChange faChange) throws ParseException {
        if(faChange.getBuyTime()!=null || !faChange.getBuyTime().equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = sdf.parse(faChange.getBuyTime());
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            //计算折旧到期日（根据购买日期计算）
            rightNow.add(Calendar.MONTH, Integer.parseInt(faChange.getJitiType()) - 1);
            Date dt2 = rightNow.getTime();
            faChange.setJzdqrTime(sdf.format(dt2));
        }
        return faChangeRepository.save(faChange)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaChangeDept")
    public Mono<R> saveFaChangeDept(@RequestBody FaChangeDept faChangeDept){
        return faChangeDeptRepository.save(faChangeDept)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaChangeDeptList")
    public Mono<R> saveFaChangeDeptList(@RequestBody List<FaChangeDept> faChangeDeptList){
        return faChangeDeptRepository.saveAll(faChangeDeptList)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaChangeProject")
    public Mono<R> saveFaChangeProject(@RequestBody FaChangeProject faChangeProject){
        return faChangeProjectRepository.save(faChangeProject)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFaChangeProjectList")
    public Mono<R> saveFaChangeProjectList(@RequestBody List<FaChangeProject> faChangeProjectList){
        return faChangeProjectRepository.saveAll(faChangeProjectList)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findFaAssetTypeByFlagAndBend")
    @ApiOperation(value = "查询资产类别", notes = "启用状态末级")
    public Mono<R> findFaAssetTypeByFlagAndBend() {
        return faAssetTypeRepository.findByFlagAndBendOrderByEcCode("1", "1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findFaAddCutModeByFlagAndBend")
    @ApiOperation(value = "查询增减方式", notes = "启用状态末级")
    public Mono<R> findFaAddCutModeByFlagAndBend() {
        return faAddCutModeRepository.findByFlagAndBendOrderByEcCode("1", "1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findFaPropertyByFlag")
    @ApiOperation(value = "查询资产属性", notes = "启用状态")
    public Mono<R> findFaPropertyByFlag() {
        return faPropertyRepository.findByFlagOrderByPeopertyId("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findUnitOfMeaByFlag")
    @ApiOperation(value = "查询计量单位", notes = "启用状态")
    public Mono<R> findUnitOfMeaByFlag() {
        return sysUnitOfMeaRepository.findAllByFlag()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findFaUsageStatusByFlagAndBend")
    @ApiOperation(value = "查询使用状态", notes = "启用状态末级")
    public Mono<R> findFaUsageStatusByFlagAndBend() {
        return faUsageStatusRepository.findByFlagAndBendOrderByEcCode("1", "1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findZhejiuMethodByFlag")
    @ApiOperation(value = "查询折旧方式", notes = "启用状态")
    public Mono<R> findZhejiuMethodByFlag() {
        return faZhejiuMethodRepository.findByFlagOrderByCreateDate("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findLocationByFlagAndBend")
    @ApiOperation(value = "查询存放位置", notes = "启用状态")
    public Mono<R> findLocationByFlagAndBend() {
        return faLocationRepository.findByFlagAndBendOrderByEcCode("1", "1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteCardMasterByCardUnique")
    public Mono<R> deleteCardMasterByCardUnique(String cardUnique){
        return faCardMasterRepository.deleteByCardUnique(cardUnique)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteChangeByCardUnique")
    public Mono<R> deleteChangeByCardUnique(String cardUnique){
        return faChangeRepository.deleteByCardUnique(cardUnique)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteChangeDeptByCardUnique")
    public Mono<R> deleteChangeDeptByCardUnique(String cardUnique){
        return faChangeDeptRepository.deleteByCardUnique(cardUnique)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteChangeProjectByCardUnique")
    public Mono<R> deleteChangeProjectByCardUnique(String cardUnique){
        return faChangeProjectRepository.deleteByCardUnique(cardUnique)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteDepreciationByCardUnique")
    public Mono<R> deleteDepreciationByCardUnique(String cardUnique){
        Mono mono1 = faDepreciationRepository.deleteByCardUnique(cardUnique);
        Mono mono2 = faDepreciationDeptRepository.deleteByCardUnique(cardUnique);
        Mono mono3 = faDepreciationProjectRepository.deleteByCardUnique(cardUnique);
        return Mono.zip(mono1,mono2,mono3)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findCardMasterByCdateList")
    @ApiOperation(value = "查询存放位置", notes = "启用状态")
    public Mono<R> findCardMasterByCdateList(String manageCode,String cdate) {
        return faCardMasterRepository.findAllByManageCodeAndCreatTime(manageCode,cdate)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
