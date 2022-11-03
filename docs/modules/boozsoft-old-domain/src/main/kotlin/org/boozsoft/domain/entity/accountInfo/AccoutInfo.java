package org.boozsoft.domain.entity.accountInfo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("accout_info" )
@ApiModel(value="账套信息基本表",description="accout_info: table")
public class AccoutInfo {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "账套编码", position = 1)
	private String accCode;

	@ApiModelProperty(value = "账套名称", position = 2)
	private String accName;

	@ApiModelProperty(value = "账套启用日期 ", position = 3)
	private String accStartDate;

	@ApiModelProperty(value = "账套会计年度起始日期（如01-01或04-01） ", position = 4)
	private String periodMonth;

	@ApiModelProperty(value = "账套启用会计期间号（1~12） ", position = 5)
	private String periodNum;

	@ApiModelProperty(value = "账套所属组织编码 ", position = 6)
	private String accGroup;

	@ApiModelProperty(value = "账套会计准则名称 ", position = 7)
	private String accStandard;

	@ApiModelProperty(value = "账套本位币国际代码（CHY） ", position = 8)
	private String currency;

	@ApiModelProperty(value = "账套本位币中文简称（元） ", position = 9)
	private String currencyCh;

	@ApiModelProperty(value = "账套本位币中文全称（人民币） ", position = 10)
	private String currencyName;

	@ApiModelProperty(value = "科目级次（4-2-2-2-2-2-2-2） ", position = 11)
	private String ccodeLevel;

	@ApiModelProperty(value = "账套机构全称(公司或单位名称) ", position = 12)
	private String accNameFull;

	@ApiModelProperty(value = "账套机构简称(公司或单位名称) ", position = 13)
	private String accNameCn;

	@ApiModelProperty(value = "上级集团唯一码 ", position = 14)
	private String groupCode;

	@ApiModelProperty(value = "上级集团占股比例 ", position = 15)
	private String groupShareRatio;

	@ApiModelProperty(value = "上级公司唯一码（可为空） ", position = 16)
	private String corpCode;

	@ApiModelProperty(value = "上级公司占股比例 ", position = 17)
	private String corpShareRatio;

	@ApiModelProperty(value = "行政区划唯一码 ", position = 18)
	private String uniqueCodeZone;

	@ApiModelProperty(value = "行业性质编码 ", position = 19)
	private String industryclassCode;
}
