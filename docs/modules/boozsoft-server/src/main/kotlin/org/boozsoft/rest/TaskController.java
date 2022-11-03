package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.vo.TaskVo;
import org.boozsoft.repo.TaskRepository;
import org.boozsoft.repo.stock.StockCurrentstockRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhh
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    // 计算两个时间分钟差值
    public static Long getDatePoor(Date endDate, Date nowDate) {
        long xiaoshi=0L;
       try{
           SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//           Date start = sdf.parse("2015-10-22 05:12:10");
//           Date end = sdf.parse("2013-10-23 08:10:10");
           xiaoshi= (endDate.getTime() - nowDate.getTime())/(1000*60);
//           return "已累计在线：" + xiaoshi + "分钟";
       } catch (Exception e) {
           e.printStackTrace();
       }
       return xiaoshi;
    }
    // 时间格式转换：Str -->  Date
    public static Date strToDateLong(String strDate) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(strDate, pos);
            return strtodate;
         }
    public static void main(String[] args) {
        Date date1 = strToDateLong(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        Date date2 = strToDateLong("2021-10-21 11:25:00");
        System.out.println(getDatePoor(date1,date2));
    }

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    StockCurrentstockRepository stockCurrentstockRepository;


    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(@RequestBody Map map){
        String overtime=map.get("overtime").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchInfo"));

        Long overtime2=Long.valueOf(overtime)==0?1000:Long.valueOf(overtime);
        return taskRepository.findAllBy()
                .collectList()
                .flatMap(list->{
                    List<TaskVo> list2=list;
                    List<String> idlist=new ArrayList<>();

                    if(StrUtil.isNotBlank(searchMap.get("searchValue"))){
                        if(searchMap.get("searchQuery").equals("companyCode")){
                            list2= list.stream().filter(a->a.getCompanyCode().contains(searchMap.get("searchValue"))).collect(Collectors.toList());
                        }else if(searchMap.get("searchQuery").equals("companyName")){
                            list2= list.stream().filter(a->a.getCompanyName().contains(searchMap.get("searchValue"))).collect(Collectors.toList());
                        }else if(searchMap.get("searchQuery").equals("caozuoName")){
                            list2= list.stream().filter(a->a.getCaozuoName().contains(searchMap.get("searchValue"))).collect(Collectors.toList());
                        }else if(searchMap.get("searchQuery").equals("functionModule")){
                            list2= list.stream().filter(a->a.getFunctionModule().contains(searchMap.get("searchValue"))).collect(Collectors.toList());
                        }else if(searchMap.get("searchQuery").equals("method")){
                            list2= list.stream().filter(a->a.getMethod().equals(searchMap.get("searchValue"))).collect(Collectors.toList());
                        }else if(searchMap.get("searchQuery").equals("iyear")){
                            list2= list.stream().filter(a->a.getIyear().equals(searchMap.get("searchValue"))).collect(Collectors.toList());
                        }
                    }
                    list2.forEach(v->{
                        // 系统时间 减去 表中操作时间  差值 >= 设置的超时时间  就是 异常状态
                        if(getDatePoor(strToDateLong(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())),strToDateLong(v.getTime()))>=overtime2){
                            v.setState("0");
                            idlist.add(v.getId());
                        }
                    });
                    return Mono.just(list2);
                })
                .map(R::page);
    }

    @DeleteMapping("deleteByState")
    @ApiOperation(value = "清除异常", notes = "传入code")
    public Mono deleteByState(@RequestBody List<Task> list){
        return taskRepository.deleteAll(list)
                .then(Mono.just(R.ok()));
    }
    @DeleteMapping("deleteList")
    @ApiOperation(value = "清除任务", notes = "传入code")

    public Mono<R> deleteList(@RequestBody List<Task> list){
        return taskRepository.deleteAll(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("save")
    public Mono<R> save(@RequestBody Task t){
        t.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        t.setState("1");
        return taskRepository.save(t).map(o->R.ok().setResult(o));
    }

    /**
     * 查找模块任务
     * @param
     * @return
     */
    @PostMapping("countByFunctionModule")
    public Mono<R> countByFunctionModule(String name){
        return taskRepository.countByFunctionModule(name).map(o->R.ok().setResult(o));
    }
    @PostMapping("findByFunctionModule")
    public Mono<R> findByFunctionModule(String name){
        return taskRepository.findAllByFunctionModule(name).map(o->R.ok().setResult(o));
    }

    /**
     * 根据功能名称和操作员删除任务
     * @param caozuoUnique
     * @param name
     * @return
     */
    @PostMapping("deleteByFunctionModuleAndCaozuoUnique")
    public Mono<R> deleteByFunctionModuleAndCaozuoUnique(String caozuoUnique,String name){
        return taskRepository.deleteByFunctionModuleAndCaozuoUnique(caozuoUnique,name).then(Mono.just(R.ok()));
    }

    /**
     * 获取 操作锁定超时时间
     * @return
     */
    @PostMapping("GroupAccuracyOverTime")
    public Mono<R> GroupAccuracyOverTime(){
        return taskRepository.groupAccuracyOverTime().map(o->R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }
    @PostMapping("saveGroupAccuracyOverTime")
    public Mono<R> saveGroupAccuracyOverTime(String decimalPlaces){
        return taskRepository.saveGroupAccuracyOverTime(decimalPlaces,String.valueOf(System.currentTimeMillis())).map(o->R.ok().setResult(o));
    }
    @PostMapping("editGroupAccuracyOverTime")
    public Mono<R> editGroupAccuracyOverTime(String id,String decimalPlaces){
        return taskRepository.editGroupAccuracyOverTime(id,decimalPlaces).then(Mono.just(R.ok()));
    }
    @PostMapping("delAcclotList")
    public Mono<R> delAcclotList(String idlist){
        List<String> list=new ArrayList<>(Arrays.asList(idlist.split(",")));
        return taskRepository.delAcclotList(list).then(Mono.just(R.ok()));
    }
    @PostMapping("editAcclotStateList")
    public Mono<R> editAcclotStateList(String idlist){
        List<String> list=new ArrayList<>(Arrays.asList(idlist.split(",")));
        return taskRepository.editAcclotStateList(list).then(Mono.just(R.ok()));
    }

    // 获取现存量被锁数据
    @GetMapping("getStockCurrLock")
    public Mono<R> getStockCurrLock(){
        return stockCurrentstockRepository.findByRevison(1L).collectList().map(a->R.ok().setResult(a));
    }
    @PostMapping("delStockCurrLock")
    public Mono<R> delStockCurrLock(String id){
        return stockCurrentstockRepository.updateStockCurrentRevision(id,0L,"","","").then(Mono.just(R.ok()));
    }

    @GetMapping("findByIyearAndCaozuoModule")
    public Mono<R> findByIyearAndCaozuoModule(String iyear,String caozuoModule){
        return taskRepository.findByIyearAndCaozuoModule(iyear,caozuoModule)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
