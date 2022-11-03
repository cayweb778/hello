package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysPsn;
import org.boozsoft.repo.group.GroupSysPsnRepository;
import org.boozsoft.util.DesUtil;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/groupSys/psn")
public class GroupSysPsnController {

    @Autowired
    GroupSysPsnRepository sysPsnRepository;

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(){
        return sysPsnRepository.findAllByIsDelOrderByPsnCode("0")
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable,String uniquePsnType, String flag){
        /*AtomicReference<Long> totalAR = new AtomicReference(0);
        return sysPsnRepository.findAllByOrderById().collectList()
                .map(list->{
                    totalAR.set((long) list.size());
                    if (StrUtil.isNotBlank(uniquePsnType) && !uniquePsnType.equals("0") && !uniquePsnType.equals("undefined")) {
                        Set<String> psnTypeIds = StringUtils.isBlank(uniquePsnType) ? new HashSet<>() : new HashSet<>(Arrays.asList(uniquePsnType.split(",")));
                        return list.stream().filter(item -> psnTypeIds.contains(item.getUniquePsnType()))
                                .collect(Collectors.toList());
                    }
                    return list;
                })
                .map(list->{
                    totalAR.set((long) list.size());
                    if (StrUtil.isNotBlank(flag)) {
                        return list.stream().filter(item -> flag.contains(item.getFlag()))
                                .collect(Collectors.toList());
                    }
                    return list;
                })
                .map(list->R.page(list,pageable,(totalAR.get())));*/
        if (StrUtil.isNotBlank(uniquePsnType) && !uniquePsnType.equals("0") && !uniquePsnType.equals("undefined")) {
            if (StrUtil.isNotBlank(flag)){
                return sysPsnRepository.findAllByUniquePsnTypeAndFlagAndIsDelOrderByPsnCode(pageable,uniquePsnType,flag,"0")
                        .collectList()
                        .flatMap(item-> sysPsnRepository.countAllByUniquePsnTypeAndFlagAndIsDel(uniquePsnType,flag,"0").map(total->R.page(item,pageable,total)));
            }
            return sysPsnRepository.findAllByUniquePsnTypeAndIsDelOrderByPsnCode(pageable,uniquePsnType,"0")
                    .collectList()
                    .flatMap(item-> sysPsnRepository.countAllByUniquePsnTypeAndIsDel(uniquePsnType,"0").map(total->R.page(item,pageable,total)));
        } else if (StrUtil.isNotBlank(flag)) {
            return sysPsnRepository.findByFlagAndIsDelOrderByPsnCode(pageable,flag,"0")
                    .collectList()
                    .flatMap(item-> sysPsnRepository.countByFlagAndIsDel(flag,"0").map(total->R.page(item,pageable,total)));
        }
        return sysPsnRepository.findAllByIsDelOrderByPsnCode(pageable,"0")
                .collectList()
                .flatMap(item-> sysPsnRepository.countAllByIsDel("0").map(total->R.page(item,pageable,total)));
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return sysPsnRepository.findByFlagAndIsDelOrderByPsnCode("1","0")
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findById(String id){
        Mono mono = sysPsnRepository.findById(id);
        return mono.map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysPsn object){
        if (object.getUniqueCode()==null || object.getUniqueCode().equals("")){
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getFlag()==null || object.getFlag().equals("")){
            object.setFlag("1");
        }
        SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        if (object.getCreateDate()==null || object.getCreateDate().equals("")){
            object.setCreateDate(sdf.format(new Date()));
        }
        if (object.getBirthDate()!=null && !object.getBirthDate().equals("")){
            object.setBirthDate(object.getBirthDate().substring(0,10));
        }
        if (object.getEntryDate()!=null && !object.getEntryDate().equals("")){
            object.setEntryDate(object.getEntryDate().substring(0,10));
        }
        if (object.getLeaveDate()!=null && !object.getLeaveDate().equals("")){
            object.setLeaveDate(object.getLeaveDate().substring(0,10));
        }
        try {
            DesUtil putong = new DesUtil("national");
            //手机号加密
            if (object.getCellPhoneNum()!=null && !object.getCellPhoneNum().equals("")) {
                object.setCellPhoneNum(putong.encrypt(object.getCellPhoneNum()));
            }
            //邮箱加密
            if (object.getPsnEmail()!=null && !object.getPsnEmail().equals("")) {
                object.setPsnEmail(putong.encrypt(object.getPsnEmail()));
            }
            //身份证号加密
            if (object.getDocumentCode()!=null && !object.getDocumentCode().equals("")) {
                object.setDocumentCode(putong.encrypt(object.getDocumentCode()));
            }
            //银行账号加密
            if (object.getBankAccount()!=null && !object.getBankAccount().equals("")) {
                object.setBankAccount(putong.encrypt(object.getBankAccount()));
            }
            //微信号加密
            if (object.getPsnWechat()!=null && !object.getPsnWechat().equals("")) {
                object.setPsnWechat(putong.encrypt(object.getPsnWechat()));
            }
            //钉钉号加密
            if (object.getPsnQq()!=null && !object.getPsnQq().equals("")) {
                object.setPsnQq(putong.encrypt(object.getPsnQq()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sysPsnRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysPsn object){
        return sysPsnRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupSysPsn object) {
        try {
            DesUtil putong = new DesUtil("national");
            //手机号加密
            if (object.getCellPhoneNum()!=null && !object.getCellPhoneNum().equals("")) {
                object.setCellPhoneNum(putong.encrypt(object.getCellPhoneNum()));
            }
            //邮箱加密
            if (object.getPsnEmail()!=null && !object.getPsnEmail().equals("")) {
                object.setPsnEmail(putong.encrypt(object.getPsnEmail()));
            }
            //身份证号加密
            if (object.getDocumentCode()!=null && !object.getDocumentCode().equals("")) {
                object.setDocumentCode(putong.encrypt(object.getDocumentCode()));
            }
            //银行账号加密
            if (object.getBankAccount()!=null && !object.getBankAccount().equals("")) {
                object.setBankAccount(putong.encrypt(object.getBankAccount()));
            }
            //微信号加密
            if (object.getPsnWechat()!=null && !object.getPsnWechat().equals("")) {
                object.setPsnWechat(putong.encrypt(object.getPsnWechat()));
            }
            //钉钉号加密
            if (object.getPsnQq()!=null && !object.getPsnQq().equals("")) {
                object.setPsnQq(putong.encrypt(object.getPsnQq()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return sysPsnRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String code) {
        return sysPsnRepository.countByPsnCode(code)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<GroupSysPsn> object){
        return Flux.fromIterable(object).map(item -> {
                    if (item.getUniqueCode()==null || item.getUniqueCode().equals("")){
                        item.setUniqueCode(IdUtil.objectId());
                    }
                    if (item.getPsnCode()==null || item.getPsnCode().equals("")){
                        item.setPsnCode(String.format("%04d",new Random().nextInt(9999)));
                    }
                    if (item.getFlag()==null || item.getFlag().equals("")){
                        item.setFlag("1");
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
                    if (item.getCreateDate()==null || item.getCreateDate().equals("")){
                        item.setCreateDate(sdf.format(new Date()));
                    }
                    if (item.getBirthDate()!=null && !item.getBirthDate().equals("")){
                        item.setBirthDate(item.getBirthDate().substring(0,10));
                    }
                    if (item.getEntryDate()!=null && !item.getEntryDate().equals("")){
                        item.setEntryDate(item.getEntryDate().substring(0,10));
                    }
                    if (item.getLeaveDate()!=null && !item.getLeaveDate().equals("")){
                        item.setLeaveDate(item.getLeaveDate().substring(0,10));
                    }
                    try {
                        DesUtil putong = new DesUtil("national");
                        //手机号加密
                        if (item.getCellPhoneNum()!=null && !item.getCellPhoneNum().equals("")) {
                            item.setCellPhoneNum(putong.encrypt(item.getCellPhoneNum()));
                        }
                        //邮箱加密
                        if (item.getPsnEmail()!=null && !item.getPsnEmail().equals("")) {
                            item.setPsnEmail(putong.encrypt(item.getPsnEmail()));
                        }
                        //身份证号加密
                        if (item.getDocumentCode()!=null && !item.getDocumentCode().equals("")) {
                            item.setDocumentCode(putong.encrypt(item.getDocumentCode()));
                        }
                        //银行账号加密
                        if (item.getBankAccount()!=null && !item.getBankAccount().equals("")) {
                            item.setBankAccount(putong.encrypt(item.getBankAccount()));
                        }
                        //微信号加密
                        if (item.getPsnWechat()!=null && !item.getPsnWechat().equals("")) {
                            item.setPsnWechat(putong.encrypt(item.getPsnWechat()));
                        }
                        //钉钉号加密
                        if (item.getPsnQq()!=null && !item.getPsnQq().equals("")) {
                            item.setPsnQq(putong.encrypt(item.getPsnQq()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(sysPsnRepository::saveAll)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findDelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDelList() {
        return sysPsnRepository.findAllByIsDelOrderByPsnCode("1")
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByUniqueCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByUniqueCode(String uniqueCode) {
        return sysPsnRepository.findByUniqueCode(uniqueCode)
                .map(item -> {
                    try {
                        DesUtil putong = new DesUtil("national");
                        item.setCellPhoneNum(putong.decrypt(item.getCellPhoneNum()));
                        item.setPsnEmail(putong.decrypt(item.getPsnEmail()));
                        item.setDocumentCode(putong.decrypt(item.getDocumentCode()));
                        item.setBankAccount(putong.decrypt(item.getBankAccount()));
                        item.setPsnWechat(putong.decrypt(item.getPsnWechat()));
                        item.setPsnQq(putong.decrypt(item.getPsnQq()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return item;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCtypeAndOriginId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCtypeAndOriginId(String originId) {
        return sysPsnRepository.findByCtypeAndOriginId("1",originId)
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCtypeAndTenantId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCtypeAndTenantId(String tenantId) {
        return sysPsnRepository.findByCtypeAndTenantId("2",tenantId)
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByPsnNameAndCellPhoneNumAndSuccessState")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByPsnNameAndCellPhoneNumAndSuccessState(String psnName,String cellPhoneNum) {
        if(cellPhoneNum==null || cellPhoneNum.equals("")){
            return sysPsnRepository.findByPsnNameAndSuccessState(psnName,"1")
                    .collectList()
                    .map(list->{
                        List<GroupSysPsn> psnList = new ArrayList<>();
                        try {
                            DesUtil putong = new DesUtil("national");
                            for (int i = 0; i < list.size(); i++) {
                                GroupSysPsn psn = list.get(i);
                                psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                                psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                                psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                                psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                                psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                                psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                                psnList.add(psn);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return psnList;
                    })
                    .map(o -> R.ok().setResult(o));
        }
        return sysPsnRepository.findByPsnNameAndCellPhoneNumAndSuccessState(psnName, cellPhoneNum,"1")
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByPsnNameAndCellPhoneNum")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByPsnNameAndCellPhoneNum(String psnName,String cellPhoneNum) {
        if(cellPhoneNum==null || cellPhoneNum.equals("")){
            return sysPsnRepository.findByPsnName(psnName)
                    .collectList()
                    .map(list->{
                        List<GroupSysPsn> psnList = new ArrayList<>();
                        try {
                            DesUtil putong = new DesUtil("national");
                            for (int i = 0; i < list.size(); i++) {
                                GroupSysPsn psn = list.get(i);
                                psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                                psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                                psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                                psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                                psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                                psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                                psnList.add(psn);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return psnList;
                    })
                    .map(o -> R.ok().setResult(o));
        }
        return sysPsnRepository.findByPsnNameAndCellPhoneNum(psnName, cellPhoneNum)
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByNotSuccessState")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNotSuccessState() {
        return sysPsnRepository.findByNotSuccessState()
                .collectList()
                .map(list->{
                    List<GroupSysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsn psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psn.setPsnEmail(putong.decrypt(psn.getPsnEmail()));
                            psn.setDocumentCode(putong.decrypt(psn.getDocumentCode()));
                            psn.setBankAccount(putong.decrypt(psn.getBankAccount()));
                            psn.setPsnWechat(putong.decrypt(psn.getPsnWechat()));
                            psn.setPsnQq(putong.decrypt(psn.getPsnQq()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongPsnCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongPsnCode(Integer num,Integer sum,Integer qzNum,String qianzhui) {
        return sysPsnRepository.findBukongPsnCode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxPsnCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxPsnCode(Integer num,Integer sum,Integer qzNum,String qianzhui) {
        return sysPsnRepository.findMaxPsnCode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

}
