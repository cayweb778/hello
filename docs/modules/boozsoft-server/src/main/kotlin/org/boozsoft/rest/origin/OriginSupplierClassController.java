package org.boozsoft.rest.origin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.origin.OrgCustomerClass;
import org.boozsoft.domain.entity.origin.OrgSupplierClass;
import org.boozsoft.domain.vo.AAtemp;
import org.boozsoft.domain.vo.GroupCustomerClassVo;
import org.boozsoft.repo.group.GroupCustomerClassAccountRepository;
import org.boozsoft.repo.group.GroupSupplierClassAccountRepository;
import org.boozsoft.repo.origin.OriginCustomerClassRepository;
import org.boozsoft.repo.origin.OriginSupplierClassRepository;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/originSupplierClass")
public class OriginSupplierClassController {

    @Autowired
    OriginSupplierClassRepository originSupplierClassRepository;
    @Autowired
    GroupSupplierClassAccountRepository groupSupplierClassAccountRepository;



    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map){
        String orgUnique=map.get("orgUnique").toString();
        String superid=map.get("superid").toString();
        String searchValue=map.get("searchValue").toString();
        return originSupplierClassRepository.findAllByOrgUnique(orgUnique).collectList()
                .flatMap(item->{
                    List<OrgSupplierClass> list=item;
                    if(StringUtils.isNotBlank(searchValue)){
                        list=item.stream().filter(v->v.getCusClass().contains(searchValue) || v.getCusCclassName().contains(searchValue)).collect(Collectors.toList());
                    }
                    if(StringUtils.isNotBlank(superid)){
                        list=item.stream().filter(v->v.getParentId().contains(superid) || v.getUniqueCustclass().contains(superid)).collect(Collectors.toList());
                    }
                    list.stream().forEach(v->{
                        List<OrgSupplierClass> collect = item.stream().filter(f -> v.getParentId().equals(f.getUniqueCustclass())).collect(Collectors.toList());
                        if(collect.size()>0){
                            v.setSuperClassName(collect.get(0).getCusCclassName());
                        }
                    });
                    return Mono.just(list);
                })
                .map(R::page);
    }

    @PostMapping("treeCustomerClass")
    public Mono<R> treeCustomerClass(@RequestBody Map map) {
        String orgUnique=map.get("orgUnique").toString();
        return originSupplierClassRepository.findAllByOrgUnique(orgUnique)
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }

    @PostMapping("saveAll")
    public Mono<R> saveAll(@RequestBody List<OrgSupplierClass> list) {
        List<OrgSupplierClass> collect = list.stream().distinct().collect(Collectors.toList());
        return originSupplierClassRepository.saveAll(collect).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delOriginCustomerClassById")
    public Mono<R> delOriginCustomerClassById(@RequestBody Map map){
        String orgUnique=map.get("orgUnique").toString();
        List<OrgSupplierClass> list= JSON.parseArray(map.get("list").toString(), OrgSupplierClass.class);
        list.sort(Comparator.comparing(OrgSupplierClass::getCusGradeCode).reversed());

        // 不可删除
        List<String> noBend0Codelist=new ArrayList<>();
        // 可删除
        List<OrgSupplierClass> Bend1Codelist=new ArrayList<>();
        return originSupplierClassRepository.findAll().collectList()
        .flatMap(orgClassList->{
            list.stream().distinct().forEach(a->{
                Bend1Codelist.add(a);
                orgClassList.removeIf(li->li.getUniqueCustclass().equals(a.getUniqueCustclass()));

                // 判断科目是否含有下级
                List<AAtemp> childDel = getChildDel(a.getUniqueCustclass(), orgClassList);
                if(childDel.size()>0){
                    noBend0Codelist.add(a.getCusCclassName());
                }
            });
            // 过滤出上级科目,过滤集合中是否还有下级,没有修改末级标识
            List<String> editflag = new ArrayList<>();
            List<String> collect = list.stream().filter(a -> !a.getParentId().equals("0")).map(OrgSupplierClass::getUniqueCustclass).distinct().collect(Collectors.toList());
            collect.forEach(f->{
                List<AAtemp> childDel = getChildDel(f, orgClassList);
                if(childDel.size()==0){
                    editflag.add(f);
                }
            });
            // 不能删除的去重
            List<String> listStr=noBend0Codelist.stream().distinct().collect(Collectors.toList());

            // 修改上级末级标志
            if(editflag.size()==0){ editflag.add("0"); }
            Mono editFlg=originSupplierClassRepository.updateBendByIds("1",editflag).then();

            // 删除
            // 去除包含不能删除的
            List<OrgSupplierClass> str=Bend1Codelist.stream().filter(a->listStr.indexOf(a.getCusCclassName())==-1).collect(Collectors.toList());
            Mono delData=originSupplierClassRepository.deleteAll(str).then();

            // 集团客户分类分配状态改为未分配
            Mono editGroupClassAccount=groupSupplierClassAccountRepository.editFlagByCtypeAndOrgUnique("0","1",orgUnique,str.stream().map(OrgSupplierClass::getUniqueCustclass).collect(Collectors.toList())).then();

            return listStr.size()>0?Mono.zip(delData,editGroupClassAccount).then(Mono.just(listStr)):Mono.zip(editFlg,delData,editGroupClassAccount).then(Mono.just(R.ok()));
        }).map(a->R.ok().setResult(a));
    }

    // 引入集团分配给组织
    @PostMapping("findByBringGroupClassAccount")
    public Mono<R> findByGroupClassAccount(@RequestBody Map map) {
        String orgUnique=map.get("orgUnique").toString();
        String bend=map.get("bend").toString();
        return groupSupplierClassAccountRepository.findAllByNoBringOrg(orgUnique,"1",bend).collectList()
            .flatMap(list->{
                list.stream().forEach(v->{
                    List<GroupCustomerClassVo> collect = list.stream().filter(f -> v.getParentId().equals(f.getUniqueCustclass())).collect(Collectors.toList());
                    if(collect.size()>0){
                        v.setSuperClassName(collect.get(0).getCusCclassName());
                    }
                });
                return Mono.just(list);
            })
            .map(a->R.ok().setResult(a));
    }
    // 引入分配给账套
    @PostMapping("findByBringOrgTenantId")
    public Mono<R> findByBringOrgTenantId(@RequestBody Map map) {
        String tenantId=map.get("tenantId").toString();
        String bend=map.get("bend").toString();
        return groupSupplierClassAccountRepository.findAllByNoBringTenantId(tenantId,"2",bend).collectList()
                .flatMap(list->{
                    list.stream().forEach(v->{
                        List<GroupCustomerClassVo> collect = list.stream().filter(f -> v.getParentId().equals(f.getUniqueCustclass())).collect(Collectors.toList());
                        if(collect.size()>0){
                            v.setSuperClassName(collect.get(0).getCusCclassName());
                        }
                    });
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }

    public List<Map<String, Object>> getChild(String pid, List<OrgSupplierClass> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (OrgSupplierClass ms : allList) {
            if (ms.getParentId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("id", ms.getId());
                map.put("uniqueCustclass", ms.getUniqueCustclass());
                map.put("cusClass", ms.getCusClass());
                map.put("cusClassGrade", ms.getCusClassGrade());
                map.put("cusCclassName", ms.getCusCclassName());
                map.put("cusBend", ms.getCusBend());
                map.put("flag", ms.getFlag());
                map.put("parentId", ms.getUniqueCustclass());
                map.put("parentId2", ms.getParentId());
                map.put("cusGradeCode", ms.getCusGradeCode());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(String.valueOf(map.get("parentId")), allList);
            map.put("children", tList);
        }
        return childList;
    }
    public List<AAtemp> getChildDel(String pid, List<OrgSupplierClass> allList) {
        List<AAtemp> childList = new ArrayList<>();//用于保存子节点的list;
        for (OrgSupplierClass ms : allList) {
            if (ms.getParentId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                AAtemp a=new AAtemp();
                a.setTitle(ms.getCusCclassName()).setValue(ms.getUniqueCustclass()).setKey(ms.getUniqueCustclass()).setBend(ms.getCusBend()).setChildren(new ArrayList<>()).setParentId(ms.getParentId());
                childList.add(a); //加入子节点
            }
        }
        for (AAtemp map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<AAtemp> tList = getChildDel(map.getKey(), allList);
            map.setChildren(tList);
        }
        return childList;
    }
}
