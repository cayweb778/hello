package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockTakingClass;
import org.boozsoft.domain.entity.stock.StockTakingCun;

import java.io.Serializable;

/**
 * @Description
 * @Author  lz
 * @Date 2022-05-07
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockTakingCunVo extends StockTakingCun implements Serializable {

    @ApiModelProperty(value = "存货分类")
    private String stockClass;
}
