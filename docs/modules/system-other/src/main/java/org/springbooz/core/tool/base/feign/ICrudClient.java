package org.springbooz.core.tool.base.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springbooz.core.tool.result.R;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

/**
 * ss
 * @param <T> ss
 */
public interface ICrudClient<T> {
    /**
     * 查询列表
     * @return 返回列表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询列表")
    Mono<R> list();

    /**
     * 删除
     * @param id 传入id
     * @return ss
     */
    @DeleteMapping
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "删除", notes = "传入id")
    Mono<Void> delete(String id);

    /**
     * 通过ID查询
     * @param id 传入code
     * @return ss
     */
    @GetMapping
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "通过ID查询", notes = "传入code")
    Mono get(String id);

    /**
     *  新增或修改
     * @param object 传入code
     * @return 保存对象
     */
    @PostMapping
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增或修改", notes = "传入code")
    Mono save(T object);

}
