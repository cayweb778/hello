package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.boozsoft.domain.entity.FaAssetType;

@Data
@ApiModel(value="资产类别",description="资产类别")
public class FaAssetTypeVo extends FaAssetType {

	private String aname;

	private String uname;

	private String mname;

}
