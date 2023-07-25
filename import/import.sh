#!/bin/bash

PSQL="$PSQL_HOME/psql"

URL=https://placedata.reddit.com/data/canvas-history/

for i in {00..78}
do  
    echo "Step: $i"
    DOWNLOAD_FILE="2022_place_canvas_history-0000000000$i.csv.gzip"
    DOWNLOAD_URL=${URL}${DOWNLOAD_FILE}
    FILE=files/$DOWNLOAD_FILE

    if [ ! -f "$FILE" ]; then
        echo "Downloading $DOWNLOAD_URL"
        curl $DOWNLOAD_URL -f -o $FILE
    else 
        echo "${FILE} already exists"
    fi

    echo "Copy csv into import table"
    "$PSQL" -U postgres -d place \
        -c "\copy place_import(timestamp, user_id, pixel_color, coordinate) \
        from program 'gzip -dc $FILE' \
        delimiter ',' CSV HEADER;"
    if [ $? -ne 0 ]; then
        echo "CSV copied: ❌ failed"
    else
        echo "CSV copied: ✅ successfull"
    fi
    

    echo "Import data into pixel table"
    "$PSQL" -U postgres -d place \
        -c "insert into place_pixel \
            select \
                to_timestamp(replace(i.timestamp, ' UTC', ''), 'YYYY-MM-DD HH24:MI:SS.MS') as timestamp, \
                split_part(i.coordinate, ',', 1)::int as x, \
                split_part(i.coordinate, ',', 2)::int as y, \
                hex_to_int(i.pixel_color) as color, \
                i.user_id \
            from place_import i on conflict do nothing; \
            truncate table place_import;"
    if [ $? -ne 0 ]; then
        echo "Pixel data imported: ❌ failed"
    else
        echo "Pixel data imported: ✅ successfull"
    fi

done

