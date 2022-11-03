//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.result.page;

import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@ApiModel(
        value = "PageResult对象",
        description = "PageResult对象"
)
public class PageResult<T> {
    private int page;
    private int pageSize;
    private int totalPage;
    private int total;
    private List<T> items;

    public PageResult() {
        this(0, 20);
    }

    public PageResult(int page, int pageSize) {
        this.page = Math.max(page, 0);
        this.pageSize = pageSize <= 0 ? 20 : pageSize;
    }

    public PageResult(int page, int pageSize, int total) {
        this(page, pageSize);
        this.total = total;


        this.totalPage = PageUtil.totalPage(total, pageSize);
    }



    public PageResult<T> addAll(List<T> items){
        this.items=items;
        return this;
    }
    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    /** @deprecated */
    @Deprecated
    public int getNumPerPage() {
        return this.pageSize;
    }

    /** @deprecated */
    @Deprecated
    public void setNumPerPage(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isFirst() {
        return this.page == 0;
    }

    public boolean isLast() {
        return this.page >= this.totalPage - 1;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
