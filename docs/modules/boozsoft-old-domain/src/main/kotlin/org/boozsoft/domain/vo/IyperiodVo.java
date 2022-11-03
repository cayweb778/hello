package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="会计凭证表",description="会计凭证表")
public class IyperiodVo {

	@ApiModelProperty(value = "会计期间年月   (会计年度+会计月份)", position = 5)
	private String iyperiod;
	private String ccode;

}
