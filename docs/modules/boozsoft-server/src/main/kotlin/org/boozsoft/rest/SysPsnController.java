package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.SysPsn;
import org.boozsoft.domain.entity.account.SysPsnRollback;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.SysDepartmentRepository;
import org.boozsoft.repo.SysPsnRepository;
import org.boozsoft.repo.SysPsnRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.boozsoft.util.DesUtil;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/psn")
public class SysPsnController {

//    @Autowired
//    OAuth2ClientProperties oAuth2ClientProperties;

    @Autowired
    SysDepartmentRepository sysDepartmentRepository;
    @Autowired
    SysPsnRepository sysPsnRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(){
        return sysPsnRepository.findAllByIsDelOrderByPsnCode("0")
                .collectList()
                .map(list->{
                    List<SysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            SysPsn psn = list.get(i);
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
    public Mono<R> findAll(Pageable pageable,String uniqueCodeDept,String uniquePsnType,String flag){
        /*AtomicReference<Long> totalAR = new AtomicReference(0);
        return sysPsnRepository.findAllByOrderByPsnCode().collectList()
                .map(list -> {
                    totalAR.set((long) list.size());
                    if (StrUtil.isNotBlank(uniqueCodeDept) && !uniqueCodeDept.equals("0") && !uniqueCodeDept.equals("undefined")) {
                        Set<String> deptIds = StringUtils.isBlank(uniqueCodeDept) ? new HashSet<>() : new HashSet<>(Arrays.asList(uniqueCodeDept.split(",")));
                        return list.stream().filter(item -> deptIds.contains(item.getUniqueCodeDept()))
                                .collect(Collectors.toList());
                    }
                    return list;
                })
                .map(list->{
                    totalAR.set((long) list.size());
                    if (StrUtil.isNotBlank(uniquePsnType) && !uniquePsnType.equals("0") && !uniquePsnType.equals("undefined")) {
                        Set<String> psnTypeIds = StringUtils.isBlank(uniquePsnType) ? new HashSet<>() : new HashSet<>(Arrays.asList(uniquePsnType.split(",")));
                        return list.stream().filter(item -> psnTypeIds.contains(item.getUniquePsnType()))
                                .collect(Collectors.toList());
                    }
                    return list;
                })
                .map(list -> {
                    totalAR.set((long) list.size());
                    if (StrUtil.isNotBlank(flag)) {
                        return list.stream().filter(item -> flag.contains(item.getFlag()))
                                .collect(Collectors.toList());
                    }
                    return list;
                })
                .map(list->R.page(list,pageable,(totalAR.get())));*/
        if (StrUtil.isNotBlank(uniqueCodeDept) && !uniqueCodeDept.equals("0") && !uniqueCodeDept.equals("undefined")){
            List<String> deptIds = StringUtils.isBlank(uniqueCodeDept) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(uniqueCodeDept.split(",")));
            if (StrUtil.isNotBlank(flag)){
                return sysPsnRepository.findAllByUniqueCodeDeptInAndFlagAndIsDelOrderByPsnCode(deptIds,flag,"0",pageable)
                        .collectList()
                        .flatMap(item-> sysPsnRepository.countAllByUniqueCodeDeptInAndFlagAndIsDel(deptIds,flag,"0").map(total->R.page(item,pageable,total)));
            }
            return sysPsnRepository.findAllByUniqueCodeDeptInAndIsDelOrderByPsnCode(deptIds,"0",pageable)
                    .collectList()
                    .flatMap(item-> sysPsnRepository.countAllByUniqueCodeDeptInAndIsDel(deptIds,"0").map(total->R.page(item,pageable,total)));
        } else if (StrUtil.isNotBlank(uniquePsnType) && !uniquePsnType.equals("0") && !uniquePsnType.equals("undefined")) {
            if (StrUtil.isNotBlank(flag)){
                return sysPsnRepository.findAllByUniquePsnTypeAndFlagAndIsDelOrderByPsnCode(uniquePsnType,flag,"0",pageable)
                        .collectList()
                        .flatMap(item-> sysPsnRepository.countAllByUniquePsnTypeAndFlagAndIsDel(uniquePsnType,flag,"0").map(total->R.page(item,pageable,total)));
            }
            return sysPsnRepository.findAllByUniquePsnTypeAndIsDelOrderByPsnCode(uniquePsnType,"0",pageable)
                    .collectList()
                    .flatMap(item-> sysPsnRepository.countAllByUniquePsnTypeAndIsDel(uniquePsnType,"0").map(total->R.page(item,pageable,total)));
        } else if (StrUtil.isNotBlank(flag)) {
            return sysPsnRepository.findByFlagAndIsDelOrderByPsnCode(flag,"0",pageable)
                    .collectList()
                    .flatMap(item-> sysPsnRepository.countByFlagAndIsDel(flag,"0").map(total->R.page(item,pageable,total)));
        }
        return sysPsnRepository.findAllByIsDelOrderByPsnCode("0",pageable)
                .collectList()
                .flatMap(item-> sysPsnRepository.countAllByIsDel("0").map(total->R.page(item,pageable,total)));
    }

    @GetMapping("findAllByDept")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByDept(Pageable pageable, String deptNum){
        /*sysDepartmentRepository.findByFlagOrderById("1").collectList().map(list->{
            SysDepartment department = list.stream().filter(enrity -> enrity.getUniqueCode().equals(deptNum)).collect(Collectors.toList()).get(0);
            List<SysDepartment> nodes = list.stream().filter(entity -> entity.getParentId().equals(department.getId())).collect(Collectors.toList());
         *//*   if (nodes.size() > 0){

            }
            return '';*//*
        })*/
        return sysPsnRepository.findAllByUniqueCodeDeptInAndIsDelOrderByPsnCode(new ArrayList<>(),"0", pageable).collectList().map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return sysPsnRepository.findByFlagAndIsDelOrderByPsnCode("1","0").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")

    public Mono findById(String id){
        Mono mono = sysPsnRepository.findById(id);
        return mono;
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")

    public Mono save(@RequestBody SysPsn object){
        if (object.getUniqueCode()==null || object.getUniqueCode().equals("")){
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getFlag()==null || object.getFlag().equals("")){
            object.setFlag("1");
        }
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        if (object.getCreateDate()==null || object.getCreateDate().equals("")){
            object.setCreateDate(sdf.format(new Date()));
        }
        if (object.getBirthDate()!=null && !object.getBirthDate().equals("")){
            object.setBirthDate(object.getBirthDate().substring(0,10));
        }
        if (object.getEntryDate()!=null && !object.getEntryDate().equals("")){
            object.setEntryDate(object.getEntryDate().substring(0, 10));
        }
        if (object.getLeaveDate() != null && !object.getLeaveDate().equals("")) {
            object.setLeaveDate(object.getLeaveDate().substring(0, 10));
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
        //增加
        if (object.getId() == null || object.getId().equals("")) {
            return sysPsnRepository.save(object)
                    .map(o -> R.ok().setResult(o));
        }
        return sysPsnRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")

    public Mono delete(@RequestBody SysPsn object){
        return sysPsnRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")

    public Mono editFlag(@RequestBody SysPsn object) {
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
        //修改
        return sysPsnRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String code) {
        return sysPsnRepository.countByPsnCodeAndIsDel(code,"0")
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SysPsn> object){
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
                    List<SysPsn> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            SysPsn psn = list.get(i);
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

    @GetMapping("findAllByCpersonId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByCpersonId(String cpersonId){
        return accvoucherRepository.findAllByCpersonId(cpersonId)
                .collectList()
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
