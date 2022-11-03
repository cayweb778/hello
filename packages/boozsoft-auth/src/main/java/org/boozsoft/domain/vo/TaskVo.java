package org.boozsoft.domain.vo;


import lombok.Data;

@Data
public class TaskVo {
	private String id;
	private String caozuoUnique;
	private String time;
	private String functionModule;
	private String recordNum;
	private String caozuoName;
	private String username;
	private String state;
	private String iyear;
	private String imonth;
	private String method;
	private String companyCode;
	private String companyName;
	private String tenantId;
	private String accGroup;
}
