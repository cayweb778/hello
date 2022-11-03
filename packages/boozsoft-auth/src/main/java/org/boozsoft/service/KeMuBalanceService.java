package org.boozsoft.service;

import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.springbooz.core.tool.result.R;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : KeMuMingXiService
 * @Description : 科目明细账
 * @Author : miao
 * @Date: 2021-06-23 14:57
 */
public interface KeMuBalanceService {
    


    /**
     * @Author myh
     * @Description 科目明细账
     * @Date 9:45 2021/6/24
     * @Param [bend, strDate, endDate, maxJc, minJc, minKm, timflg:是 期间 qj 还是 日期 rq,ishaveRjz 是否包含未记账  0否/1是; bz-币种,parameterAccuracyList-保留几位小数]
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R> 
     **/
    Mono<R> findAll(Integer page,Integer size,Pageable pageable,String bend, String strDate, String endDate, String maxJc, String minJc, String maxKm, String minKm, String timflg,
                    String ishaveRjz, String bz, String styleValue, Map<String, String> searchMap, Map<String, String> filterMap,
                    List<AccStyle> styleList,String database,String querytype,String accvoucherType,List<CodeKemu> kmList);
}
