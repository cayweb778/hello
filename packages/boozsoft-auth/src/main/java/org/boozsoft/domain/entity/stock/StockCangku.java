package org.boozsoft.domain.entity.stock;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("stock_cangku" )
@ApiModel(value="仓库档案",description="仓库档案")
public class StockCangku {

  @Id
  @CreatedBy
  @JsonSerialize(using = ToStringSerializer.class)
  private String id;
  @ApiModelProperty(value = "是否启用工厂；1是", hidden = true)
  private String plantFlag;
  @ApiModelProperty(value = "工厂唯一码", hidden = true)
  private String plantUnique;
  @ApiModelProperty(value = "仓库编码", hidden = true)
  private String ckNum;
  @ApiModelProperty(value = "仓库名称", hidden = true)
  private String ckName;
  @ApiModelProperty(value = "仓库地址", hidden = true)
  private String ckAddress;
  @ApiModelProperty(value = "行政区划（市）", hidden = true)
  private String ckCity;
  @ApiModelProperty(value = "状态，1正常", hidden = true)
  private String ckFlag;
  @ApiModelProperty(value = "备注", hidden = true)
  private String ckRemark;
  @ApiModelProperty(value = "是否库位管理；1是", hidden = true)
  private String ckNumberFlag;
  @ApiModelProperty(value = "库位数量", hidden = true)
  private long ckNumber;
  @ApiModelProperty(value = "管理属性:  2供应商直发库/1自有仓库", hidden = true)
  private String ckType;
  @ApiModelProperty(value = "全球位置编码", hidden = true)
  private String ckGlobalAddress;
  @ApiModelProperty(value = "是否启用工厂；1是", hidden = true)
  private String tenantId;
  @ApiModelProperty(value = "仓库经伟度", hidden = true)
  private String ckJwd;

  @ApiModelProperty(value = "楼/层", hidden = true)
  private String ckAddress2;

  @ApiModelProperty(value = "仓库级次ID", hidden = true)
  private String ckLevel;
  @ApiModelProperty(value = "联系人及电话", hidden = true)
  private String ckPsnPhone;
  @ApiModelProperty(value = "国家（地区）", hidden = true)
  private String country;
  @ApiModelProperty(value = "创建时间", hidden = true)
  private String creatTime;
  @ApiModelProperty(value = "是否1独立仓库、0库位仓库", hidden = true)
  private String ckIsDuli;

  @Transient
  private String label;
  @Transient
  private String title;
  @Transient
  private String value;
  @Transient
  private String cangkuLevelOrder;
  @Transient
  private String parentId;
  @Transient
  private String cangkuId;
}
