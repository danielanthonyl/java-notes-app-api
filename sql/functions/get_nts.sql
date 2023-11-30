CREATE OR REPLACE FUNCTION get_nts(
    p_id VARCHAR2 DEFAULT NULL
    )
    RETURN nts_tab PIPELINED AS

    BEGIN
        FOR nt_rec IN(
            SELECT * FROM nts
            WHERE (p_id IS NULL OR id = p_id)
        ) LOOP
            PIPE ROW(nt_row(nt_rec.id, nt_rec.title, nt_rec.body));
        END LOOP;
      RETURN;
    END get_nts;
/
