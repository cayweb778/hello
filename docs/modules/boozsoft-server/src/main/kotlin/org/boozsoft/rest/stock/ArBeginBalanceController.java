package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.ArBeginBalance;
import org.boozsoft.domain.entity.stock.ArXySource;
import org.boozsoft.repo.group.GroupStockAccountRepository;
import org.boozsoft.repo.group.GroupStockPeriodRepository;
import org.boozsoft.repo.stock.ArBeginBalanceRepository;
import org.boozsoft.repo.stock.ArXySourceRepository;
import org.boozsoft.repo.stock.StockSaleousingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/arBeginBalance")
public class ArBeginBalanceController {

    @Autowired
    private ArBeginBalanceRepository arBeginBalanceRepository;
    @Autowired
    private GroupStockPeriodRepository groupStockPeriodRepository;
    @Autowired
    private ArXySourceRepository arXySourceRepository;

    @GetMapping("findAll")
    public Mono<R> findAll(){
        return arBeginBalanceRepository.findAllByOrderByCcode()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByBillStyleAndIyearOrderByCcode")
    public Mono<R> findByBillStyleAndIyearOrderByCcode(String billStyle,String iyear){
        return arBeginBalanceRepository.findAllByBillStyleAndIyearOrderByCcode(billStyle, iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    public Mono<R> save(@RequestBody ArBeginBalance object){
        return arBeginBalanceRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteById")
    public Mono<R> deleteById(String id){
        return arBeginBalanceRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui) {
        return arBeginBalanceRepository.findBukongCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui) {
        return arBeginBalanceRepository.findMaxCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCvencode")
    public Mono<R> findByCvencode(String cvencode,String iyear){
        return arBeginBalanceRepository.findByCvencode(cvencode,iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findWhxList")
    public Mono<R> findWhxList(String billStyle,String iyear){
        return arBeginBalanceRepository.findWhxList(billStyle, iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findYearByUniqueCode")
    public Mono<R> findYearByUniqueCode(String id){
        return groupStockPeriodRepository.findYearByUniqueCode(id)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArXySource")
    public Mono<R> saveArXySource(@RequestBody ArXySource object){
        return arXySourceRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCcode")
    public Mono<R> findByCcode(String ccode){
        return arBeginBalanceRepository.findByCcode(ccode)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findArXySourceByCcodeAndXyccode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findArXySourceByCcodeAndXyccode(String ccode,String xyccode){
        return arXySourceRepository.findByCcodeAndXyccode(ccode, xyccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteArXySouceById")
    public Mono<R> deleteArXySouceById(String id){
        return arXySourceRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findYszzYueList")
    public Mono<R> findYszzYueList(String iyear,String bcheck){
        if (bcheck.equals("1")){
            return arBeginBalanceRepository.findYshYszzYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arBeginBalanceRepository.findWshYszzYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arBeginBalanceRepository.findYszzYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findYszzYueByCvencodeList")
    public Mono<R> findYszzYueByCvencodeList(String bcheck,String iyear,String cvencode){
        if (bcheck.equals("1")){
            return arBeginBalanceRepository.findYshYszzYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arBeginBalanceRepository.findWshYszzYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arBeginBalanceRepository.findYszzYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findSkdYueList")
    public Mono<R> findSkdYueList(String bcheck,String iyear){
        if (bcheck.equals("1")){
            return arBeginBalanceRepository.findYshSkdYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arBeginBalanceRepository.findWshSkdYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arBeginBalanceRepository.findSkdYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findFkdYueList")
    public Mono<R> findFkdYueList(String bcheck,String iyear){
        if (bcheck.equals("1")){
            return arBeginBalanceRepository.findYshFkdYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arBeginBalanceRepository.findWshFkdYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arBeginBalanceRepository.findFkdYueList(iyear)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findSkdYueByCvencodeList")
    public Mono<R> findSkdYueByCvencodeList(String bcheck,String iyear,String cvencode){
        if (bcheck.equals("1")){
            return arBeginBalanceRepository.findYshSkdYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arBeginBalanceRepository.findWshSkdYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arBeginBalanceRepository.findSkdYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findFkdYueByCvencodeList")
    public Mono<R> findFkdYueByCvencodeList(String bcheck,String iyear,String cvencode){
        if (bcheck.equals("1")){
            return arBeginBalanceRepository.findYshFkdYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arBeginBalanceRepository.findWshFkdYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arBeginBalanceRepository.findFkdYueByCvencodeList(iyear,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

}
