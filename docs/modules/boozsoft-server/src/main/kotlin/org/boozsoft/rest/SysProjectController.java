package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.SysNotice;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.repo.GroupDistRepository;
import org.boozsoft.repo.SysNoticeRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.CustomerService;
import org.boozsoft.service.ProjectService;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sysProject")
public class SysProjectController {
    @Autowired
    ProjectService projectService;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    SysLogService sysLogService;
    @Autowired
    SysNoticeRepository sysNoticeRepository;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProjectRepositoryBase projectRepository;

    /**
     * 查询  集团档案分配管控权限表
     * @return
     */
    @GetMapping("findByDatabaseUniqueCode")
    
    public Mono<R> findByDatabaseUniqueCode(String projectCateCode) {
        return groupDistRepository.findByDatabaseUniqueCodeAndTableName("abc001","project_"+projectCateCode)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByClass")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByClass(Pageable pageable,String projectCateCode,String projectClassCode) {
        return projectService.findByClass(pageable,projectCateCode,projectClassCode);
    }

    @GetMapping("accFindByClass")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> accFindByClass(Pageable pageable,String projectCateCode,String projectClassCode) {
        return projectService.acc_findByClass(pageable,projectCateCode,projectClassCode);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody Project project) {
        //新增
        if (project.getId()==null || project.getId().equals("")){
            return add(project);
        }
        return edit(project);
    }

    /**
     * 新增方法
     * @param project
     * @return
     */
    public Mono add(@RequestBody Project project) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        project.setApplyDatabaseUniqueCode("abc001");//申请账套唯一码
        project.setApplyUser("test");//申请人
        project.setApplyDate(time);
        project.setBiandongMethod("1");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】增加了一条项目信息,项目编码：【"+project.getProjectCode()+"】,项目名称：【"+project.getProjectName()+"】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("user001");
        sysNotice.setNoticeType("增加");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont());
        sysNotice.setNoticeName("项目目录");
        sysNotice.setNoticeCode("project_"+project.getProjectCateCode());
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        //管控
        if (project.getIsControl().equals("1")){
            //是否自动分配
            //自动分配系统和账套同时添加
            if (project.getIsAuto().equals("1")){
                project.setSuccessState("1");
                //系统保存项目
                return projectService.sys_save(project)
                        //账套保存项目
                        .flatMap(item -> projectService.acc_save(project))
                        //增加集团档案分配表
                        .flatMap(item -> customerService.sysDistRecord_save("abc001", project.getProjectCateCode(), "project_"+project.getProjectCateCode()))
                        //增加系统日志表
                        .flatMap(item -> sysLogService.save_log(sysLog))
                        //增加系统通知信息表
                        .flatMap(item -> sysNoticeRepository.save(sysNotice))
                        .map(item -> R.ok().setResult(item));
            }
            //不自动分配系统添加需要走审批
            project.setSuccessState("0");
            sysLog.setAccId("booznc-group");
            //系统增加项目
            return projectService.sys_save(project)
                    //增加系统日志表
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(item -> R.ok().setResult(item));
        }
        //不管控
        project.setSuccessState("1");
        //账套增加项目
        return projectService.acc_save(project)
                //增加系统日志表
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(item -> R.ok().setResult(item));
    }

    /**
     * 修改方法
     * @param project
     * @return
     */
    public Mono edit(@RequestBody Project project) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】修改了一条项目信息,项目编码：【"+project.getProjectCode()+"】,项目名称：【"+project.getProjectName()+"】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("user001");
        sysNotice.setNoticeType("增加");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont());
        sysNotice.setNoticeName("项目目录");
        sysNotice.setNoticeCode("project_"+project.getProjectCateCode());
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        //管控
        if (project.getIsControl().equals("1")){
            //是否自动分配
            //自动分配系统和账套同时添加
            if (project.getIsAuto().equals("1")){
                project.setSuccessState("1");
                //系统保存项目和回滚表信息
                return projectService.sys_edit(project, "1", "abc001", "user001")
                        //账套保存项目
                        .flatMap(item -> projectService.acc_edit(project, "1", "abc001", "test"))
                        //增加系统日志表
                        .flatMap(item -> sysLogService.save_log(sysLog))
                        //增加系统通知信息表
                        .flatMap(item -> sysNoticeRepository.save(sysNotice))
                        .map(item -> R.ok().setResult(item));
            }
            //不自动分配系统添加需要走审批
            sysLog.setAccId("booznc-group");
            //生效时需要新增一条大类信息
            if (project.getSuccessState().equals("1")) {
                project.setId(null);
                project.setSuccessState("0");
                project.setApplyDate(time);
            }
            //系统增加项目
            return projectService.sys_save(project)
                    //增加系统日志表
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(item -> R.ok().setResult(item));
        }
        //不管控
        //账套保存项目和回滚表信息
        return projectService.acc_edit(project, "1", "abc001", "test")
                //增加系统日志表
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(item -> R.ok().setResult(item));
    }

    @PostMapping("sysSave")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono sysSave(@RequestBody Project project) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        project.setSuccessState("1");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("booznc-group");
        sysLog.setIyear(year);
        //新增
        if (project.getId() == null || project.getId().equals("")) {
            sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】增加了一条项目信息,项目编码：【"+project.getProjectCode()+"】,项目名称：【"+project.getProjectName()+"】");
            project.setApplyUser("admin");//申请人
            project.setApplyDate(time);
            project.setBiandongMethod("1");
            //账套增加项目信息
            return projectService.sys_save(project)
                    //增加系统日志表
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o -> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】修改了一条项目信息,项目编码：【"+project.getProjectCode()+"】,项目名称：【"+project.getProjectName()+"】");
        project.setBiandongMethod("2");
        //如果分配账套修改
        if (project.getApplyDatabaseUniqueCode()!=null && !project.getApplyDatabaseUniqueCode().equals("")) {
            //通知集团操作员
            SysNotice sysNotice = new SysNotice();
            sysNotice.setCorpUniqueCode("abc001");
            sysNotice.setUserUniqueCode("admin");
            sysNotice.setNoticeType("修改");
            sysNotice.setNoticeTime(time);
            sysNotice.setNoticeContent(sysLog.getOperationCont());
            sysNotice.setNoticeName("项目目录");
            sysNotice.setNoticeCode("project_"+project.getProjectCateCode());
            sysNotice.setReceiveRole("001");
            sysNotice.setReceiveUser("test");
            sysNotice.setNoticeState("0");
            //系统修改项目和新增回滚表信息
            return projectService.sys_edit(project,"1","admin","admin")
                    //账套修改项目和新增回滚表信息
                    .flatMap(item -> projectService.acc_edit(project,"1","admin","admin"))
                    //增加系统日志表
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(o -> R.ok().setResult(o));
        }
        //系统修改项目和新增回滚表信息
        return projectService.sys_edit(project,"1","admin","admin")
                //增加系统日志表
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("approve")
    @ApiOperation(value = "审批操作", notes = "传入code")
    public Mono approve(@RequestBody Project project) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        project.setApproveDate(time);
        project.setApproveUser("admin");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】审批了一条项目信息,项目编码：【"+project.getProjectCode()+"】,项目名称：【"+project.getProjectName()+"】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("admin");
        sysNotice.setNoticeType("审批");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont()+",审批意见："+project.getApproveIdea());
        sysNotice.setNoticeName("项目目录");
        sysNotice.setNoticeCode("project_"+project.getProjectCateCode());
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        //审批不通过
        if (project.getSuccessState().equals("0")){
            return sysLogService.save_log(sysLog)//增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(item -> R.ok().setResult(item));
        }
        //新增审批
        if (project.getBiandongMethod().equals("1")){
            return projectService.sys_save(project)
                    .flatMap(item -> projectService.acc_save(project))
                    //增加系统日志表
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(item -> R.ok().setResult(item));
        }
        //修改审批
        return projectService.sys_approval_edit(project,"1","admin","admin")
                .flatMap(item -> projectService.acc_edit(project,"1","admin","admin"))
                //增加系统日志表
                .flatMap(item ->  sysLogService.save_log(sysLog))
                //增加系统通知信息表
                .flatMap(item -> sysNoticeRepository.save(sysNotice))
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody Project project){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】删除了一条项目信息,项目编码：【"+project.getProjectCode()+"】,项目名称：【"+project.getProjectName()+"】");
        //添加回滚信息，删除项目信息
        return projectService.sys_delete(project)
                .defaultIfEmpty(project)
                .flatMap(item -> projectService.acc_delete(project))
                .defaultIfEmpty(project)
                //增加系统日志表
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByCode(String projectCateCode, String projectCode){
        return projectService.findByCode(projectCateCode, projectCode);
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String projectCateCode, String projectName){
        return projectService.findByName(projectCateCode, projectName);
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody JSONObject jsonObject){
        String excelValue= jsonObject.get("excelValue").toString();
        //导入全新项目信息
        if (excelValue=="1"){
            //是指项目库中不存在的项目，项目编码和名称不允许与当前库重复
            List<Project> object = JSON.parseArray(JSON.toJSONString(jsonObject.getJSONArray("object")),Project.class);
//        List<Project> object = new Gson().fromJson(new Gson().toJson(jsonObject.get("object")), new TypeToken<List<Project>>(){}.getType());
            String cateCode= jsonObject.get("cateCode").toString();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String year = new SimpleDateFormat("yyyy").format(new Date());
            System.out.println(object.size());
            //系统操作日志
            SysLog sysLog = new SysLog();
            sysLog.setOperationDate(time);
            sysLog.setUniqueCode("a001");
            sysLog.setUserName("test");
            sysLog.setAccId("bjxgkj-001");
            sysLog.setIyear(year);
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】导入了"+cateCode+"大类下的项目");
            return projectRepository.saveAll(object)
                    .collectList()
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        } else {
            //是导入系统默认字段之外的项目信息，导入的项目编码在项目库中必须存在，且导入字段名与当前项目大类栏目定义名称相同
            return null;
        }
    }

    @GetMapping("findByCateCode")
    @ApiOperation(value = "根据项目大类查询项目信息", notes = "传入code")
    public Mono<R> findByCateCode(String projectCateCode) {
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0")
                .collectList().map(o -> R.ok().setResult(o));
    }

}
