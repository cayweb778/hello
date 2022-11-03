package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.stock.StockTakings;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 计量单位
 * @Author  lz 
 * @Date 2022-07-21
 */
@Data
public class SysUnitOfMeaListVo extends SysUnitOfMeaList implements Serializable {
	private String umid;
}
