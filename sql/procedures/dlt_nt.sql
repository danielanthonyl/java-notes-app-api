CREATE OR REPLACE PROCEDURE dlt_nt (
    p_id VARCHAR2 DEFAULT NULL
  ) AS
  BEGIN
    DELETE FROM nts
    WHERE (p_id IS NULL OR id = p_id);
  END dlt_nt;
/

/*
-- unit tests
DECLARE
    v_nt_id VARCHAR(36);
    v_new_nt_id VARCHAR(36);
  BEGIN
    -- Assemble
    SELECT id INTO v_nt_id
    FROM nts
    FETCH FIRST 1 ROW ONLY;

    -- Act
    dlt_nt(v_nt_id);

    -- Assert
    SELECT id INTO v_new_nt_id
    FROM nts
    WHERE id = v_nt_id;

    DBMS_OUTPUT.PUT_LINE(v_new_nt_id);
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
      DBMS_OUTPUT.PUT_LINE('No data found in the nts table for id ' || v_nt_id);
  END;
/
*/
