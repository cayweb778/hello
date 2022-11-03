create table _app_origin_report_template_column_formula
(
    id          varchar not null,
    template_id varchar not null,
    column_id   varchar not null,
    ccode       varchar,
    ccode_name  varchar,
    fuhao       varchar,
    fangxiang   varchar,
    rules       varchar,
    beiyong1    varchar,
    beiyong2    varchar,
    beiyong3    varchar,
    beiyong4    varchar,
    beiyong5    varchar,
    origin_id   varchar
);

comment on table _app_origin_report_template_column_formula is '财务报表模板栏目公式';

comment on column _app_origin_report_template_column_formula.id is '主键';

comment on column _app_origin_report_template_column_formula.template_id is '模板id';

comment on column _app_origin_report_template_column_formula.column_id is '栏目id';

comment on column _app_origin_report_template_column_formula.ccode is '科目编码';

comment on column _app_origin_report_template_column_formula.ccode_name is '科目名称';

comment on column _app_origin_report_template_column_formula.fuhao is '符号';

comment on column _app_origin_report_template_column_formula.fangxiang is '方向';

comment on column _app_origin_report_template_column_formula.rules is '取数规则（余额；借方发生额；贷方发生额）';

alter table _app_origin_report_template_column_formula
    owner to postgres;