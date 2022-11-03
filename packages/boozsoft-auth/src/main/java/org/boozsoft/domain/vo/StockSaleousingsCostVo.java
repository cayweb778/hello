package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockSaleousings;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * @Description  盘点单明细
 * @Author  lz 
 * @Date 2022-04-09
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="盘点单明细",description="盘点单明细")
public class StockSaleousingsCostVo extends StockSaleousings implements Serializable {

	private String buname;
}
