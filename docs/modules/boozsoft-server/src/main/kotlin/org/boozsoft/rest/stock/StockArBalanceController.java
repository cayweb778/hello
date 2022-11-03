package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.entity.stock.StockArBeginBalance;
import org.boozsoft.repo.TaskRepository;
import org.boozsoft.repo.stock.StockArBeginBalanceRepository;
import org.boozsoft.repo.stock.StockArCustRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应收款期初
 */
@Slf4j
@RestController
@RequestMapping("/stock_ar_balance")
public class StockArBalanceController {

    @Autowired
    StockArBeginBalanceRepository stockArBeginBalanceRepository;
    @Autowired
    TaskRepository taskRepository;



    @PostMapping("getStockArBalanceMaxIineId")
    public Mono<R> getStockArBalanceMaxIineId(String iyear){
        return stockArBeginBalanceRepository.getStockArBalanceMaxIineId(iyear).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockArBalanceSave")
    public Mono<R> stockArBalanceSave(@RequestBody StockArBeginBalance obj){
        return stockArBeginBalanceRepository.save(obj).map(a->R.ok().setResult(a));
    }
    @PostMapping("stockArBalanceEdit")
    public Mono<R> stockArBalanceEdit(@RequestBody StockArBeginBalance obj){
        return stockArBeginBalanceRepository.save(obj).map(a->R.ok().setResult(a));
    }
    @PostMapping("findByStockArBalance")
    public Mono<R> findByStockArBalance(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String custId=map.get("custId").toString();
        return stockArBeginBalanceRepository.findByStockArBalance(iyear).collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(custId)){
                        list=list.stream().filter(a->a.getCustId().equals(custId)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }
    // 审核\弃审
    @PostMapping("auditStockArBalance")
    public Mono<R> auditStockArBalance(@RequestBody Map map){
        String bcheck=map.get("bcheck").toString();
        String bcheckUser=map.get("bcheckUser").toString();
        String bcheckTime=map.get("bcheckTime").toString();
        List<String> id= (List<String>) map.get("id");
        return stockArBeginBalanceRepository.editAuditStockArBalance(bcheck,bcheckUser,bcheckTime,id).then(Mono.just(R.ok()));
    }
    @PostMapping("delStockArBalance")
    public Mono<R> delStockArBalance(@RequestBody Map map){
        List<String> id= (List<String>) map.get("id");
        return stockArBeginBalanceRepository.delStockArBalance(id).then(Mono.just(R.ok()));
    }
    @PostMapping("countByCustIdAndIyearAndBcheck")
    public Mono<R> findStockApBalanceByBcheck(@RequestBody Map map){
        String custId=map.get("custId").toString();
        String iyear=map.get("iyear").toString();
        return stockArBeginBalanceRepository.countByCustIdAndIyearAndBcheck(custId,iyear,"1").map(a->R.ok().setResult(a));
    }








    /************************************** 查询任务 ********************************************/
    @GetMapping("getByStockArBalanceTask")
    public Mono<R> getByStockArBalanceTask(String iyear){
        List<String> list=new ArrayList<>();
        list.add("编辑");
        return taskRepository.findAllByFunctionModule2("应收款期初余额",iyear,list).collectList().map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
    @PostMapping("stockArBalanceTaskSave")
    public Mono<R> stockArBalanceTaskSave(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String userName=map.get("userName").toString();
        String functionModule=map.get("functionModule").toString();
        String method=map.get("method").toString();
        Task obj=new Task();
        obj.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .setFunctionModule(functionModule)
                .setMethod(method)
                .setIyear(iyear)
                .setCaozuoUnique(userName)
                .setState("1");
        return taskRepository.save(obj).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockArBalanceTaskEditNewTime")
    public Mono<R> stockArBalanceTaskEditNewTime(String id){
        return taskRepository.editTimeByid(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),id).then(Mono.just(R.ok()));
    }
    @PostMapping("stockArBalanceTaskDelByUserName")
    public Mono<R> stockArBalanceTaskDelByUserName(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String userName=map.get("userName").toString();
        String functionModule=map.get("functionModule").toString();
        String method=map.get("method").toString();
        return taskRepository.deleteByFunctionModuleAndCaozuoUnique2(userName,functionModule,iyear,method).then(Mono.just(R.ok()));
    }
    /************************************** End ********************************************/
}
