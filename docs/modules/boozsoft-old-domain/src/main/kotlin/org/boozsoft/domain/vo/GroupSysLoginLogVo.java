package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.boozsoft.domain.entity.group.GroupSysLoginLog;

@ApiModel(value = "数据表：操作日志主表", description = "操作日志主表")
@Data
public class GroupSysLoginLogVo extends GroupSysLoginLog {
    private String stockAccName;
}
