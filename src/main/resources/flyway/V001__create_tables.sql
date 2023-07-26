
create table place_import (
    timestamp varchar,
    user_id varchar,
    pixel_color varchar,
    coordinate varchar
);

create table place_pixel (
    timestamp timestamp,
    x int,
    y int,
    color int,
    user_id varchar
) partition by range (timestamp);
create index place_pixel_coordinate_idx on place_pixel (x, y, timestamp);

/* TODO: create partitions automaticly when importing ? */
create table place_pixel_22_01 partition of place_pixel 
    for values from ('2022-04-01 00:00:00.000') to ('2022-04-02 00:00:00.000');
create table place_pixel_22_02 partition of place_pixel 
    for values from ('2022-04-02 00:00:00.000') to ('2022-04-03 00:00:00.000');
create table place_pixel_22_03 partition of place_pixel 
    for values from ('2022-04-03 00:00:00.000') to ('2022-04-04 00:00:00.000');
create table place_pixel_22_04 partition of place_pixel 
    for values from ('2022-04-04 00:00:00.000') to ('2022-04-05 00:00:00.000');
create table place_pixel_22_05 partition of place_pixel 
    for values from ('2022-04-05 00:00:00.000') to ('2022-04-06 00:00:00.000');
create table place_pixel_22_06 partition of place_pixel 
    for values from ('2022-04-06 00:00:00.000') to ('2022-04-07 00:00:00.000');


create or replace function hex_to_int(hexval varchar) returns integer as $$
declare
    result  int;
begin
    execute 'SELECT x' || quote_literal(replace(hexval, '#', '')) || '::int' into result;
    return result;
end;
$$ language plpgsql IMMUTABLE STRICT;

