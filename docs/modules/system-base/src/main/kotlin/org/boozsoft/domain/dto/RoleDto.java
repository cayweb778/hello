package org.boozsoft.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {
    public Long roleId;
    public
    List<Long> menuIds;
}
