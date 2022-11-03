package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="库存台账",description="库存台账")
public class StockKctzRestVo implements Serializable {


	private String sumBq;
	private String sumIm;
	private String sumBq1;
	private String sumBq2;
	private Integer size;
	List<StockKctzVo> list = new ArrayList<>();

}
