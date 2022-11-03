-- 添加字段_app_group_sys_account
alter table _app_group_sys_account
	add statement_fangxiang varchar;

comment on column _app_group_sys_account.statement_fangxiang is '对账单余额方向（1,借；0,贷）';

alter table _app_group_sys_account
	add cash_bank_day_book_sort varchar;

comment on column _app_group_sys_account.cash_bank_day_book_sort is '现金日记账排序方式（1,日期+凭证类别字+凭证号；2,日期+制单顺序号）';

alter table _app_group_sys_account
	add bank_statement_sort varchar;

comment on column _app_group_sys_account.bank_statement_sort is '银行对账匹配优先级（1,对方单位+票号+日期+结算方式；2,对方单位+日期+票号+结算方式）';