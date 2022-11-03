package org.boozsoft.domain.entity.group;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("_app_group_fa_acc_auth")
@ApiModel(value = "资产管理权限表", description = "资产管理权限表")
public class GroupFaAccAuth {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "操作员编码", position = 1)
    private String userNum;

    @ApiModelProperty(value = "账套管理ID", position = 2)
    private String manageUniqueCode;

    @ApiModelProperty(value = "年度", position = 2)
    private String iyear;

    @ApiModelProperty(value = "账套主管 1 ", position = 5)
    private String supervisor;
}

