package org.boozsoft.domain.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DeptModel {
    private String id;
    private String deptName;
    private String orderNo;
    private String createTime;
    private String remark;
    private String status;
    private String parentDept;
    private List children;
}
