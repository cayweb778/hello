package org.boozsoft.service.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.boozsoft.domain.vo.stock.StockCurrentLackVo;
import org.boozsoft.util.BigDecimalUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class StockXCLService {
    @Autowired
    DatabaseClient client;


    /**
     * 现存量and可用量
     * @param endDate                       单据日期
     * @param queryType                     获取类型。xcl\keyong ( 可为空, 需要获取 不足数量的 必传 )
     * @param rkBcheck                      入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
     * @param ckBcheck                      出库保存状态就是现存量 标志 1是查询所有 0 查询已审核
     * @param bdocumStyle                   1红字  0蓝字
     * @param iyear
     * @param stockWarehousingsList
     * @return
     */
    public Mono<R> verifyStockXCLList(String endDate,String queryType,String rkBcheck,String ckBcheck,String bdocumStyle,String iyear,List<StockCurrentLackVo> stockWarehousingsList){

        String bcheck1="";
        String bcheck2="";
        // 入库：0 查已审核的
        if(StrUtil.isBlank(rkBcheck)||rkBcheck.equals("0")){ bcheck1=" and s.bcheck='1' "; }
        // 出库  0 查已审核的
        if(StrUtil.isBlank(ckBcheck)||ckBcheck.equals("0")){ bcheck2=" and s.bcheck='1' "; }

        String qcsql="select coalesce(cast(s.base_quantity as float), '0') base_quantity,s.iyear,s.stock_id as cinvode,s.batch_number as batch_id," +
                "s.dpdate,s.dvdate,s.stock_cangku_id as cwhcode from stock_begin_balance s where s.bcheck = '1' and s.iyear='"+iyear+"' ";

        String finalBcheck = bcheck1;
        String finalBcheck1 = bcheck2;
        return client.sql(qcsql).fetch().all().collectList()
        .flatMap(qclist->{
            List<StockCurrentLackVo> newqclist= JSON.parseArray(JSON.toJSONString(qclist), StockCurrentLackVo.class);

            String sqla=StrUtil.isNotBlank(endDate)?" and s.ddate between '"+endDate.substring(0,4)+"-01-01' and '"+endDate+"' ":"";
            // 采购入库、其他入库
            String rksql="select coalesce(cast(s.base_quantity as float),'0') base_quantity,s.iyear,s.cwhcode,s.batch_id,s.dpdate,s.dvdate,s.cinvode from stock_warehousings s where " +
                    " s.bill_style in ('CGRKD','QTRKD') and s.iyear='"+iyear+"' "+ finalBcheck +" "+sqla+" ";
            return client.sql(rksql).fetch().all().collectList()
            .flatMap(rklist->{
                List<StockCurrentLackVo> newrklist=JSON.parseArray(JSON.toJSONString(rklist), StockCurrentLackVo.class);

                // 销售出库、其他出库
                String cksql="select coalesce(cast(s.base_quantity as float),'0') base_quantity,s.iyear,s.cwhcode,s.batch_id,s.dpdate,s.dvdate,s.cinvode from stock_saleousings s " +
                        " where s.bill_style in ('XSCKD','QTCKD') and s.iyear='"+iyear+"' "+ finalBcheck1 +" "+sqla+" ";
                return client.sql(cksql).fetch().all().collectList()
                .flatMap(cklist->{
                    List<StockCurrentLackVo> newcklist=JSON.parseArray(JSON.toJSONString(cklist), StockCurrentLackVo.class);
                    
                   return Flux.fromIterable(stockWarehousingsList)
                   .index((v,t)->{
                       t.setLackBaseQuantity(new BigDecimal(0));
                       BigDecimal qcNumber= BigDecimal.valueOf(0);
                       BigDecimal rkNumber= BigDecimal.valueOf(0);
                       BigDecimal ckNumber= BigDecimal.valueOf(0);
                       
                       if(newqclist.size()>0){
                           AtomicReference<List<StockCurrentLackVo>> collect1 = new AtomicReference<>(newqclist.stream().filter(a -> a.getCinvode().equals(t.getStockNum())).collect(Collectors.toList()));
                           if(StrUtil.isNotBlank(t.getCwhcode())){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList()));
                           }
                           if(StrUtil.isNotBlank(t.getBatchId()) ){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getBatchId().equals(t.getBatchId())).collect(Collectors.toList()));
                           }
                           if(StrUtil.isNotBlank(t.getDvdate()) ){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getDvdate().equals(t.getDvdate())).collect(Collectors.toList()));
                           }
                           qcNumber=collect1.get().stream().map(StockCurrentLackVo::getBaseQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                       }
                       if(newrklist.size()>0){
                           AtomicReference<List<StockCurrentLackVo>> collect1 = new AtomicReference<>(newrklist.stream().filter(a -> a.getCinvode().equals(t.getStockNum())).collect(Collectors.toList()));
                           if(StrUtil.isNotBlank(t.getCwhcode())){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList()));
                           }
                           if(StrUtil.isNotBlank(t.getBatchId()) ){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getBatchId().equals(t.getBatchId())).collect(Collectors.toList()));
                           }
                           if(StrUtil.isNotBlank(t.getDvdate()) ){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getDvdate().equals(t.getDvdate())).collect(Collectors.toList()));
                           }
                           rkNumber=collect1.get().stream().map(StockCurrentLackVo::getBaseQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                       }
                       if(newcklist.size()>0){
                           AtomicReference<List<StockCurrentLackVo>> collect1 = new AtomicReference<>(newcklist.stream().filter(a -> a.getCinvode().equals(t.getStockNum())).collect(Collectors.toList()));
                           if(StrUtil.isNotBlank(t.getCwhcode())){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList()));
                           }
                           if(StrUtil.isNotBlank(t.getBatchId()) ){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getBatchId().equals(t.getBatchId())).collect(Collectors.toList()));
                           }
                           if(StrUtil.isNotBlank(t.getDvdate()) ){
                               collect1.set(collect1.get().stream().filter(qc -> qc.getDvdate().equals(t.getDvdate())).collect(Collectors.toList()));
                           }
                           ckNumber=collect1.get().stream().map(StockCurrentLackVo::getBaseQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                       }

                       BigDecimal xcl=qcNumber.add(rkNumber).subtract(ckNumber);
                       t.setXcl(xcl);

                       // 在途到货、在途
                       Mono<List<StockCurrentLackVo>> dhMidWay = findByDaoHuoMidWay(t.getStockNum(), iyear,endDate)
                               .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                       // 在途入库
                       Mono<List<StockCurrentLackVo>> rukuMidWay = findByRukuMidWay(t.getStockNum(), iyear,endDate)
                               .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                       // 在途销货
                       Mono<List<StockCurrentLackVo>> xhMidWay = findByXiaoHuoMidWay(t.getStockNum(), iyear,endDate)
                               .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                       // 在途出库
                       Mono<List<StockCurrentLackVo>> ckMidWay = findByChukuMidWay(t.getStockNum(), iyear,endDate)
                               .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                       return Mono.zip(Mono.just(t),dhMidWay,rukuMidWay,xhMidWay,ckMidWay)
                       .map(a->{
                           StockCurrentLackVo t1 = a.getT1();
                           List<StockCurrentLackVo> dhlist = a.getT2();
                           List<StockCurrentLackVo> rklist2 = a.getT3();
                           List<StockCurrentLackVo> xhlist = a.getT4();
                           List<StockCurrentLackVo> cklist2 = a.getT5();
                           if(StrUtil.isNotBlank(t.getCwhcode())){
                               dhlist=dhlist.stream().filter(d->d.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList());
                               rklist2=rklist2.stream().filter(d->d.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList());
                               xhlist=xhlist.stream().filter(d->d.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList());
                               cklist2=cklist2.stream().filter(d->d.getCwhcode().equals(t.getCwhcode())).collect(Collectors.toList());
                           }
                           // 批次现存量查询所需
                           if(StrUtil.isNotBlank(t.getBatchId())){
                               dhlist=dhlist.stream().filter(d->d.getBatchId().equals(t.getBatchId())).collect(Collectors.toList());
                               rklist2=rklist2.stream().filter(d->d.getBatchId().equals(t.getBatchId())).collect(Collectors.toList());
                               xhlist=xhlist.stream().filter(d->d.getBatchId().equals(t.getBatchId())).collect(Collectors.toList());
                               cklist2=cklist2.stream().filter(d->d.getBatchId().equals(t.getBatchId())).collect(Collectors.toList());
                           }
                           if(StrUtil.isNotBlank(t.getDvdate())){
                               dhlist=dhlist.stream().filter(d->d.getDvdate().equals(t.getDvdate())).collect(Collectors.toList());
                               rklist2=rklist2.stream().filter(d->d.getDvdate().equals(t.getDvdate())).collect(Collectors.toList());
                               xhlist=xhlist.stream().filter(d->d.getDvdate().equals(t.getDvdate())).collect(Collectors.toList());
                               cklist2=cklist2.stream().filter(d->d.getDvdate().equals(t.getDvdate())).collect(Collectors.toList());
                           }

                           BigDecimal dh = dhlist.stream().map(StockCurrentLackVo::getMidWayDh).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                           BigDecimal rk = rklist2.stream().map(StockCurrentLackVo::getMidWayRk).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                           BigDecimal xh = xhlist.stream().map(StockCurrentLackVo::getMidWayXh).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                           BigDecimal ck = cklist2.stream().map(StockCurrentLackVo::getMidWayCk).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                           t1.setMidWayDh(dh);
                           t1.setMidWayRk(rkBcheck.equals("1")?BigDecimal.ZERO:rk);
                           t1.setMidWayXh(xh);
                           t1.setMidWayCk(ckBcheck.equals("1")?BigDecimal.ZERO:ck);
                           t1.setKeyong(t1.getXcl().add(t1.getMidWayDh().add(t1.getMidWayRk())).subtract(t1.getMidWayXh().add(t1.getMidWayCk())));

                           BigDecimal lackBaseQuantity = BigDecimal.ZERO;
                           if(StrUtil.isNotBlank(queryType)&&queryType.equals("xcl")){
                               if(bdocumStyle.equals("1")){
                                   lackBaseQuantity=t1.getBaseQuantity().add(xcl);
                               }else{
                                   BigDecimal subtract = xcl.subtract(t1.getBaseQuantity());
                                   // 大于等于0 （不赋，强制为0）
                                   lackBaseQuantity=subtract.compareTo(BigDecimal.ZERO)>-1?BigDecimal.ZERO:subtract;
                               }
                           }else if(StrUtil.isNotBlank(queryType)&&queryType.equals("keyong")){
                               if(bdocumStyle.equals("1")){
                                   lackBaseQuantity=t1.getBaseQuantity().add(t1.getKeyong());
                               }else{
//                                   lackBaseQuantity=t1.getBaseQuantity().subtract(t1.getKeyong());
                                   BigDecimal subtract = t1.getKeyong().subtract(t1.getBaseQuantity());
                                   // 大于等于0 （不赋，强制为0）
                                   lackBaseQuantity=subtract.compareTo(BigDecimal.ZERO)>-1?BigDecimal.ZERO:subtract;
                               }
                           }
                           t1.setLackBaseQuantity(lackBaseQuantity);
                           return t1;
                       });
                   })
                   .flatMap(it->it)
                   .collectList();    
                });
            });
        })
        .map(a->R.ok().setResult(a.stream().filter(sw -> !sw.getKeyong().equals(BigDecimal.ZERO)||!sw.getXcl().equals(BigDecimal.ZERO)).collect(Collectors.toList())));
    }

    // 在途到货、在途
    public Mono<List<StockCurrentLackVo>> findByDaoHuoMidWay(String cinvode,String iyear,String endDate){
        String sqla=StrUtil.isNotBlank(endDate)?" and sws.ddate between '"+endDate.substring(0,4)+"-01-01"+"' and '"+endDate+"' ":"";
        String sql="select sws.cinvode,\n" +
                "       sws.cwhcode,\n" +
                "       sws.batch_id,\n" +
                "       sws.dpdate,\n" +
                "       sws.dvdate,\n" +
                "       cast(sws.base_quantity as float) - cast(coalesce(sws.isum_ruku, '0') as float) mid_way_dh\n" +
                "from stock_warehousings sws\n" +
                "where sws.bill_style in ('CGDHD')\n" +
                "  and (sws.sourcetype not in ('CGRKD') or sws.sourcetype isnull or sws.sourcetype='') \n" +
                "  and sws.iyear = '"+iyear+"'\n" +
                "  and sws.cinvode = '"+cinvode+"' "+sqla+" union all select sws.cinvode,\n" +
                "       sws.cwhcode,\n" +
                "       sws.batch_id,\n" +
                "       sws.dpdate,\n" +
                "       sws.dvdate,\n" +
                "       cast(sws.base_quantity as float) - cast(coalesce(sws.isum_ruku, '0') as float) mid_way_dh\n" +
                "from stock_warehousings sws\n" +
                "where sws.bill_style='QC'\n" +
                "  and sws.iyear ='"+iyear+"' and sws.bcheck='1' and sws.cinvode = '"+cinvode+"' ";
        return client.sql(sql).fetch().all().collectList()
        .flatMapMany(skList-> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), StockCurrentLackVo.class)))
                .collectList()
                .map(a->a);
    }
    // 在途入库
    public Mono<List<StockCurrentLackVo>> findByRukuMidWay(String cinvode,String iyear,String endDate){
        String sqla=StrUtil.isNotBlank(endDate)?" and sws.ddate between '"+endDate.substring(0,4)+"-01-01"+"' and '"+endDate+"' ":"";
        String sql="select sws.cinvode,\n" +
                "       sws.cwhcode,\n" +
                "       sws.batch_id,\n" +
                "       sws.dpdate,\n" +
                "       sws.dvdate,\n" +
                "       sws.base_quantity as mid_way_rk\n" +
                "from stock_warehousings sws\n" +
                "where sws.bill_style in ('CGRKD','QTRKD')\n" +
                "  and sws.iyear = '"+iyear+"'\n" +
                "  and sws.cinvode = '"+cinvode+"' and sws.bcheck='0' "+sqla+" ";
        return client.sql(sql).fetch().all().collectList()
                .flatMapMany(skList-> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), StockCurrentLackVo.class)))
                .collectList();
    }
    // 在途销货
    public Mono<List<StockCurrentLackVo>> findByXiaoHuoMidWay(String cinvode,String iyear,String endDate){
        String sqla=StrUtil.isNotBlank(endDate)?" and sws.ddate between '"+endDate.substring(0,4)+"-01-01"+"' and '"+endDate+"' ":"";
        String sql="select sws.cinvode,\n" +
                "       sws.cwhcode,\n" +
                "       sws.batch_id,\n" +
                "       sws.dpdate,\n" +
                "       sws.dvdate,\n" +
                "       cast(sws.base_quantity as float) - cast(coalesce(sws.isum_chuku, '0') as float) mid_way_xh\n" +
                "from stock_saleousings sws\n" +
                "where sws.bill_style in ('XHD')\n" +
                "  and (sws.sourcetype not in ('XSCKD') or sws.sourcetype isnull or sws.sourcetype='') \n" +
                "  and sws.iyear = '"+iyear+"'\n" +
                "  and sws.cinvode = '"+cinvode+"' "+sqla+" union all select sws.cinvode,\n" +
                "       sws.cwhcode,\n" +
                "       sws.batch_id,\n" +
                "       sws.dpdate,\n" +
                "       sws.dvdate,\n" +
                "       cast(sws.base_quantity as float) - cast(coalesce(sws.isum_chuku, '0') as float) mid_way_dh\n" +
                "from stock_saleousings sws\n" +
                "where sws.bill_style='QC'\n" +
                "  and sws.iyear = '"+iyear+"' and sws.bcheck='1' ";
        return client.sql(sql).fetch().all().collectList()
                .flatMapMany(skList-> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), StockCurrentLackVo.class)))
                .collectList()
                .map(a->a);
    }
    // 在途出库
    public Mono<List<StockCurrentLackVo>> findByChukuMidWay(String cinvode,String iyear,String endDate){
        String sqla=StrUtil.isNotBlank(endDate)?" and sws.ddate between '"+endDate.substring(0,4)+"-01-01"+"' and '"+endDate+"' ":"";
        String sql="select sws.cinvode,\n" +
                "       sws.cwhcode,\n" +
                "       sws.batch_id,\n" +
                "       sws.dpdate,\n" +
                "       sws.dvdate,\n" +
                "       sws.base_quantity as mid_way_ck\n" +
                "from stock_saleousings sws\n" +
                "where sws.bill_style in ('XSCKD','CLLYD','QTCKD')\n" +
                "  and sws.iyear = '"+iyear+"'\n" +
                "  and sws.cinvode = '"+cinvode+"' and (sws.bcheck='0' or sws.bcheck isnull ) "+sqla+" ";
        return client.sql(sql).fetch().all().collectList()
                .flatMapMany(skList-> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), StockCurrentLackVo.class)))
                .collectList();
    }
}
