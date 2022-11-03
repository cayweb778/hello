package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Customer;

/**
 * 供应商 - 客户  用同一个
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="客户信息表",description="客户信息表")
public class CustomerVo extends Customer {

	private String uniqueCustclassName;
	private String ccode;
	private String ctype;
	private String accName;
	private String accStartDate; // 账套启用期间

	private String orgName;
	// 对应供应商|客户名称
	private String supName;
	// 母公司名称
	private String parentName;

	private String label;
	private String value;
	private String title;
	private String ccodeGroupCcode;
	private String custGroupName;
	private String deptName;
	private String psnName;
}
