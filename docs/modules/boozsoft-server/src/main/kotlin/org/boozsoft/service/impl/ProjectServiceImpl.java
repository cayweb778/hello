package org.boozsoft.service.impl;

import cn.hutool.core.util.IdUtil;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.entity.share.project.base.ProjectRollback;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.repo.project.base.ProjectRollbackRepositoryBase;
import org.boozsoft.service.ProjectService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.R2dbcRouter;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepositoryBase projectRepository;
    @Autowired
    ProjectRollbackRepositoryBase projectRollbackRepository;

    @Autowired
    R2dbcRouterLoader router;
    @Override
    public Mono findByClass(Pageable pageable, String projectCateCode, String projectClassCode) {
        /*Mono bind = router.bind(() -> {
                    if (projectClassCode!=null && !projectClassCode.equals("")){
                        return projectRepository.findByProjectClassCodeAndProjectCateCodeOrderById(pageable, projectClassCode,projectCateCode).collectList().map(R::page);
                    }
                    return projectRepository.findByProjectCateCodeOrderById(pageable,projectCateCode).collectList().map(R::page);
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("bjxgkj-001").build()
        );*/
//        Flux aa =getRepository(projectCateCode).findByProjectClassCodeOrderById(pageable, projectClassCode);
//        return null;
        if (projectClassCode!=null && !projectClassCode.equals("")){
            return projectRepository.findByProjectClassCodeAndProjectCateCodeAndIsDelOrderByProjectCode(projectClassCode,projectCateCode,"0",pageable)
                    .collectList()
                    .flatMap(item-> projectRepository.countByProjectClassCodeAndProjectCateCodeAndIsDel(projectClassCode,projectCateCode,"0")
                            .map(total->R.page(item,pageable,total)));
        }
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0",pageable).collectList()
                .flatMap(item-> projectRepository.countByProjectCateCodeAndIsDel(projectCateCode,"0")
                        .map(total->R.page(item,pageable,total)));
    }

    @Override
    
    public Mono acc_findByClass(Pageable pageable, String projectCateCode, String projectClassCode) {
//        Flux aa =getRepository(projectCateCode).findByProjectClassCodeOrderById(pageable, projectClassCode);
//        return null;
        if (projectClassCode!=null && !projectClassCode.equals("")){
            return projectRepository.findByProjectClassCodeAndProjectCateCodeAndIsDelOrderByProjectCode(projectClassCode,projectCateCode,"0",pageable)
                    .collectList()
                    .flatMap(item-> projectRepository.countByProjectClassCodeAndProjectCateCodeAndIsDel(projectClassCode,projectCateCode,"0")
                            .map(total->R.page(item,pageable,total)));
        }
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0",pageable).collectList()
                .flatMap(item-> projectRepository.countByProjectCateCodeAndIsDel(projectCateCode,"0")
                        .map(total->R.page(item,pageable,total)));
    }

    @Override
    public Mono save(Project project) {
//        Project01 project01= (Project01) project;
//        ProjectRepositoryBase<Project> c= (ProjectRepositoryBase<Project>) getRepository(project.getProjectCateCode());
//        c.save(project01);
        if (project.getUniqueCode()==null || project.getUniqueCode().equals("")){
            project.setUniqueCode(IdUtil.objectId());
        }
        if (project.getStartDate()!=null && !project.getStartDate().equals("")){
            project.setStartDate(project.getStartDate().substring(0,10));
        }
        if (project.getEndDate()!=null && !project.getEndDate().equals("")){
            project.setEndDate(project.getEndDate().substring(0,10));
        }
        return projectRepository.save(project).map(item -> R.ok().setResult(item));
    }

    @Override
    
    public Mono sys_save(Project project) {
        Mono bind = router.bind(() -> {
                    return save(project);
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
//        return save(project);
        return bind;
    }

    @Override
    
    public Mono acc_save(Project project) {
        return save(project);
    }

    @Override
    
    public Mono sys_delete(Project project) {
        Mono bind = router.bind(() -> {
                    return save_rollback(project, "2","abc001", "user001")
                            .defaultIfEmpty(project)
                            .flatMap(item -> projectRepository.deleteById(project.getId()))
                            .defaultIfEmpty(project)
                            .map(item -> R.ok().setResult(item));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
//        return save_rollback(project, "2","abc001", "user001")
//                .defaultIfEmpty(project)
//                .flatMap(item -> projectRepository.deleteById(project.getId()))
//                .defaultIfEmpty(project)
//                .map(item -> R.ok().setResult(item));
        return bind;
    }

    @Override
    
    public Mono acc_delete(Project project) {
        return save_rollback(project, "2","abc001", "user001")
                .defaultIfEmpty(project)
                .flatMap(item -> projectRepository.deleteById(project.getId()))
                .defaultIfEmpty(project)
                .map(item -> R.ok().setResult(item));
    }

    @Override
    
    public Mono sys_edit(Project project, String biandongMethod, String biandongUniqueCode, String biandongName) {
        Mono bind = router.bind(() -> {
                    //保存回滚表信息
                    return save_rollback(project, biandongMethod, biandongUniqueCode, biandongName)
                            .defaultIfEmpty(project)
                            //修改项目信息
                            .flatMap(item -> projectRepository.save(project))
                            .map(item -> R.ok().setResult(item));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        //保存回滚表信息
//        return save_rollback(project, biandongMethod, biandongUniqueCode, biandongName)
//                .defaultIfEmpty(project)
//                //修改项目信息
//                .flatMap(item -> projectRepository.save(project))
//                .map(item -> R.ok().setResult(item));
        return bind;
    }

    @Override
    
    public Mono acc_edit(Project project, String biandongMethod, String biandongUniqueCode, String biandongName) {
        //修改项目信息
        Mono mono =  projectRepository.findByProjectCodeAndSuccessStateAndProjectCateCodeOrderByProjectCode(project.getProjectCode(),"1",project.getProjectCateCode())
                .map(item -> {
                    project.setId(item.getId());
                    return project;
                }).flatMap(projectRepository::save);
        //保存回滚表信息
        return save_rollback(project, biandongMethod, biandongUniqueCode, biandongName)
                .defaultIfEmpty(project)
                //修改项目信息
                .flatMap(item -> mono)
                .map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono save_rollback(Project project, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return projectRepository.findByProjectCodeAndSuccessStateAndProjectCateCodeOrderByProjectCode(project.getProjectCode(),"1",project.getProjectCateCode())
                .map(item -> {
                    ProjectRollback rollback = new ProjectRollback();
                    BeanUtils.copyProperties(project, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(project.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod(biandongMethod);
                    rollback.setBiandongUniqueCode(biandongUniqueCode);
                    rollback.setBiandongName(biandongName);
                    return rollback;
                }).flatMap(projectRollbackRepository::save)
                .map(item -> R.ok().setResult(item));
    }

    @Override
    
    public Mono sys_approval_edit(Project project, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //修改项目信息和回滚表添加记录
        Mono mono1 = sys_edit(project, biandongMethod, biandongUniqueCode, biandongName);
        //根据id删除项目信息记录
        Mono mono2 = projectRepository.findByProjectCodeAndSuccessStateAndProjectCateCodeOrderByProjectCode(project.getProjectCode(),"1",project.getProjectCateCode())
                .map(item -> item.getId())
                .flatMap(projectRepository :: deleteById);
        Mono bind = router.bind(() -> {
                    return mono1
                            .defaultIfEmpty(project)
                            .flatMap(item -> mono2)
                            .defaultIfEmpty(project)
                            .map(item -> R.ok().setResult(item));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
//        return mono1
//                .defaultIfEmpty(project)
//                .flatMap(item -> mono2)
//                .defaultIfEmpty(project)
//                .map(item -> R.ok().setResult(item));
        return bind;
    }

    @Override
    public Mono findByCode(String projectCateCode, String projectCode) {
        return projectRepository.findByProjectCodeAndProjectCateCodeOrderByProjectCode(projectCode,projectCateCode).map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono findByName(String projectCateCode, String projectName) {
        return projectRepository.findByProjectCodeAndProjectCateCodeOrderByProjectCode(projectName,projectCateCode).map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono<Set<String>> findByProjectCodeAndValue(String projectCateCode, String nameAndNumber) {
      return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0").collectList().map(list ->{
            HashSet<String> strings = new HashSet<>();
            for (Project project : list) {
                if (nameAndNumber.equals("1")){
                    strings.add(project.getProjectName()+"--"+project.getUniqueCode());
                }else {
                    strings.add(project.getProjectCode()+"--"+project.getUniqueCode());
                }
            }
            return strings;
      });
    }
}
