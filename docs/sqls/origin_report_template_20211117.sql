create table _app_origin_report_template
(
    id               varchar not null,
    report_name      varchar,
    template_name    varchar,
    acc_standard     varchar,
    title_name       varchar,
    flag             varchar,
    menu1            varchar,
    menu2            varchar,
    menu3            varchar,
    menu4            varchar,
    menu5            varchar,
    beiyong1         varchar,
    beiyong2         varchar,
    beiyong3         varchar,
    beiyong4         varchar,
    beiyong5         varchar,
    kemu_template_id varchar,
    sys_flag         varchar,
    menu6            varchar,
    num              integer,
    origin_id        varchar
);

comment on table _app_origin_report_template is '财务报表模板';

comment on column _app_origin_report_template.id is '主键';

comment on column _app_origin_report_template.report_name is '报表名称（资产负债表、利润表、现金流量表）';

comment on column _app_origin_report_template.template_name is '模板名称';

comment on column _app_origin_report_template.acc_standard is '会计准则';

comment on column _app_origin_report_template.title_name is '标题名称';

comment on column _app_origin_report_template.flag is '是否停用(1.启用;0停用)';

comment on column _app_origin_report_template.menu1 is '自定义菜单1';

comment on column _app_origin_report_template.menu2 is '自定义菜单2';

comment on column _app_origin_report_template.menu3 is '自定义菜单3';

comment on column _app_origin_report_template.menu4 is '自定义菜单4';

comment on column _app_origin_report_template.menu5 is '自定义菜单5';

comment on column _app_origin_report_template.kemu_template_id is '科目模板id';

comment on column _app_origin_report_template.sys_flag is '是否系统模板（1.是）';

comment on column _app_origin_report_template.menu6 is '自定义菜单6（报表编码）';

comment on column _app_origin_report_template.num is '排序号';

alter table _app_origin_report_template
    owner to postgres;