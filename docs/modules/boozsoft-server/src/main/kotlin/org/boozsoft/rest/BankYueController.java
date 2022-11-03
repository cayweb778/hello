package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.repo.AccvoucherRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/bankYue")
public class BankYueController {

    @Autowired
    AccvoucherRepository accvoucherRepository;


    @Autowired
    DatabaseClient databaseClient;

    /**
     * 查询银行科目余额表本期
     * @return
     */
    @PostMapping("findAccountByBankYue")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByBankYue(@RequestBody Map map){

//        databaseClient.sql("");
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        if (ishaveRjz==null || ishaveRjz.equals("0")){
            return accvoucherRepository.findAccountByBankYueByIbook(strDate, endDate)
                    .collectList()
                    .map(o->R.ok().setResult(o));
        }
        return accvoucherRepository.findAccountByBankYue(strDate, endDate)
                .collectList()
                .map(o->R.ok().setResult(o));
    }

    /**
     * 查询银行科目余额表期初
     * @return
     */
    @PostMapping("findAccountByBankYueQichu")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByBankYueQichu(@RequestBody Map map){
        String strDate=map.get("strDate").toString();   // 起始期间
        String year = strDate.substring(0, 4);
        String month = String.format("%02d", Integer.parseInt(strDate.substring(4))-1);
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        if (ishaveRjz==null || ishaveRjz.equals("0")){
            return accvoucherRepository.findAccountByBankYueByIbook(year+"00", year+month)
                    .collectList()
                    .map(o->R.ok().setResult(o));
        }
        return accvoucherRepository.findAccountByBankYue(year+"00", year+month)
                .collectList()
                .map(o->R.ok().setResult(o));
    }
    /**
     * 查询银行科目余额表累计
     * @return
     */
    @PostMapping("findAccountByBankYueLeiji")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByBankYueLeiji(@RequestBody Map map){
        String strDate=map.get("strDate").toString();   // 起始期间
        String year = strDate.substring(0, 4);
        String endDate=map.get("endDate").toString();   // 结束期间
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        if (ishaveRjz==null || ishaveRjz.equals("0")){
            return accvoucherRepository.findAccountByBankYueByIbook(year+"01", endDate)
                    .collectList()
                    .map(o->R.ok().setResult(o));
        }
        return accvoucherRepository.findAccountByBankYue(year+"01", endDate)
                .collectList()
                .map(o->R.ok().setResult(o));
    }

    /**
     * 查询现金科目余额表本期
     * @return
     */
    @PostMapping("findAccountByCashYue")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByCashYue(@RequestBody Map map){

//        databaseClient.sql("");
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        if (ishaveRjz==null || ishaveRjz.equals("0")){
            return accvoucherRepository.findAccountByCashYueByIbook(strDate, endDate)
                    .collectList()
                    .map(o->R.ok().setResult(o));
        }
        return accvoucherRepository.findAccountByCashYue(strDate, endDate)
                .collectList()
                .map(o->R.ok().setResult(o));
    }

    /**
     * 查询现金科目余额表期初
     * @return
     */
    @PostMapping("findAccountByCashYueQichu")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByCashYueQichu(@RequestBody Map map){
        String strDate=map.get("strDate").toString();   // 起始期间
        String year = strDate.substring(0, 4);
        String month = String.format("%02d", Integer.parseInt(strDate.substring(4))-1);
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        if (ishaveRjz==null || ishaveRjz.equals("0")){
            return accvoucherRepository.findAccountByCashYueByIbook(year+"00", year+month)
                    .collectList()
                    .map(o->R.ok().setResult(o));
        }
        return accvoucherRepository.findAccountByCashYue(year+"00", year+month)
                .collectList()
                .map(o->R.ok().setResult(o));
    }

    /**
     * 查询现金科目余额表累计
     * @return
     */
    @PostMapping("findAccountByCashYueLeiji")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByCashYueLeiji(@RequestBody Map map){
        String strDate=map.get("strDate").toString();   // 起始期间
        String year = strDate.substring(0, 4);
        String endDate=map.get("endDate").toString();   // 结束期间
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        if (ishaveRjz==null || ishaveRjz.equals("0")){
            return accvoucherRepository.findAccountByCashYueByIbook(year+"01", endDate)
                    .collectList()
                    .map(o->R.ok().setResult(o));
        }
        return accvoucherRepository.findAccountByCashYue(year+"01", endDate)
                .collectList()
                .map(o->R.ok().setResult(o));
    }

}
