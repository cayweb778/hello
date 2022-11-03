//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.result.page;

import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@ApiModel(
        value = "PageResult对象",
        description = "PageResult对象"
)
public class PageUtil {
    public static int totalPage(int totalCount, int pageSize) {
        if (pageSize == 0) {
            return 0;
        } else {
            return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        }
    }

    public static <T> PageResult  of(Pageable pageable, List<T> items, int total){
        PageResult<T> tPageResult =  new PageResult(pageable.getPageNumber(),pageable.getPageSize(),total);
        tPageResult.addAll(items);
        return tPageResult;
    }
    public static <T> Mono<PageResult<T>> of(Flux<T> itemsFun, Mono<Long> totalFun, Pageable pageable){
        return itemsFun.collectList().zipWith(totalFun).map(it->{
            PageResult<T> tPageResult =  new PageResult<T>(pageable.getPageNumber(),pageable.getPageSize(),it.getT2().intValue());
            tPageResult.addAll(it.getT1());
            return tPageResult;
        });
    }
}
