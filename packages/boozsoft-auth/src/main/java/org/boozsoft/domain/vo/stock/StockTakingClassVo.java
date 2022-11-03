package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockCustPrice;
import org.boozsoft.domain.entity.stock.StockTakingClass;

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
public class StockTakingClassVo extends StockTakingClass implements Serializable {

    private String name;
}
