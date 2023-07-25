insert into place_pixel 
select 
    to_timestamp(replace(i.timestamp, ' UTC', ''), 'YYYY-MM-DD HH24:MI:SS.MS') as timestamp,
    split_part(i.coordinate, ',', 1)::int as x,
    split_part(i.coordinate, ',', 2)::int as y,
    hex_to_int(i.pixel_color) as color,
    i.user_id
from place_import i on conflict do nothing;
truncate table place_import;