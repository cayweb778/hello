package org.boozsoft.domain.entity.group;

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
 * @Date 2022-02-18 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "_app_group_customer_class_account" )
@ApiModel(value="客户分类分配表",description="客户分类分配表")
public class GroupCustomerClassAccount implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "档案唯一码")
	private String dataUnique;

	@ApiModelProperty(value = "分配类型(1组织、2账套)")
	private String ctype;

	@ApiModelProperty(value = "组织唯一码")
	private String orgUnique;

	@ApiModelProperty(value = "租户id")
	private String tenantId;

	@ApiModelProperty(value = "分配人id")
	private String assignUserId;

	@ApiModelProperty(value = "分配时间")
	private String assignDate;

	@ApiModelProperty(value = "引入状态；1已引入，0")
	private String flag;

	@ApiModelProperty(value = "引入人id")
	private String acceptUserId;

	@ApiModelProperty(value = "引入时间")
	private String acceptDate;
}
