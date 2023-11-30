CREATE OR REPLACE PROCEDURE add_nt(
        p_id IN VARCHAR2,
        p_title IN VARCHAR2,
        p_body IN VARCHAR2
      ) AS
  BEGIN
        INSERT INTO nts (id, title, body)
        VALUES (p_id, p_title, p_body);
  END add_nt;
/
