alter table report_data
    add flag varchar;

comment on column report_data.flag is '审核状态（1.审核）';

alter table report_data
    add lock_flag varchar;

comment on column report_data.lock_flag is '锁定状态（1.锁定）';

alter table report_data
    add lock_user varchar;

comment on column report_data.lock_user is '锁定人';

alter table report_data
    add lock_date varchar;

comment on column report_data.lock_date is '锁定日期';

alter table report_data alter column edit_date type varchar using edit_date::varchar;