package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "档案名：日志档案", description = "日志档案")
@Table("_app_group_sys_logger")
@Data
public class SysLogger {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
            
    @ApiModelProperty(value = "类型", hidden = true)
    private String type;
                

    @ApiModelProperty(value = "标题", hidden = true)
    private String title;


    @ApiModelProperty(value = "创建人id", hidden = true)
    private String creatid;

    @ApiModelProperty(value = "创建人", hidden = true)
    private String creatby;
                

    @ApiModelProperty(value = "创建时间", hidden = true)
    private String creattime;

    @ApiModelProperty(value = "创建年", hidden = true)
    private String iyear;

    @ApiModelProperty(value = "创建月", hidden = true)
    private String imonth;


    @ApiModelProperty(value = "创建日", hidden = true)
    private String iday;
                

    @ApiModelProperty(value = "主机", hidden = true)
    private String ip;
                

    @ApiModelProperty(value = "代理", hidden = true)
    private String agent;
                

    @ApiModelProperty(value = "浏览器", hidden = true)
    private String chrome;
                

    @ApiModelProperty(value = "Get 查看,PUT ,DELETE 删除,POST 提交,IMPORT 导入,PRINT 打印，EXCEL 表格导出，UNACTIVE 停用 ", hidden = true)
    private String method;
                

    @ApiModelProperty(value = "提交数据", hidden = true)
    private String commitdata;
                

    @ApiModelProperty(value = "执行时间", hidden = true)
    private String exectime;
                

    @ApiModelProperty(value = "错误信息", hidden = true)
    private String errorinfo;
                

    @ApiModelProperty(value = "应用标识", hidden = true)
    private String appflag;

    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;
                
}
