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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-10-14 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_borrows_source" )
@ApiModel(value="借入借出下游表",description="借入借出下游表")
public class StockBorrowsSource  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@Transient
	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "下游单据类型 （JRJYRKD借入借用入库单、JRHHD借入还回单、JRHHCKD借入还回出库单、JCJYCKD借出借用单出库单、借JCGHD出归还单、JCGHRKD借出归还入库单、JRZHD借入转换单、JRZHCKD借入转换出库单、JCZHD借出转换单、JCZHRKD借出转换入库单）")
	private String xyBillStyle;

	@ApiModelProperty(value = "本单据类型 （YRJYD借入借用单、JRHHD借入还回单、JCJYD借出借用单、JCGHD借出归还单、JRZHD借入转换单、JCZHD借出转换单）")
	private String billStyle;

	@ApiModelProperty(value = "本单单号")
	private String ccode;

	@ApiModelProperty(value = "下游单号")
	private String xyccode;
}
