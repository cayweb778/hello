package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockCustPriceUser;
import org.boozsoft.domain.entity.stock.StockSupPriceUser;

import java.io.Serializable;

/**
 * @Description  存货价格客户
 * @Author  lz
 * @Date 2022-04-09
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="存货价格客户",description="存货价格客户")
public class StockSupPriceUserVo extends StockSupPriceUser implements Serializable {

    @ApiModelProperty(value = "客户编码（不允许重复）")
    private String custCode;

    @ApiModelProperty(value = "客户简称（不允许重复）")
    private String custAbbname;


}
