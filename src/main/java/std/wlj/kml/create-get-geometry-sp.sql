DROP FUNCTION FN_GetGeometry(INTEGER);

CREATE OR REPLACE FUNCTION FN_GetGeometry(in_boundary_id INTEGER) RETURNS GEOMETRY AS
$BODY$
DECLARE
  tempGeom GEOMETRY;

BEGIN
    SELECT CASE WHEN ST_GeometryType(rb.boundary_datax) = 'ST_MultiLineString' THEN ST_MakePolygon(ST_LineMerge(rb.boundary_datax))
                WHEN ST_GeometryType(rb.boundary_datax) = 'ST_LineString'      THEN ST_MakePolygon(rb.boundary_datax)
                ELSE boundary_datax
           END
      INTO tempGeom
      FROM rep_boundary AS rb
     WHERE rb.boundary_id = in_boundary_id;

    RETURN tempGeom;
END;
$BODY$
LANGUAGE plpgsql;