package org.boozsoft.domain.entity.accstandard;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_acc_standard" )
@ApiModel(value="会计准则表",description="会计准则表")
public class AccStandard {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueAccStandard;

	@ApiModelProperty(value = "会计准则名称", position = 2)
	private String accStandardName;

	@ApiModelProperty(value = "是否预算会计(1.是;0否)", position = 3)
	private String flagYusuan;

	@ApiModelProperty(value = "排序号", position = 4)
	private Integer num;

	@ApiModelProperty(value = "科目类型唯一码", position = 5)
	private String accStyleUnique;

	@ApiModelProperty(value = "科目级次 第一级(4或3)", position = 4)
	private Integer codeFirst;

	@ApiModelProperty(value = "预算科目公式", position = 5)
	private String yusuan;
	@ApiModelProperty(value = "财务科目公式", position = 5)
	private String caiwu;
	@ApiModelProperty(value = "会计准则发行时间", position = 5)
	private String lawsTime;
	@ApiModelProperty(value = "国家地区", position = 5)
	private String country;

	@Transient
	private String styleName;
}
