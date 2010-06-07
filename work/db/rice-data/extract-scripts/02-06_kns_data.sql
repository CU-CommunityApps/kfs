-- Namespaces
SELECT NMSPC_CD, NMSPC_CD AS OBJ_ID, 1 AS VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD 
    FROM KRNS_NMSPC_T
    WHERE NMSPC_CD LIKE 'KFS%'
    ORDER BY 1
/

-- Parameter Components
SELECT NMSPC_CD, PARM_DTL_TYP_CD, 'KFS'||ROWNUM AS OBJ_ID, 1 AS VER_NBR, NM, ACTV_IND 
    FROM KRNS_PARM_DTL_TYP_T
    WHERE NMSPC_CD LIKE 'KFS%'
    ORDER BY NMSPC_CD, PARM_DTL_TYP_CD
/

-- KNS Parameters
SELECT NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, 'KFS'||ROWNUM AS OBJ_ID, 1 AS VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD 
    FROM (SELECT * FROM KRNS_PARM_T
    ORDER BY NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, APPL_NMSPC_CD
    )
    WHERE NVL( APPL_NMSPC_CD, 'KUALI' ) = 'KFS' OR NMSPC_CD LIKE 'KFS%'
    ORDER BY NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, APPL_NMSPC_CD
/

-- Postal Codes

SELECT POSTAL_CD, POSTAL_CNTRY_CD, POSTAL_CNTRY_CD||'-'||POSTAL_CD AS OBJ_ID, 1 AS VER_NBR, POSTAL_STATE_CD, COUNTY_CD, POSTAL_CITY_NM, ACTV_IND 
    FROM KR_POSTAL_CODE_T
    ORDER BY 2, 1
/
