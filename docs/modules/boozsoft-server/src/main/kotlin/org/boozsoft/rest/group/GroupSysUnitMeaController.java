package org.boozsoft.rest.group;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMea;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMeaList;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMeaMany;
import org.boozsoft.repo.SysUnitOfMeaListRepository;
import org.boozsoft.repo.SysUnitOfMeaManyRepository;
import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.repo.group.GroupSysUnitOfMeaListRepository;
import org.boozsoft.repo.group.GroupSysUnitOfMeaManyRepository;
import org.boozsoft.repo.group.GroupSysUnitOfMeaRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 计量单位
 */
@RestController
@RequestMapping("/group/sys/unitmea")
public class GroupSysUnitMeaController {

    @Autowired
    GroupSysUnitOfMeaRepository sysUnitOfMeaRepository;

    @Autowired
    GroupSysUnitOfMeaManyRepository sysUnitOfMeaManyRepository;

    @Autowired 
    GroupSysUnitOfMeaListRepository sysUnitOfMeaTypeRepository;

    @GetMapping("findAllSysUnitOfMea")
    public Mono<R> findAllSysUnitOfMea(){
        return sysUnitOfMeaRepository.findAll().collectList()
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(@RequestBody Map maps){
        return sysUnitOfMeaRepository.findAllOrderById().collectList()
                .map(list->{
                    return list.stream().filter(v->{
                        if(maps.containsKey("unitName") && !v.getUnitName().contains(maps.get("unitName").toString())){
                            return false;
                        }
                        return true;
                    });
                })
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("findAllSysUnitOfMeaList")
    public Mono<R> findAllSysUnitOfMeaList(@RequestBody Map maps){
        String manyCode =  maps.get("manyCode").toString();
        return sysUnitOfMeaTypeRepository.findAllByManyCode(manyCode).collectList()
                .map(list->{
                    return list.stream().filter(v->{
                        if(maps.containsKey("unitName") && !v.getUnitName().contains(maps.get("unitName").toString())){
                            return false;
                        }
                        return true;
                    });
                })
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono save(@RequestBody GroupSysUnitOfMea object) {
        return sysUnitOfMeaRepository.findAll()
                .collectList()
                .flatMap(list->{
                    //验证单位名称不能重复
                    Optional<GroupSysUnitOfMea> first = list.stream().filter(v -> object.getUnitName().equals(v.getUnitName())).findFirst();
                    if(first.isPresent()){
                        return Mono.just("验证单位名称不能重复!");
                    }
                    object.setCreateDate(LocalDateTime.now().toString());
                    object.setFlag("1");
                    String num = "001";
                    if(list.size() > 1){
                        num = list.get(list.size()-1).getUnitCode();
                    }
                    object.setUnitCode(addOne(num));
                    return sysUnitOfMeaRepository.save(object);
                })
                .map(o -> R.ok().setResult(o))
                .defaultIfEmpty(R.error());
    }

    @PostMapping("saveList")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono saveList(@RequestBody Map maps) {
        List<String> list =  (List<String>)maps.get("ids");
        String typeCode =  maps.get("typeCode").toString();
        return  sysUnitOfMeaRepository.updateTypeCode(typeCode, list)
                .then(Mono.just(R.ok()));
    }

    public  static String addOne(String testStr){
        String[] strs = testStr.split( "[^0-9]" ); //根据不是数字的字符拆分字符串
        String numStr = strs[strs.length- 1 ]; //取出最后一组数字
        if (numStr !=  null  && numStr.length()> 0 ){ //如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int  n = numStr.length(); //取出字符串的长度
            int  num = Integer.parseInt(numStr)+ 1 ; //将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return  testStr.subSequence( 0 , testStr.length()-n)+added;
        } else {
            throw  new  NumberFormatException();
        }
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupSysUnitOfMea object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return sysUnitOfMeaRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysUnitOfMeaRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("delMany")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delMany(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysUnitOfMeaManyRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("delManyList")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delManyList(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysUnitOfMeaTypeRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    /**
     * 查询所有分类list
     * @return
     */
    @PostMapping("findAllSysUnitOfMeaMany")
    public Mono<R> findAllSysUnitOfMeaMany(@RequestBody Map maps){
        return sysUnitOfMeaManyRepository.findAll().collectList()
                .map(list->{
                    return list.stream().filter(v->{
                        if(maps.containsKey("unitName") && !v.getUnitName().contains(maps.get("unitName").toString())){
                            return false;
                        }
                        return true;
                    });
                })
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findAllByUnitName")
    @ApiOperation(value = "只获取计量单位名称", notes = "传入code")
    public Mono findAllByUnitName(){
        return sysUnitOfMeaRepository.findAllByUnitName().collectList().map(item->R.ok().setResult(item));
    }


    @GetMapping("delType/{id}")
    public Mono<R> delType(@PathVariable String id){
        return sysUnitOfMeaTypeRepository.deleteById(id)
                .thenReturn(R.ok());
    }

    @PostMapping("saveManyList")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveManyList(@RequestBody Map maps){

        String unitName = maps.get("unitName").toString();
        String conversionType = maps.get("conversionType").toString();

        String list = JSONObject.toJSONString(maps.get("list"));
        List<GroupSysUnitOfMeaList> list1 = JSONObject.parseArray(list, GroupSysUnitOfMeaList.class);

        return sysUnitOfMeaManyRepository.findAll()
                .collectList()
                .flatMap(lists->{
                    //验证单位名称不能重复
                    Optional<GroupSysUnitOfMeaMany> first = lists.stream().filter(v -> unitName.equals(v.getUnitName())).findFirst();
                    if(first.isPresent()){
                        return Mono.just("验证单位名称不能重复!");
                    }
                    GroupSysUnitOfMeaMany so =  new GroupSysUnitOfMeaMany();
                    so.setUnitName(unitName);
                    so.setFlag("1");
                    so.setCreateDate(LocalDate.now().toString());
                    String num = "001";
                    if(lists.size() > 1){
                        num = lists.get(lists.size()-1).getUnitCode();
                    }
                    so.setUnitCode(addOne(num));

                    return sysUnitOfMeaManyRepository.save(so)
                            .zipWith(
                                    sysUnitOfMeaTypeRepository.findMaxCode()
                                            .defaultIfEmpty("001")
                                            .map(obj->{
                                                AtomicReference<String> a = new AtomicReference<>(addOne(obj));
                                                list1.forEach(v->{
                                                    v.setManyCode(so.getUnitCode());
                                                    v.setUnitCode(a.get());
                                                    v.setConversionType(conversionType);
                                                    a.set(addOne(a.get()));
                                                });
                                                return list1;
                                            }).flatMap(list2->{
                                                return sysUnitOfMeaTypeRepository.saveAll(list2).collectList();
                                            })
                            );
                })
                .map(o -> R.ok().setResult(o))
                .defaultIfEmpty(R.error());

    }


}
