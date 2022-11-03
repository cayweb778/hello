package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.vo.stock.StockSFCNumberVo;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/stock/sfc")
public class StockSFCController {

    @Autowired
    DatabaseClient client;


    @PostMapping("numberOrMoney")
    public Mono<R> numberOrMoney(@RequestBody Map map){
        String endDate=map.get("strDate").toString();
        String ckId=map.get("ckId").toString();
        String dataType=map.get("dataType").toString();
        List<String> stockClassId= (List<String>) map.get("stockClassId");
        List<String> stockId= (List<String>) map.get("stockId");
        StringBuffer sb1=new StringBuffer();
        StringBuffer sb2=new StringBuffer();
        StringBuffer sb0=new StringBuffer();

        if(StrUtil.isNotBlank(endDate)){
            String strDate=endDate.substring(0,4)+"-01-31";
            sb1.append("and sws.ddate between '"+strDate+"' and '"+endDate+"'");
        }
        if(StrUtil.isNotBlank(dataType)){
            sb1.append("and sws.bcheck = '"+dataType+"'");
        }

        if(StrUtil.isNotBlank(ckId)){
            sb0.append(" and sws.stock_cangku_id='"+ckId+"' ");
            sb1.append(" and sws.cwhcode='"+ckId+"' ");
        }
        if(stockId.size()>0){
            String idStr="";
            for (int i = 0; i <stockId.size(); i++) {
                idStr+="'"+stockId.get(i)+"',";
            }
            idStr=idStr.substring(0,idStr.length()-1);
            sb2.append(" and sk.stock_num in ("+idStr+") ");
        }
        if(stockClassId.size()>0){
            String idStr="";
            for (int i = 0; i <stockClassId.size(); i++) {
                idStr+="'"+stockClassId.get(i)+"',";
            }
            idStr=idStr.substring(0,idStr.length()-1);
            sb2.append(" and sk.stock_class in ("+idStr+") ");
        }

        String sql="select distinct temp.*,sk.stock_num,sk.stock_name,sk.stock_ggxh,sk.stock_class,sk.stock_unit_name as unit_name,sk.stock_unit_name1 as unit_name1,sk.stock_unit_name2 as unit_name2,\n" +
                "(select coalesce(sum(cast(coalesce(sws.base_quantity, '0') as decimal)), '0') from stock_begin_balance sws where sws.stock_id = sk.stock_num and sws.bcheck='1' "+sb0+") qichu,\n" +
                " (select coalesce(sum(cast(coalesce(sws.sub_quantity1, '0') as decimal)), '0') from stock_begin_balance sws where sws.stock_id = sk.stock_num and sws.bcheck='1' "+sb0+") qichu1,\n" +
                " (select coalesce(sum(cast(coalesce(sws.sub_quantity2, '0') as decimal)), '0') from stock_begin_balance sws where sws.stock_id = sk.stock_num and sws.bcheck='1' "+sb0+") qichu2,\n" +
                " (select coalesce(sum(cast(coalesce(sws.icost, '0') as decimal)), '0') from stock_begin_balance sws where sws.stock_id = sk.stock_num and sws.bcheck='1') qcicost,\n" +
                " (select coalesce(sum(cast(coalesce(sws.base_quantity, '0') as decimal)), '0') from stock_warehousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('CGRKD', 'QTRKD', 'DBRKD', 'PDRKD', 'XTZHRKD')) rk,\n" +
                " (select coalesce(sum(cast(coalesce(sws.sub_quantity1, '0') as decimal)), '0') from stock_warehousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('CGRKD', 'QTRKD', 'DBRKD', 'PDRKD', 'XTZHRKD')) rk1,\n" +
                " (select coalesce(sum(cast(coalesce(sws.sub_quantity2, '0') as decimal)), '0') from stock_warehousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('CGRKD', 'QTRKD', 'DBRKD', 'PDRKD', 'XTZHRKD')) rk2,\n" +
                " (select coalesce(sum(cast(coalesce(sws.icost, '0') as decimal)), '0')+coalesce(sum(cast(coalesce(sws.thmoney, '0') as decimal)), '0') from stock_warehousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('CGRKD', 'QTRKD', 'DBRKD', 'PDRKD', 'XTZHRKD', 'RKTZD')) rkicost,\n" +
                " (select coalesce(sum(cast(coalesce(sws.base_quantity, '0') as decimal)), '0') from stock_saleousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('QTCKD', 'DBCKD', 'PDCKD', 'XTZHCKD', 'XSCKD','CLLYD'))  ck,\n" +
                " (select coalesce(sum(cast(coalesce(sws.sub_quantity1, '0') as decimal)), '0') from stock_saleousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('QTCKD', 'DBCKD', 'PDCKD', 'XTZHCKD', 'XSCKD','CLLYD'))  ck1,\n" +
                " (select coalesce(sum(cast(coalesce(sws.sub_quantity2, '0') as decimal)), '0') from stock_saleousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('QTCKD', 'DBCKD', 'PDCKD', 'XTZHCKD', 'XSCKD','CLLYD'))  ck2,\n" +
                " (select coalesce(sum(cast(coalesce(sws.icost, '0') as decimal)), '0')+coalesce(sum(cast(coalesce(sws.thmoney, '0') as decimal)), '0') from stock_saleousings sws where sws.cinvode = sk.stock_num "+sb1+" and sws.bill_style in ('QTCKD', 'DBCKD', 'PDCKD', 'XTZHCKD', 'XSCKD','CLLYD','CKTZD'))   ckicost\n" +
                "from (\n" +
                "  select xcl.stock_id as cinvode from stock_begin_balance xcl left join stock sk on sk.stock_num=xcl.stock_id group by xcl.stock_id,sk.stock_unit_id union all\n" +
                "  select xcl.cinvode from stock_warehousings xcl group by xcl.cinvode,xcl.cg_unit_id union all\n" +
                "  select xcl.cinvode from stock_saleousings xcl  group by xcl.cinvode,xcl.xs_unit_id\n" +
                ") as temp left join stock sk on sk.stock_num=temp.cinvode where temp.cinvode is not null "+sb2+" ";

        return client.sql(sql).fetch().all().collectList()
                .map(list->JSON.parseArray(JSON.toJSONString(list), StockSFCNumberVo.class))
                .map(R::ok);
    }
}
