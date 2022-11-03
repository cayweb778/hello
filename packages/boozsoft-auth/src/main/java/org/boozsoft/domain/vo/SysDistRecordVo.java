package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="档案分配表VO",description="档案分配表VO")
public class SysDistRecordVo {
	@ApiModelProperty(value = "账套编码", position = 1)
	private String accId;
	@ApiModelProperty(value = "档案唯一码", position = 2)
	private String recordUniqueCode;
	@ApiModelProperty(value = "集团客户档案中ID", position = 2)
	private String id;
}
