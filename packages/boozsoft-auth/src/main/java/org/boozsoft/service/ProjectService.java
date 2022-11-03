package org.boozsoft.service;

import org.boozsoft.domain.entity.share.project.base.Project;
import org.springbooz.core.tool.result.R;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ProjectService {

    /** 根据项目大类和项目分类查询项目信息 */
    Mono<R> findByClass(Pageable pageable, String projectCateCode, String projectClassCode);
    /** 根据项目大类和项目分类查询项目信息 */
    Mono<R> acc_findByClass(Pageable pageable, String projectCateCode, String projectClassCode);
    /** 保存项目信息 */
    Mono save(Project project);
    /** 系统保存项目信息 */
    Mono sys_save(Project project);
    /** 账套保存项目信息 */
    Mono acc_save(Project project);
    /** 集团删除项目信息 */
    Mono sys_delete(Project project);
    /** 账套删除项目信息 */
    Mono acc_delete(Project project);
    /** 系统修改项目信息 */
    Mono sys_edit(Project project, String biandongMethod, String biandongUniqueCode, String biandongName);
    /** 账套修改项目信息 */
    Mono acc_edit(Project project, String biandongMethod, String biandongUniqueCode, String biandongName);
    /** 保存项目回滚信息 */
    Mono save_rollback(Project project, String biandongMethod, String biandongUniqueCode, String biandongName);
    /** 审批修改内容 */
    Mono sys_approval_edit(Project project, String biandongMethod, String biandongUniqueCode, String biandongName);
    /** 根据项目编码查询项目 */
    Mono findByCode(String projectCateCode, String projectCode);
    /** 根据项目名称查询项目 */
    Mono findByName(String projectCateCode, String projectName);
    /** 根据项目大类编码查询该项目下所有项目编码或名称 **/
    Mono<Set<String>> findByProjectCodeAndValue(String projectCateCode, String nameOrNum);

}
