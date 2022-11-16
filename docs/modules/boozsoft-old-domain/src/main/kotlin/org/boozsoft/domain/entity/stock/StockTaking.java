package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.boozsoft.domain.vo.stock.StockTakingClassVo;
import org.boozsoft.domain.vo.stock.StockTakingCunVo;
import org.boozsoft.domain.vo.stock.StockTakingCunbatchVo;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Table ( "stock_taking" )
@ApiModel(value="盘点单表",description="盘点单表")
public class StockTaking {


	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;
	@ApiModelProperty(value = "盘点单编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "制单人（ 操作员",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "制单日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "盘点开始日期",hidden = true)
	private String startDate;
	@ApiModelProperty(value = "盘点结束日期",hidden = true)
	private String stopDate;
	@ApiModelProperty(value = "盘点范围（0.全仓库、1.存货分类、2.存货档案、3.存货+批号）",hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "盘点人唯一码（员工档案）",hidden = true)
	private String pdUserid;
	@ApiModelProperty(value = "库管员唯一码（员工档案）",hidden = true)
	private String kgUserid;
	@ApiModelProperty(value = "盘点仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "锁定状态(1锁定、0或空未锁定）",hidden = true)
	private String lockStatus;
	@ApiModelProperty(value = "锁定时间",hidden = true)
	private String lockDate;
	@ApiModelProperty(value = "审核状态（1审核，1或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核人（操作员ID)",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree5;

	//存货分类
	@Transient
	private String[] classList;
	//存货
	@Transient
	private String[] cunList;
	//明细list
	@Transient
	private List<StockTakings> stsList;
	@Transient
	private StockWarehousing py;
	@Transient
	private List<StockWarehousings> pyList;
	@Transient
	private StockSaleousing pk;
	@Transient
	private List<StockSaleousings> pkList;
	@Transient
	private List<StockTakingSource> sList;
	@Transient
	private List<StockCurrentstock> ctList;
	@Transient
	private List<StockCurrentstock> ctsList = new ArrayList<>();
	//验证范围使用
	@Transient
	private List<StockTaking> stList = new ArrayList<>();
	@Transient
	private List<StockTakingClassVo> stcvList = new ArrayList<>();
	@Transient
	private List<StockTakingCunVo> stcList = new ArrayList<>();
	@Transient
	private List<StockTakingCunbatchVo> stcpList = new ArrayList<>();
	@Transient
	private String rkBcheck;
	@Transient
	private String ckBcheck;
}
