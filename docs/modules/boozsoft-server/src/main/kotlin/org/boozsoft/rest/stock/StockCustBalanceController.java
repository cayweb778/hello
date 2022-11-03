package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.stock.ArApYsyf;
import org.boozsoft.domain.entity.stock.ArBeginBalance;
import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.repo.CustomerRepository;
import org.boozsoft.repo.stock.ArApYsyfRepository;
import org.boozsoft.repo.stock.ArBeginBalanceRepository;
import org.boozsoft.repo.stock.StockSaleousingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/custBalance")
public class StockCustBalanceController {

    @Autowired
    private StockSaleousingRepository saleousingRepository;

    @Autowired
    private ArApYsyfRepository arApYsyfRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ArBeginBalanceRepository beginBalanceRepository;

    @PostMapping("findCustYsYue")
    public Mono<R> findCustYsYue(@RequestBody Map map) {
        Map<String, String> query = (Map<String, String>) map.get("query");
        String expirationDate = query.get("expirationDate");
        String year =  expirationDate.split("-")[0];
        Mono<List<StockSaleousing>> one = saleousingRepository.findAllByIyearAndDdateLessThanAndBillStyleInOrderByDdateAscCcodeAsc(year, expirationDate,Arrays.asList(new String[]{"XHD","YSD"})).collectList();
        Mono<List<Customer>> two = customerRepository.findAllByFlag("1").collectList();
        Mono<List<ArBeginBalance>> three = beginBalanceRepository.findAllByIyearAndDdateLessThanAndBillStyleOrderByCcode(year,expirationDate,"ar").collectList();
        Mono<List<ArApYsyf>> four = arApYsyfRepository.findAllByIyearAndDdateLessThanOrderByCcode(year, expirationDate).collectList();
        return Mono.zip(one,two,three,four).map(o -> R.ok().setResult(queryFilter(o, query)));
    }

    private Object queryFilter(Tuple4<List<StockSaleousing>, List<Customer>, List<ArBeginBalance>, List<ArApYsyf>> o, Map<String, String> query) {
        String cvencode = !query.containsKey("cvencode") ? "" : query.get("cvencode").trim();
        String cvencodeClass = !query.containsKey("cvencodeClass") ? "" : query.get("cvencodeClass").trim();
        String cvencodeJs = !query.containsKey("cvencodeJs") ? "" : query.get("cvencodeJs").trim();
        String cpersoncode = !query.containsKey("cpersoncode") ? "" : query.get("cpersoncode").trim();
        String cdepcode = !query.containsKey("cdepcode") ? "" : query.get("cdepcode").trim();
        String bcheck = !query.containsKey("bcheck") ? "" : query.get("bcheck").trim();
        List<StockSaleousing> filterList = o.getT1().stream().filter(entiry -> {
            if (StrUtil.isNotBlank(cvencodeClass) && !(o.getT2().stream().filter(it -> it.getId().equals(entiry.getCvencode())).collect(Collectors.toList()).get(0).getUniqueCustclass()).equals(cvencodeClass))
                return false;
            if (StrUtil.isNotBlank(cvencode) && !entiry.getCvencode().equals(cvencode)) return false;
            if (StrUtil.isNotBlank(bcheck) && !entiry.getBcheck().equals(bcheck)) return false;
            if (StrUtil.isNotBlank(cvencodeJs) && !entiry.getCvencodeJs().equals(cvencodeJs)) return false;
            if (StrUtil.isNotBlank(cpersoncode) && entiry.getCvencodeJs().equals(cpersoncode)) return false;
            if (StrUtil.isNotBlank(cdepcode) && (null == entiry.getCdepcode() || !entiry.getCdepcode().equals(cdepcode)))
                return false;
            return true;
        }).collect(Collectors.toList());
        List<String> custs = filterList.stream().map(it -> it.getCvencodeJs()).distinct().collect(Collectors.toList());
        List<ArApYsyf> skList = o.getT4().stream().filter(entiry -> {
            if (custs.stream().filter(s->s.equals(entiry.getCvencode())).collect(Collectors.toList()).size() == 0) return false;
            if (StrUtil.isNotBlank(bcheck) && !entiry.getBcheck().equals(bcheck)) return false;
            return true;
        }).collect(Collectors.toList());
        List<Object> list = new ArrayList<>();
        for (String s : custs) {
            Map<String, String> map = new HashMap<>();
            Customer customerJs = o.getT2().stream().filter(it -> it.getId().equals(s)).collect(Collectors.toList()).get(0);
            map.put("cvencodeJs",customerJs.getCustName());
            map.put("cvencodeJsCode",customerJs.getCustCode());
            List<ArBeginBalance> ysd = o.getT3().stream().filter(it->it.getBusStyle().equals("YSD") && !it.getArStyle().equals("XSFP") && it.getCvencodeJs().equals(s)).collect(Collectors.toList());
            List<ArBeginBalance> skd = o.getT3().stream().filter(it->it.getBusStyle().equals("SKD") && !it.getArStyle().equals("XSFP") && it.getCvencodeJs().equals(s)).collect(Collectors.toList());
            double qcys = ysd.size() == 0 ? 0 : ysd.stream().mapToDouble(v -> {
                if (Objects.isNull(v.getIsumBenbi())) return 0.00d;
                return Double.parseDouble(v.getIsumBenbi());
            }).sum();
            double qssk = skd.size() == 0 ? 0 : skd.stream().mapToDouble(v -> {
                if (Objects.isNull(v.getIsumBenbi())) return 0.00d;
                return Double.parseDouble(v.getIsumBenbi());
            }).sum();
            map.put("initialBalance",(qcys+qssk)+""); // （应收起初+收款起初）
             double ys = filterList.stream().filter(it -> it.getCvencodeJs().equals(s)).mapToDouble(v -> {
                if (Objects.isNull(v.getIsum())) return 0.00d;
                return Double.parseDouble(v.getIsum());
            }).sum();
            map.put("amountReceivable",""+ys); // 销货单价税合计+应收单价税合计
             double yis = skList.stream().filter(it -> it.getCvencode().equals(s)).mapToDouble(v -> {
                if (Objects.isNull(v.getIsum())) return 0.00d;
                return Double.parseDouble(v.getIsum());
            }).sum();
            map.put("amountReceived",""+yis);  // 当年收款单税合计
             double v = ((qcys + qssk) + ys) - yis;
            map.put("closingAmount",""+(Double.isInfinite(v)?0:v)); // (起初+应收)-已收
            double dPos =(yis/((qcys+qssk)+ys));
            map.put("receiptRatio",""+(Double.isInfinite(dPos) || dPos<=0?0:dPos));  // 已收/(起初+应收)
            list.add(map);
        }
        return list;
    }

}
