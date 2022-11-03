package org.boozsoft.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("sys_user_ncmenu")
@Data
public class Ncmenu {
    @Id
    public Long id;
    public String userId;
    public String menusJson;
}
