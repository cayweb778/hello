package org.boozsoft.service;

import org.boozsoft.domain.entity.stock.StockCangku;
import org.boozsoft.domain.vo.stock.StockCangkuLevelRecordVo;
import org.boozsoft.repo.stock.StockCangkuLevelRecordRepository;
import org.boozsoft.repo.stock.StockCangkuRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StockCangkuLevelRecordService {
    @Autowired
    StockCangkuLevelRecordRepository stockCangkuLevelRecordRepository;
    @Autowired
    StockCangkuRepository stockCangkuRepository;

    public Mono<R> findCangkuJoinName(String id){
        return stockCangkuLevelRecordRepository.findAllRecord().collectList().zipWith(stockCangkuRepository.findAll().collectList())
        .flatMap(data->{
            List<StockCangkuLevelRecordVo>recordList=data.getT1();
            List<StockCangku>cangkuList=data.getT2();

            List<StockCangkuLevelRecordVo> collect2 = recordList.stream().filter(a -> a.getCangkuLevelOrder().equals("2")).collect(Collectors.toList());
            List<StockCangkuLevelRecordVo> collect3 = recordList.stream().filter(a -> a.getCangkuLevelOrder().equals("3")).collect(Collectors.toList());
            List<StockCangkuLevelRecordVo> collect4 = recordList.stream().filter(a -> a.getCangkuLevelOrder().equals("4")).collect(Collectors.toList());
            List<StockCangkuLevelRecordVo> collect5 = recordList.stream().filter(a -> a.getCangkuLevelOrder().equals("5")).collect(Collectors.toList());
            List<StockCangkuLevelRecordVo> collect6 = recordList.stream().filter(a -> a.getCangkuLevelOrder().equals("6")).collect(Collectors.toList());

            List<StockCangkuLevelRecordVo> collect = recordList.stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList());
            String cangkuRecordJoinName="";
            String cangkuRecordJoinId="";
            if(collect.size()>0){
                if(collect.get(0).getCangkuLevelOrder().equals("2")){
                    cangkuRecordJoinId=collect.get(0).getCangkuId()+"\\"+collect.get(0).getId();
                    cangkuRecordJoinName=collect.get(0).getCangkuName()+"\\"+collect.get(0).getRecordName();
                }else if(collect.get(0).getCangkuLevelOrder().equals("3")){
                    List<StockCangkuLevelRecordVo> temp2 = collect2.stream().filter(a -> a.getId().equals(collect.get(0).getParentId())).collect(Collectors.toList());
                    cangkuRecordJoinId=collect.get(0).getCangkuId()+"\\"+temp2.get(0).getId()+"\\"+collect.get(0).getId();
                    cangkuRecordJoinName=collect.get(0).getCangkuName()+"\\"+temp2.get(0).getRecordName()+"\\"+collect.get(0).getRecordName();
                }else if(collect.get(0).getCangkuLevelOrder().equals("4")){
                    List<StockCangkuLevelRecordVo> temp3 = collect3.stream().filter(a -> a.getId().equals(collect.get(0).getParentId())).collect(Collectors.toList());
                    List<StockCangkuLevelRecordVo> temp2 = collect2.stream().filter(a -> a.getId().equals(temp3.get(0).getParentId())).collect(Collectors.toList());
                    cangkuRecordJoinId=collect.get(0).getCangkuId()+"\\"+temp2.get(0).getId()+"\\"+temp3.get(0).getId()+"\\"+collect.get(0).getId();
                    cangkuRecordJoinName=collect.get(0).getCangkuName()+"\\"+temp2.get(0).getRecordName()+"\\"+temp3.get(0).getRecordName()+"\\"+collect.get(0).getRecordName();
                }else if(collect.get(0).getCangkuLevelOrder().equals("5")){
                    List<StockCangkuLevelRecordVo> temp4 = collect4.stream().filter(a -> a.getId().equals(collect.get(0).getParentId())).collect(Collectors.toList());
                    List<StockCangkuLevelRecordVo> temp3 = collect3.stream().filter(a -> a.getId().equals(temp4.get(0).getParentId())).collect(Collectors.toList());
                    List<StockCangkuLevelRecordVo> temp2 = collect2.stream().filter(a -> a.getId().equals(temp3.get(0).getParentId())).collect(Collectors.toList());
                    cangkuRecordJoinId=collect.get(0).getCangkuId()+"\\"+temp2.get(0).getId()+"\\"+temp3.get(0).getId()+"\\"+temp4.get(0).getId()+"\\"+collect.get(0).getId();
                    cangkuRecordJoinName=collect.get(0).getCangkuName()+"\\"+temp2.get(0).getRecordName()+"\\"+temp3.get(0).getRecordName()+"\\"+temp4.get(0).getRecordName()+"\\"+collect.get(0).getRecordName();
                }else if(collect.get(0).getCangkuLevelOrder().equals("6")){
                    List<StockCangkuLevelRecordVo> temp5 = collect5.stream().filter(a -> a.getId().equals(collect.get(0).getParentId())).collect(Collectors.toList());
                    List<StockCangkuLevelRecordVo> temp4 = collect4.stream().filter(a -> a.getId().equals(temp5.get(0).getParentId())).collect(Collectors.toList());
                    List<StockCangkuLevelRecordVo> temp3 = collect3.stream().filter(a -> a.getId().equals(temp4.get(0).getParentId())).collect(Collectors.toList());
                    List<StockCangkuLevelRecordVo> temp2 = collect2.stream().filter(a -> a.getId().equals(temp3.get(0).getParentId())).collect(Collectors.toList());
                    cangkuRecordJoinId=collect.get(0).getCangkuId()+"\\"+temp2.get(0).getId()+"\\"+temp3.get(0).getId()+"\\"+temp4.get(0).getId()+"\\"+temp5.get(0).getId()+"\\"+collect.get(0).getId();
                    cangkuRecordJoinName=collect.get(0).getCangkuName()+"\\"+temp2.get(0).getRecordName()+"\\"+temp3.get(0).getRecordName()+"\\"+temp4.get(0).getRecordName()+"\\"+temp5.get(0).getRecordName()+"\\"+collect.get(0).getRecordName();
                }
            }
            else{
                List<StockCangku> collect1 = cangkuList.stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList());
                cangkuRecordJoinName=collect1.size()>0?collect1.get(0).getCkName():"";
                cangkuRecordJoinId=collect1.size()>0?collect1.get(0).getId():"";
                collect.add(new StockCangkuLevelRecordVo());
            }
            String finalCangkuRecordJoinName = cangkuRecordJoinName;
            String finalCangkuRecordJoinId = cangkuRecordJoinId;
            collect.forEach(a->{
                        a.setCangkuRecordJoinName(finalCangkuRecordJoinName);
                        a.setCangkuRecordJoinId(finalCangkuRecordJoinId);
                    });
                    return Mono.just(collect);
                }).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
}
