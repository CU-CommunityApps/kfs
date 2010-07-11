alter table CA_OBJECT_CODE_T
add RSCH_BDGT_CTGRY_CD varchar2(3);

alter table CA_OBJECT_CODE_T
add RSCH_OBJ_CD_DESC varchar2(200);

alter table CA_OBJECT_CODE_T
add RSCH_ON_CMP_IND varchar2(1);

CREATE OR REPLACE VIEW CA_OBJECT_CODE_V
AS
(
SELECT
        C.UNIV_FISCAL_YR,
        FIN_COA_CD,
        FIN_OBJECT_CD,
        FIN_OBJ_CD_NM,
        FIN_OBJ_CD_SHRT_NM,
        FIN_OBJ_LEVEL_CD,
        RPTS_TO_FIN_COA_CD,
        RPTS_TO_FIN_OBJ_CD ,
        FIN_OBJ_TYP_CD,
        FIN_OBJ_SUB_TYP_CD,
        HIST_FIN_OBJECT_CD,
        FIN_OBJ_ACTIVE_CD,
        FOBJ_BDGT_AGGR_CD,
        FOBJ_MNXFR_ELIM_CD,
        FIN_FED_FUNDED_CD,
        NXT_YR_FIN_OBJ_CD,
        RSCH_BDGT_CTGRY_CD,
        RSCH_OBJ_CD_DESC,
        RSCH_ON_CMP_IND
        
FROM CA_OBJECT_CODE_T C, SH_UNIV_DATE_T S
WHERE C.UNIV_FISCAL_YR = S.UNIV_FISCAL_YR AND UNIV_DT = TRUNC(SYSDATE)
) 