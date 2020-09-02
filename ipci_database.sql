drop table mobile_table;

create table mobile_table ( 
mobile_idx number primary key,
mobile_image varchar2 (10000) null,
mobile_str1  varchar2 (500) null,
mobile_str2  varchar2 (500) null,
mobile_str3  varchar2 (10000) null,
mobile_see  int null,
mobile_right  float null,
mobile_rate  float null) ;

select * from  mobile_table;