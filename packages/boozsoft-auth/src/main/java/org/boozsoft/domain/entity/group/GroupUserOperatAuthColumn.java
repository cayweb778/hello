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
@Table("_group_user_operat_auth_column")
@ApiModel(value = "操作员账套权限表", description = "操作员账套权限表")
public class GroupUserOperatAuthColumn {
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;
    @ApiModelProperty(value = "平台ID")
    private String  platformMark;
    @ApiModelProperty(value = "标题")
    private String  columnTitle;
    @ApiModelProperty(value = "属性名称")
    private String  columnProperty;
    private String  beiyong1;
    private String  beiyong2;
    private String  beiyong3;
    private String  beiyong4;
    private String  beiyong5;
}
