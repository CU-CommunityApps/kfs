INSERT INTO KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
VALUES ('KFS-COA', 'Account', 'RESEARCH_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION', SYS_GUID(), 1, 'CONFG',
'save', 'This parameter defines the workflow action that will be taken on an account document automatically generated by an integrated research admin system. The possible parameter values are save, submit, and blanketApprove.', 'A', 'KFS');
