ALTER TABLE AR_INV_DTL_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
alter table AR_NON_INV_MT modify FDOC_OVERRIDE_CD varchar2(100)
/
ALTER TABLE AR_NON_INV_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE CM_AST_PMT_DTL_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE FP_ACCT_LINES_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE FP_BDGT_ADJ_DTL_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE FP_JV_ACCT_LINES_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE FP_PRCRMNT_ACCT_LINES_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE LD_EXP_TRNFR_FRM_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE LD_EXP_TRNFR_TO_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/
ALTER TABLE LD_JRNL_VCHR_DTL_T MODIFY FDOC_OVERRIDE_CD VARCHAR2(100)
/

DELETE FROM KRNS_PARM_T
WHERE NMSPC_CD = 'KFS-EC' AND
PARM_NM = 'ORGANIZATION_ROUTING_EDITABLE_IND'
/
DELETE FROM KRNS_PARM_T
WHERE NMSPC_CD = 'KFS-EC' AND
PARM_NM = 'AWARD_ROUTING_EDITABLE_IND'
/
DELETE FROM KRNS_PARM_T
WHERE NMSPC_CD = 'KFS-EC' AND
PARM_NM = 'ADMINISTRATOR_ROUTING_EDITABLE_IND'
/
