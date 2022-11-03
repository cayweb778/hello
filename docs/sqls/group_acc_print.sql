create table _app_group_acc_print
(
    id              varchar not null,
    acc_id          varchar,
    paper_fangxiang varchar,
    menu_name       varchar,
    menu_ch_name    varchar,
    paper_left      varchar,
    paper_right     varchar,
    paper_top       varchar,
    paper_bottom    varchar,
    custom1         varchar,
    custom2         varchar,
    custom3         varchar,
    custom4         varchar,
    custom5         varchar,
    custom6         varchar,
    custom7         varchar,
    custom8         varchar,
    custom9         varchar,
    custom10        varchar
);

comment
on table _app_group_acc_print is '账表打印参数设置表';

comment
on column _app_group_acc_print.id is '主键';

comment
on column _app_group_acc_print.acc_id is '账套id（一个账套保存一条记录）';

comment
on column _app_group_acc_print.paper_fangxiang is '纸张方向（1,横向；2纵向）';

comment
on column _app_group_acc_print.menu_name is '菜单英文名称';

comment
on column _app_group_acc_print.menu_ch_name is '菜单中文名称';

comment
on column _app_group_acc_print.paper_left is '左边距';

comment
on column _app_group_acc_print.paper_right is '右边距';

comment
on column _app_group_acc_print.paper_top is '上边距';

comment
on column _app_group_acc_print.paper_bottom is '下边距';

