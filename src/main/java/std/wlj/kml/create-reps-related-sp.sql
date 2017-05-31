CREATE OR REPLACE FUNCTION FN_RepsRelated(in_rep_one INTEGER, in_rep_two INTEGER) RETURNS BOOLEAN AS
$BODY$
DECLARE
  temp_rep_id   INTEGER;
  parent_rep_id INTEGER;
  is_related    BOOLEAN;

BEGIN
    is_related := FALSE;

    -- Simple check in case the two IDs are the same
    IF in_rep_one = in_rep_two THEN
       is_related := TRUE;

    ELSE
        -- See if the first rep is a child of the second rep
        temp_rep_id := in_rep_one;
        LOOP
            SELECT parent_id INTO parent_rep_id FROM place_rep WHERE rep_id = temp_rep_id;
            IF parent_rep_id IS NULL  OR  parent_rep_id = 0 THEN
                EXIT;
            ELSIF in_rep_two = parent_rep_id THEN
                is_related := TRUE;
                EXIT;                
            END IF;
            temp_rep_id = parent_id;
        END LOOP;

        -- See if the second rep is a child of the first rep
        temp_rep_id := in_rep_two;
        LOOP
            SELECT parent_id INTO parent_rep_id FROM place_rep WHERE rep_id = temp_rep_id;
            IF parent_rep_id IS NULL  OR  parent_rep_id = 0 THEN
                EXIT;
            ELSIF in_rep_one = parent_rep_id THEN
                is_related := TRUE;
                EXIT;                
            END IF;
            temp_rep_id = parent_id;
        END LOOP;
    END IF;

    RETURN is_related;  
END;
$BODY$
LANGUAGE plpgsql;