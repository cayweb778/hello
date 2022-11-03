package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.SysUnitOfMeaMany;
import org.boozsoft.domain.vo.stock.StockWarehousings2Vo;
import org.boozsoft.domain.vo.stock.StockXySourceVo;
import org.boozsoft.repo.SysUnitOfMeaListRepository;
import org.boozsoft.repo.SysUnitOfMeaManyRepository;
import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.repo.stock.StockXyCsourceRepository;
import org.boozsoft.service.StockCangkuLevelRecordService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/stockXySource")
public class StockXySourceController {

    @Autowired
    StockXyCsourceRepository xyCsourceRepository;
    @Autowired
    SysUnitOfMeaRepository unitOfMeaRepository;
    @Autowired
    SysUnitOfMeaListRepository listRepository;
    @Autowired
    SysUnitOfMeaManyRepository manyRepository;
    @Autowired
    StockCangkuLevelRecordService stockCangkuLevelRecordService;
    @Autowired
    DatabaseClient client;



    @PostMapping("findAllByCcode")
    public Mono<R> findAllByCcode(String ccode){
        return xyCsourceRepository.findAllByCcode(ccode).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findByXyDataSourrceMap")
    public Mono<R> findByXyDataSourrceMap(@RequestBody Map map){
        return unitOfMeaRepository.findAll().collectList()
            .flatMap(djl->{
                return listRepository.findAll().collectList()
                    .flatMap(duojl->{
                        return manyRepository.findAll().collectList()
                            .flatMap(many->{
                                return xyCsourceRepository.findByXyDataSourrceMap(map.get("xytype").toString(),map.get("sytype").toString(),map.get("syccode").toString()).collectList()
                                .flatMap(list->{
                                    list.forEach(a->{
                                        List<SysUnitOfMea> collect = djl.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                        a.setCunitName(collect.size()>0?collect.get(0).getUnitName():"");
                                        // 查找多计量
                                        if(collect.size()==0){
                                            List<SysUnitOfMeaMany> collect1 = many.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                            if (collect1.size()>0) {
                                                List<SysUnitOfMeaList> aTrue = duojl.stream().filter(d ->d.getIsMain().equals("true")&& d.getManyCode().equals(collect1.get(0).getUnitCode())).collect(Collectors.toList());
                                                a.setCunitName(aTrue.size()>0?aTrue.get(0).getUnitName():"");
                                            }
                                        }
                                    });
                                    return Mono.just(list);
                                });
                            });
                    });
            }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByXyDataSourrceSearch")
    public Mono<R> findByXyDataSourrceSearch(@RequestBody Map map){
        List<StockXySourceVo> list = JSON.parseArray(map.get("list").toString(),StockXySourceVo.class);
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));

        if(StrUtil.isNotBlank(searchMap.get("value"))){
            if(searchMap.get("requirement").equals("ccode")){
                list=list.stream().filter(a->a.getXyccode().contains(searchMap.get("value"))).collect(Collectors.toList());
            }
            if(searchMap.get("requirement").equals("cwhcode")){
                list=list.stream().filter(a->a.getCwhcodeName().contains(searchMap.get("value"))).collect(Collectors.toList());
            }
            if(searchMap.get("requirement").equals("cinvode")){
                list=list.stream().filter(a->a.getStockNum().contains(searchMap.get("value"))).collect(Collectors.toList());
            }
            if(searchMap.get("requirement").equals("cinvodeName")){
                list=list.stream().filter(a->a.getStockName().contains(searchMap.get("value"))).collect(Collectors.toList());
            }
            if(searchMap.get("requirement").equals("ddate")){
                list=list.stream().filter(a->a.getXyccodeDate().equals(searchMap.get("value"))).collect(Collectors.toList());
            }
            if(searchMap.get("requirement").equals("cmemo")){
                list=list.stream().filter(a->a.getCmemo().contains(searchMap.get("value"))).collect(Collectors.toList());
            }
        }
        return Mono.just(R.ok().setResult(list));
    }


    /**
     * 下游表明细
     * @param map
     * @return
     */
    @PostMapping("findBySyDataSourrceMap")
    public Mono<R> findBySyDataSourrceMap(@RequestBody Map map){
        return unitOfMeaRepository.findAll().collectList()
                .flatMap(djl->{
                    return listRepository.findAll().collectList()
                            .flatMap(duojl->{
                                return manyRepository.findAll().collectList()
                                        .flatMap(many->{
                                            return xyCsourceRepository.findBySyDataSourrceMap(map.get("xytype").toString(),map.get("sytype").toString(),map.get("xyccode").toString()).collectList()
                                                    .flatMap(list->{
                                                        list.forEach(a->{
                                                            List<SysUnitOfMea> collect = djl.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                                            a.setCunitName(collect.size()>0?collect.get(0).getUnitName():"");
                                                            // 查找多计量
                                                            if(collect.size()==0){
                                                                List<SysUnitOfMeaMany> collect1 = many.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                                                if (collect1.size()>0) {
                                                                    List<SysUnitOfMeaList> aTrue = duojl.stream().filter(d ->d.getIsMain().equals("true")&& d.getManyCode().equals(collect1.get(0).getUnitCode())).collect(Collectors.toList());
                                                                    a.setCunitName(aTrue.size()>0?aTrue.get(0).getUnitName():"");
                                                                }
                                                            }
                                                        });
                                                        return Mono.just(list);
                                                    });
                                        });
                            });
                }).map(a->R.ok().setResult(a));
    }

    /**
     * 获取销货下游
     * @return
     */
    @PostMapping("findByXyOutDataSourrce")
    public Mono<R> findByXyOutDataSourrce(@RequestBody Map map){
        String billStyle = map.get("billStyle").toString();
        String code = map.get("code").toString();
        String type = map.get("type").toString();
        return xyCsourceRepository.findByXyOutDataSourrce(billStyle,code).filter(it->StrUtil.isBlank(type)?true:it.getXyBillStyle().equals(type)).collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 获取销货来源
     * @return
     */
    @PostMapping("findByLyOutDataSourrce")
    public Mono<R> findByLyOutDataSourrce(@RequestBody Map map){
        String billStyle = map.get("billStyle").toString();
        String code = map.get("code").toString();
        String type = map.get("type").toString();
        return xyCsourceRepository.findByLyOutDataSourrce(billStyle,code).filter(it->StrUtil.isBlank(type)?true:it.getXyBillStyle().equals(type)).collectList().map(a->R.ok().setResult(a));
    }


    /**
     * 检验上游子表行唯一码是否存在下游
     * @param map
     * @return
     */
    @PostMapping("verifySyLineCodeExistXyData")
    public Mono<R> verifySyLineCodeExistXyData(@RequestBody Map map){
        String billStyle=map.get("billStyle").toString();
        String xyIyear=map.get("xyIyear").toString();
        String sourcedetailId=map.get("sourcedetailId").toString();
        return xyCsourceRepository.verifySyLineCodeExistXyData(billStyle,xyIyear,sourcedetailId).map(a->R.ok().setResult(a));
    }

    /**
     * 查询下游表主表数据
     * @param map
     * @return
     */
    @PostMapping("findByXyDataMainSourrceMap")
    public Mono<R> findByXyDataMainSourrceMap(@RequestBody Map map){
        return unitOfMeaRepository.findAll().collectList()
                .flatMap(djl->{
                    return listRepository.findAll().collectList()
                            .flatMap(duojl->{
                                return manyRepository.findAll().collectList()
                                        .flatMap(many->{
                                            return xyCsourceRepository.findByXyDataMainSourrceMap(map.get("xytype").toString(),map.get("sytype").toString(),map.get("syccode")==null?"":map.get("syccode").toString()).collectList()
                                                    .flatMap(list->{
                                                        list.forEach(a->{
                                                            List<SysUnitOfMea> collect = djl.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                                            a.setCunitName(collect.size()>0?collect.get(0).getUnitName():"");
                                                            // 查找多计量
                                                            if(collect.size()==0){
                                                                List<SysUnitOfMeaMany> collect1 = many.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                                                if (collect1.size()>0) {
                                                                    List<SysUnitOfMeaList> aTrue = duojl.stream().filter(d ->d.getIsMain().equals("true")&& d.getManyCode().equals(collect1.get(0).getUnitCode())).collect(Collectors.toList());
                                                                    a.setCunitName(aTrue.size()>0?aTrue.get(0).getUnitName():"");
                                                                }
                                                            }
                                                        });
                                                        return Mono.just(list);
                                                    });
                                        });
                            });
                }).map(a->R.ok().setResult(a));
    }

    /**
     * 查询上游表主表数据
     * @param map
     * @return
     */
    @PostMapping("findBySyDataMainSourrceMap")
    public Mono<R> findBySyDataMainSourrceMap(@RequestBody Map map){
        return unitOfMeaRepository.findAll().collectList()
                .flatMap(djl->{
                    return listRepository.findAll().collectList()
                            .flatMap(duojl->{
                                return manyRepository.findAll().collectList()
                                        .flatMap(many->{
                                            return xyCsourceRepository.findBySyDataMainSourrceMap(map.get("xytype").toString(),map.get("sytype").toString(),map.get("xyccode")==null?"":map.get("xyccode").toString()).collectList()
                                                    .flatMap(list->{
                                                        list.forEach(a->{
                                                            List<SysUnitOfMea> collect = djl.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                                            a.setCunitName(collect.size()>0?collect.get(0).getUnitName():"");
                                                            // 查找多计量
                                                            if(collect.size()==0){
                                                                List<SysUnitOfMeaMany> collect1 = many.stream().filter(d -> d.getId().equals(a.getCunitid())).collect(Collectors.toList());
                                                                if (collect1.size()>0) {
                                                                    List<SysUnitOfMeaList> aTrue = duojl.stream().filter(d ->d.getIsMain().equals("true")&& d.getManyCode().equals(collect1.get(0).getUnitCode())).collect(Collectors.toList());
                                                                    a.setCunitName(aTrue.size()>0?aTrue.get(0).getUnitName():"");
                                                                }
                                                            }
                                                        });
                                                        return Mono.just(list);
                                                    });
                                        });
                            });
                }).map(a->R.ok().setResult(a));
    }

    /**
     * 查询下游单据
     * @param map
     * @return
     */
    @PostMapping("findByIyearAndCcodeAndXyBillStyle")
    public Mono<R> findByIyearAndCcodeAndXyBillStyle(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String ccode=map.get("ccode").toString();
        String xyBillStyle=map.get("xyBillStyle").toString();
        return xyCsourceRepository.findByIyearAndCcodeAndXyBillStyle(iyear,ccode,xyBillStyle).map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }


    @PostMapping("findByStockWareSMax")
    public Mono<R> findByStockWareSMax(@RequestBody Map map){
        String ccode=map.get("ccode").toString();
        List<Integer> listStr=new ArrayList<>();
        List<StockWarehousings2Vo> daohuo=new ArrayList<>();
        List<StockWarehousings2Vo> ruku=new ArrayList<>();
        List<StockWarehousings2Vo> fapiao=new ArrayList<>();
        List<Map> listMap=new ArrayList<>();
        return xyCsourceRepository.findByStockWareSMax(ccode).collectList()
        .flatMap(list->{
            list.forEach(a->{
                StockWarehousings2Vo v=new StockWarehousings2Vo();
                StockWarehousings2Vo v2=new StockWarehousings2Vo();
                StockWarehousings2Vo v3=new StockWarehousings2Vo();
                v.setIsumDaohuo(a.getIsumDaohuo());
                v2.setIsumRuku(a.getIsumRuku());
                v3.setIsumFapiao(a.getIsumFapiao());
                daohuo.add(v);
                ruku.add(v2);
                fapiao.add(v3);

                listStr.add(Integer.valueOf(a.getIsumDaohuo()));
                listStr.add(Integer.valueOf(a.getIsumRuku()));
                listStr.add(Integer.valueOf(a.getIsumFapiao()));
            });
            // 最大值
            Integer max = listStr.stream().reduce(Integer::max).get();
            if(max>0){
                long count = daohuo.stream().filter(a -> Integer.valueOf(a.getIsumDaohuo()) == max).count();
                long count2 = ruku.stream().filter(a -> Integer.valueOf(a.getIsumRuku()) == max).count();
                long count3 = fapiao.stream().filter(a -> Integer.valueOf(a.getIsumFapiao()) == max).count();
                if(count>0){
                    Map map1=new HashMap<>();
                    map1.put("daohuo",max);
                    listMap.add(map1);
                }else if(count2>0){
                    Map map1=new HashMap<>();
                    map1.put("ruku",max);
                    listMap.add(map1);
                }else if(count3>0){
                    Map map1=new HashMap<>();
                    map1.put("fapiao",max);
                    listMap.add(map1);
                }
            }
            return Mono.just(listMap);
        }).map(R::ok);
    }

    @PostMapping("findByXySyCcode")
    public Mono<R> findByXySyCcode(@RequestBody Map map){
        String xytype=map.get("xytype").toString();
        String sytype=map.get("sytype").toString();
        String syccode=map.get("syccode").toString();
        return xyCsourceRepository.findByXyDataMainSourrceMap(xytype,sytype,syccode).collectList().map(R::ok);
    }

    @PostMapping("findAllByBillStyleAndIyearAndCcode")
    public Mono<R> findAllByBillStyleAndIyearAndCcode(@RequestBody Map map){
        String billStyle=map.get("billStyle").toString();
        String iyear=map.get("iyear").toString();
        String syccode=map.get("syccode").toString();
        return xyCsourceRepository.findAllByBillStyleAndIyearAndCcode( billStyle, iyear, syccode).collectList().map(R::ok);
    }

    /**
     * @description: 其他入库单独立查询来源单据接口
     * @author: miao
     * @date: 2022/11/2 15:40
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("findQTRKD_OnlyByXyData")
    public Mono<R> findQTRKD_OnlyByXyData(@RequestBody Map map){
        String ccode=map.get("ccode").toString();

        String sql="select xy.xy_bill_style,xy.xyccode,xy.xyccode_date from stock_xy_csource xy where xy.xy_bill_style='QTRKD' and xy.xyccode='"+ccode+"' union all\n" +
                "select xy.xy_bill_style,xy.syccode as xyccode,xy.syccode_date as xyccode_date from stock_changes_source xy where xy.xy_bill_style='QTRKD' and xy.syccode='"+ccode+"' union all\n" +
                "select xy.xy_bill_style,xy.syccode as xyccode,xy.syccode_date as xyccode_date from stock_taking_source xy where xy.xy_bill_style='QTRKD' and xy.syccode='"+ccode+"' union all\n" +
                "select xy.xy_bill_style,xy.syccode as xyccode,xy.syccode_date as xyccode_date from stock_transfer_source xy where xy.xy_bill_style='QTRKD' and xy.syccode='"+ccode+"' ";
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockXySourceVo> listVo = JSON.parseArray(JSON.toJSONString(list), StockXySourceVo.class);
            return Mono.just(listVo);
        }).map(R::ok);
    }

}