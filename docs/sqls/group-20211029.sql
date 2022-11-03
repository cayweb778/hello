
-- 添加字段_app_group_sys_period   sys_period
ALTER TABLE _app_group_sys_period ADD  fixed_assets varchar;
COMMENT on column _app_group_sys_period.fixed_assets is'固定资产启用区间';

ALTER TABLE _app_group_sys_period ADD  receivables varchar;
COMMENT on column _app_group_sys_period.receivables is'应收款启用区间';

ALTER TABLE _app_group_sys_period ADD  payable varchar;
COMMENT on column _app_group_sys_period.payable is'应付款启用区间';

ALTER TABLE _app_group_sys_period ADD  sales varchar;
COMMENT on column _app_group_sys_period.sales is'销售启用区间';

ALTER TABLE _app_group_sys_period ADD  purchase varchar;
COMMENT on column _app_group_sys_period.purchase is'采购启用区间';

-- 添加字段 sys_period
ALTER TABLE sys_period ADD  fixed_assets varchar;
COMMENT on column sys_period.fixed_assets is'固定资产启用区间';

ALTER TABLE sys_period ADD  receivables varchar;
COMMENT on column sys_period.receivables is'应收款启用区间';

ALTER TABLE sys_period ADD  payable varchar;
COMMENT on column sys_period.payable is'应付款启用区间';

ALTER TABLE sys_period ADD  sales varchar;
COMMENT on column sys_period.sales is'销售启用区间';

ALTER TABLE sys_period ADD  purchase varchar;
COMMENT on column sys_period.purchase is'采购启用区间';


--  2021-11-02  添加字段 sys_acc_auth
ALTER TABLE sys_acc_auth ADD  cus_class varchar;
COMMENT on column sys_acc_auth.cus_class is'客户分类是否全部';

ALTER TABLE sys_acc_auth ADD  cus varchar;
COMMENT on column sys_acc_auth.cus is'客户档案是否全部';

ALTER TABLE sys_acc_auth ADD  sup_class varchar;
COMMENT on column sys_acc_auth.sup_class is'供应商分类是否全部';

ALTER TABLE sys_acc_auth ADD  sup varchar;
COMMENT on column sys_acc_auth.sup is'供应商档案是否全部';

ALTER TABLE sys_acc_auth ADD  project_class varchar;
COMMENT on column sys_acc_auth.project_class is'项目分类是否全部';

ALTER TABLE sys_acc_auth ADD  project_type varchar;
COMMENT on column sys_acc_auth.project_type is'项目大类是否全部';

ALTER TABLE sys_acc_auth ADD  project varchar;
COMMENT on column sys_acc_auth.project is'项目是否全部';

ALTER TABLE sys_acc_auth ADD  dept varchar;
COMMENT on column sys_acc_auth.dept is'部门是否全部';

ALTER TABLE sys_acc_auth ADD  define_id varchar;
COMMENT on column sys_acc_auth.define_id is'自定义档案是否全部1是';

--  2021-11-02  添加字段 sys_acc_code_auth
ALTER TABLE sys_acc_code_auth ADD  itype varchar;
COMMENT on column sys_acc_code_auth.itype is'档案类型';
