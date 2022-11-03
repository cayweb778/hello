package org.boozsoft.domain.entity.stock;

import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2021-12-31 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_cangku_level_record" )
@ApiModel(value="仓库级别档案表",description="仓库级别档案表")
public class StockCangkuLevelRecord  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	@ApiModelProperty(value = "公司ID")
	private String accId;

	@ApiModelProperty(value = "仓库ID")
	private String cangkuId;
	@ApiModelProperty(value = "仓库级别表ID")
	private String cangkuLevelId;
	@ApiModelProperty(value = "仓库级别表 级次")
	private String cangkuLevelOrder;
	@ApiModelProperty(value = "系统编码")
	private String sysNum;
	@ApiModelProperty(value = "上级ID,大于等于3级才赋值")
	private String parentId;
	@ApiModelProperty(value = "递归路径")
	private String parentLevelNum;

	@ApiModelProperty(value = "档案编码(暂用)")
	private String recordNum;

	@ApiModelProperty(value = "档案名称")
	private String recordName;

	@ApiModelProperty(value = "说明")
	private String recordExplain;

	@ApiModelProperty(value = "是否默认 1是，0否")
	private String recordDefault;

	@ApiModelProperty(value = "状态 1正常")
	private String recordFlag;
	@ApiModelProperty(value = "是否末级 1是")
	private String recordBend;
}
