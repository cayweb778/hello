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
 * @Date 2022-10-29 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_ad" )
@ApiModel(value="",description="")
public class StockAd  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@Transient
	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "单据类型(ZZ组装、CX拆卸)")
	private String adStyle;

	private String ccode;

	private String ddate;

	private String cmaker;

	@ApiModelProperty(value = "制单时间（年月日时分秒）")
	private String cmakerTime;

	private String cdepcode;

	private String cpersoncode;

	@ApiModelProperty(value = "BOM单号")
	private String bomCode;

	@ApiModelProperty(value = "BOM版本号")
	private String bomVer;

	@ApiModelProperty(value = "计量单位编码")
	private String cunitid;

	@ApiModelProperty(value = "数量")
	private String cnumber;

	@ApiModelProperty(value = "级次")
	private String bomLevel;

	@ApiModelProperty(value = "费用金额（4位小数点）")
	private String feiyongJe;

	@ApiModelProperty(value = "审核状态（1审核，1或其他未审）")
	private String bcheck;

	private String bcheckUser;

	private String bcheckTime;

	private String cmemo;
	private String bomVerName;

	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList;
}
