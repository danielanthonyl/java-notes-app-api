CREATE OR REPLACE PROCEDURE ptch_nts (
    p_id VARCHAR2,
    p_title VARCHAR2 DEFAULT NULL,
    p_body VARCHAR2 DEFAULT NULL
  ) AS
  BEGIN
    UPDATE nts
    SET
      title = COALESCE(p_title, title),
      body = COALESCE(p_body, body)
    WHERE id = p_id;
  END ptch_nts;
/
