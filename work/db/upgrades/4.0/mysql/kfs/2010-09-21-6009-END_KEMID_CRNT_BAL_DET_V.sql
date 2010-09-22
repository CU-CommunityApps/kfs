CREATE OR REPLACE VIEW END_KEMID_CRNT_BAL_DET_V AS
SELECT
      B.kemid
    , B.hldg_ip_ind
    , CASE WHEN R.SEC_RPT_GRP != 'CSHEQ' THEN
        SUM(b.HLDG_MVAL)
      ELSE
        CASE WHEN b.hldg_ip_ind = 'I' THEN
            A.CRNT_INC_CSH + SUM(B.HLDG_MVAL)
        ELSE
            a.CRNT_PRIN_CSH + SUM(B.HLDG_MVAL)
        END
      END AS VAL_AT_MARKET
    , R.SEC_RPT_GRP AS SEC_RPT_GRP
    , SUM(b.HLDG_ANNL_INC_EST) AS ANN_INC_EST
    , SUM(b.HLDG_FY_REM_EST_INC) AS FY_REM_EST_INC
    , SUM(b.HLDG_NEXT_FY_EST_INC) AS NEXT_FY_EST_INC
FROM
      END_CRNT_TAX_LOT_BAL_T B
    , END_SEC_T S
    , END_CLS_CD_T CL
    , END_SEC_RPT_GRP_T R
    , END_CRNT_CSH_T A
WHERE
        S.SEC_ID = B.SEC_ID
    AND S.SEC_CLS_CD = CL.SEC_CLS_CD
    AND CL.SEC_RPT_GRP = R.SEC_RPT_GRP
    AND A.kemid = B.kemid
GROUP BY
      B.kemid
    , R.SEC_RPT_GRP
    , B.hldg_ip_ind
    , A.CRNT_INC_CSH
    , a.CRNT_PRIN_CSH;

CREATE OR REPLACE VIEW END_KEMID_CRNT_BAL_V AS
SELECT
    A.kemid
    , a.CRNT_INC_CSH + SUM( CASE WHEN E.hldg_IP_IND = 'I' THEN e.hldg_mval ELSE 0.00 END ) AS INC_AT_MARKET
    , a.CRNT_PRIN_CSH + SUM( CASE WHEN E.hldg_IP_IND = 'P' THEN e.hldg_mval ELSE 0.00 END ) AS PRIN_AT_MARKET
    , a.CRNT_PRIN_CSH + a.CRNT_INC_CSH + SUM( e.hldg_mval ) AS TOTAL_MARKET_VAL
    , SUM(HLDG_ANNL_INC_EST) AS ANN_EST_INC
    , SUM(HLDG_FY_REM_EST_INC) AS FY_REM_EST_INC
    , SUM(HLDG_NEXT_FY_EST_INC) AS NEXT_FY_EST_INC
FROM
      END_CRNT_CSH_T A
    , END_CRNT_TAX_LOT_BAL_T E
WHERE a.kemid = e.kemid
GROUP BY A.kemid, A.CRNT_PRIN_CSH, a.CRNT_INC_CSH;

CREATE OR REPLACE VIEW END_KEMID_HIST_BAL_DET_V
AS
SELECT
    B.kemid
    , B.hldg_ip_ind
    ,
    CASE
        WHEN B.hldg_ip_ind = 'I'
            THEN A.HIST_INC_CSH + SUM(B.HLDG_MVAL)
            ELSE A.HIST_PRIN_CSH + SUM(B.HLDG_MVAL)
    END AS VAL_AT_MARKET
    , R.SEC_RPT_GRP AS SEC_RPT_GRP
    , SUM(B.HLDG_ANNL_INC_EST) AS ANN_INC_EST
    , SUM(B.HLDG_FY_REM_EST_INC) AS FY_REM_EST_INC
    , SUM(B.HLDG_NEXT_FY_EST_INC) AS NEXT_FY_EST_INC
    , B.ME_DT_ID
FROM
    END_HIST_CSH_T A
    , END_HLDG_HIST_T B
    , END_SEC_T S
    , END_CLS_CD_T CL
    , END_SEC_RPT_GRP_T R
WHERE
    S.SEC_ID = B.SEC_ID
    AND S.SEC_CLS_CD = CL.SEC_CLS_CD
    AND CL.SEC_RPT_GRP = R.SEC_RPT_GRP
    AND a.KEMID = b.KEMID
    AND a.ME_DT_ID = b.ME_DT_ID
GROUP BY
    B.kemid
    , R.SEC_RPT_GRP
    , B.hldg_ip_ind
    , B.ME_DT_ID
    , a.HIST_INC_CSH
    , a.HIST_PRIN_CSH;

CREATE OR REPLACE VIEW END_KEMID_HIST_BAL_V
AS
SELECT
    A.kemid, a.ME_DT_ID
    , a.HIST_INC_CSH + SUM( CASE WHEN E.hldg_IP_IND = 'I' THEN e.hldg_mval ELSE 0.00 END ) AS INC_AT_MARKET
    , a.HIST_PRIN_CSH + SUM( CASE WHEN E.hldg_IP_IND = 'P' THEN e.hldg_mval ELSE 0.00 END ) AS PRIN_AT_MARKET
    , a.HIST_PRIN_CSH + a.HIST_INC_CSH + SUM( e.hldg_mval ) AS TOTAL_MARKET_VAL
    , SUM(HLDG_ANNL_INC_EST) AS ANN_EST_INC
    , SUM(HLDG_FY_REM_EST_INC) AS FY_REM_EST_INC
    , SUM(HLDG_NEXT_FY_EST_INC) AS NEXT_FY_EST_INC
FROM
      END_HIST_CSH_T A
    , END_HLDG_HIST_T E
WHERE a.kemid = e.kemid
  AND a.ME_DT_ID = e.ME_DT_ID
GROUP BY A.kemid, a.ME_DT_ID, A.HIST_PRIN_CSH, a.HIST_INC_CSH;

