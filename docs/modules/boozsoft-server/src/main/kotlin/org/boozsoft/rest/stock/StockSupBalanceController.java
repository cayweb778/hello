package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.stock.StockSupBalanceVo;
import org.boozsoft.repo.SupplierRepository;
import org.boozsoft.repo.stock.StockWarehousingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockSupBalance")
public class StockSupBalanceController {

    @Autowired
    StockWarehousingRepository stockWarehousingRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    DatabaseClient client;


    @PostMapping("findAllStockSupBalance")
    public Mono<R> findAllStockSupBalance(@RequestBody Map map){
        String strDate=map.get("strDate").toString().substring(0,4)+"-"+map.get("strDate").toString().substring(4,6)+"-01";
        // 上月日期
        String lastStrDate=map.get("strDate").toString().substring(0,4)+"-01-01";
        int strMonth=Integer.valueOf(map.get("strDate").toString().substring(4,6));

        String endDate=map.get("endDate").toString().substring(0,4)+"-"+map.get("endDate").toString().substring(4,6)+"-31";
        String lastEndMonth=Integer.valueOf(map.get("endDate").toString().substring(4,6))-1<10?"0"+(Integer.valueOf(map.get("endDate").toString().substring(4,6))-1): String.valueOf((Integer.valueOf(map.get("endDate").toString().substring(4,6))-1));
        // 上月日期
        String lastEndDate=map.get("strDate").toString().substring(0,4)+"-"+lastEndMonth+"-31";

        String supClass=map.get("supClass").toString();
        String jssup=map.get("jssup").toString();
        String dataType=map.get("dataType").toString();
        String dept=map.get("dept").toString();
        String psn=map.get("psn").toString();
        // 账套启用
        String startDate=map.get("startDate").toString();

        StringBuffer sqlBalance=new StringBuffer("");
        if(StrUtil.isNotBlank(dataType)){
            sqlBalance.append("and abb.bcheck='"+dataType+"'");
        }else if(StrUtil.isNotBlank(dept)){
            sqlBalance.append("and abb.cdepcode='"+dept+"'");
        }else if(StrUtil.isNotBlank(psn)){
            sqlBalance.append("and abb.cmaker_id='"+psn+"'");
        }
        StringBuffer sqlStockWare=new StringBuffer("");
        if(StrUtil.isNotBlank(dataType)){
            sqlStockWare.append("and sw.bcheck='"+dataType+"'");
        }else if(StrUtil.isNotBlank(dept)){
            sqlStockWare.append("and sw.cdepcode='"+dept+"'");
        }else if(StrUtil.isNotBlank(psn)){
            sqlStockWare.append("and sw.cpersoncode='"+psn+"'");
        }
        StringBuffer sqlAAy=new StringBuffer("");
        if(StrUtil.isNotBlank(dataType)){
            sqlAAy.append("and aay.bcheck='"+dataType+"'");
        }else if(StrUtil.isNotBlank(dept)){
            sqlAAy.append("and aay.cdepcode='"+dept+"'");
        }else if(StrUtil.isNotBlank(psn)){
            sqlAAy.append("and aay.cpersoncode='"+psn+"'");
        }

        return supplierRepository.findAllByUniqueCodeCcusNotNull().collectList()
        .flatMap(suplist->{
            if(StrUtil.isNotBlank(supClass)&&!supClass.equals("0")){
                suplist=suplist.stream().filter(ff->ff.getUniqueCustclass().equals(supClass)).collect(Collectors.toList());
            }else if(StrUtil.isNotBlank(jssup)){
                suplist=suplist.stream().filter(ff->ff.getUniqueCode().equals(jssup)).collect(Collectors.toList());
            }

            String sql="select temp.* from (select sup.unique_code,\n" +
                    "       sup.cust_code,\n" +
                    "       sup.cust_name,\n" +
                    "       sup.unique_code_ccus,\n" +
                    "       ((select coalesce(sum(cast(abb.isum_benbi as decimal)), '0')\n" +
                    "         from ar_begin_balance abb\n" +
                    "         where abb.cvencode_js = sup.id\n" +
                    "           and abb.bus_style = 'YFD' "+sqlBalance+" ) - (select coalesce(sum(cast(abb.isum_benbi as decimal)), '0')\n" +
                    "                                         from ar_begin_balance abb\n" +
                    "                                         where abb.cvencode_js = sup.id\n" +
                    "                                           and abb.bus_style = 'FKD' "+ sqlBalance +" )) as qichu,\n" +
                    "       (select coalesce(sum(cast(sw.isum as decimal)), '0')\n" +
                    "         from stock_warehousing sw\n" +
                    "         where sw.cvencode_js = sup.id\n" +
                    "           and sw.bill_style = 'CGDHD' and sw.ddate between '"+lastStrDate+"' and '"+lastEndDate+"' "+sqlStockWare+" ) as last_month_cgdhd,\n" +
                    "       (select coalesce(sum(cast(sw.isum as decimal)), '0')\n" +
                    "        from stock_warehousing sw\n" +
                    "        where sw.cvencode_js = sup.id\n" +
                    "          and sw.bill_style = 'YFD' and sw.ddate between '"+lastStrDate+"' and '"+lastEndDate+"' "+sqlStockWare+" ) as last_month_yingfu,\n" +
                    "       (select coalesce(sum(cast(aay.isum as decimal)), '0')\n" +
                    "        from ar_ap_ysyf aay\n" +
                    "        where aay.cvencode = sup.id\n" +
                    "          and aay.bill_style = 'SKD' and aay.ddate between '"+lastStrDate+"' and '"+lastEndDate+"' "+sqlAAy+" ) as last_month_fukuan,\n" +
                    "       (select coalesce(sum(cast(sw.isum as decimal)), '0')\n" +
                    "         from stock_warehousing sw\n" +
                    "         where sw.cvencode_js = sup.id\n" +
                    "           and sw.bill_style = 'CGDHD' and sw.ddate between '"+strDate+"' and '"+endDate+"' "+sqlStockWare+" ) as cgdhd,\n" +
                    "       (select coalesce(sum(cast(sw.isum as decimal)), '0')\n" +
                    "        from stock_warehousing sw\n" +
                    "        where sw.cvencode_js = sup.id\n" +
                    "          and sw.bill_style = 'YFD' and sw.ddate between '"+strDate+"' and '"+endDate+"' "+sqlStockWare+" ) as yingfu,\n" +
                    "       (select coalesce(sum(cast(aay.isum as decimal)), '0')\n" +
                    "        from ar_ap_ysyf aay\n" +
                    "        where aay.cvencode = sup.id\n" +
                    "          and aay.bill_style = 'SKD' and aay.ddate between '"+strDate+"' and '"+endDate+"' "+sqlAAy+" ) as fukuan\n" +
                    "from supplier sup\n" +
                    "where sup.unique_code_ccus is not null) as temp where (temp.qichu!=0 or temp.cgdhd!=0 or temp.yingfu!=0 or temp.fukuan!=0)";
            List<CustomerVo> finalSuplist = suplist;
            return client.sql(sql).fetch().all().collectList()
            .flatMap(sqllist->{
                        List<StockSupBalanceVo> newlist=new ArrayList<>();
                        List<StockSupBalanceVo> a= JSON.parseArray(JSON.toJSONString(sqllist),StockSupBalanceVo.class);

                        finalSuplist.forEach(ff->{
                            List<StockSupBalanceVo> collect = a.stream().filter(aa -> aa.getCustCode().equals(ff.getCustCode())).collect(Collectors.toList());
                            if(collect.size()>0){
                                // 计算逻辑：1月直接读期初。反之：期初+上月到货+上月应付-上月付款     2.应付=应付+到货   3.期末=期初+应付-付款
                                BigDecimal qichu=new BigDecimal(collect.get(0).getQichu());
                                BigDecimal daohuo=new BigDecimal(collect.get(0).getCgdhd());
                                BigDecimal yingfu=daohuo.add(new BigDecimal(collect.get(0).getYingfu()));
                                BigDecimal fukuan=new BigDecimal(collect.get(0).getFukuan());

                                BigDecimal lastMonthCgdhd=new BigDecimal(collect.get(0).getLastMonthCgdhd());
                                BigDecimal lastMonthYingfu=lastMonthCgdhd.add(new BigDecimal(collect.get(0).getLastMonthYingfu()));
                                BigDecimal lastMonthFukuan=new BigDecimal(collect.get(0).getLastMonthFukuan());

                                BigDecimal qimo=new BigDecimal(0);
                                // 从1月查起
                                if(strMonth==1||startDate.equals("1")){
                                    qimo=qichu.add(yingfu).subtract(fukuan);
                                }else{
                                    qichu=qichu.add(lastMonthYingfu).subtract(lastMonthFukuan);
                                    qimo=qichu.add(yingfu).subtract(fukuan);
                                }
                                collect.get(0).setQimo(String.valueOf(qimo));
                                collect.get(0).setQichu(qichu.toString());
                                collect.get(0).setYingfu(yingfu.toString());
                                collect.get(0).setFukuan(fukuan.toString());
                                newlist.add(collect.get(0));
                            }
                        });
                        return Mono.just(newlist);
                    });
        }).map(a->R.ok().setResult(a));
    }
}
