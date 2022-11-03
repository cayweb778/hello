create table _app_origin_report_template_column
(
    id               varchar not null,
    template_id      varchar not null,
    xuhao            bigint  not null,
    column_show_name varchar,
    jici             varchar,
    hangci           varchar,
    column_type      varchar,
    formula_method   varchar,
    value_method     varchar,
    formula_count    varchar,
    beiyong1         varchar,
    beiyong2         varchar,
    beiyong3         varchar,
    beiyong4         varchar,
    beiyong5         varchar,
    sys_flag         varchar,
    origin_id        varchar
);

comment on table _app_origin_report_template_column is '财务报表模板栏目';

comment on column _app_origin_report_template_column.id is '主键';

comment on column _app_origin_report_template_column.template_id is '模板id';

comment on column _app_origin_report_template_column.xuhao is '序号';

comment on column _app_origin_report_template_column.column_show_name is '栏目显示名称';

comment on column _app_origin_report_template_column.jici is '显示级次(1~4)';

comment on column _app_origin_report_template_column.hangci is '行次（一个模板内不允许重复）';

comment on column _app_origin_report_template_column.column_type is '所属类型(仅资产负债表使用；资产、负债、权益)';

comment on column _app_origin_report_template_column.formula_method is '计算方式（1.行次；2.科目）';

comment on column _app_origin_report_template_column.value_method is '取值方式（余额；借方余额；贷方余额）';

comment on column _app_origin_report_template_column.formula_count is '计算公式';

comment on column _app_origin_report_template_column.sys_flag is '是否系统模板（1.是）';

alter table _app_origin_report_template_column
    owner to postgres;