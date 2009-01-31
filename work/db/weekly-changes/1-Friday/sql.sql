UPDATE KREW_DOC_TYP_T set POST_PRCSR='org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor' where DOC_TYP_NM='RoutingRuleDocument' and CUR_IND=1
/
ALTER TABLE KREW_RULE_T MODIFY FRM_DT DATE NULL
/
ALTER TABLE KREW_RULE_T MODIFY TO_DT DATE NULL
/
UPDATE KREW_DLGN_RSP_T RSP1 SET RSP1.RULE_RSP_ID=(SELECT RSP2.RSP_ID FROM KREW_RULE_RSP_T RSP2 WHERE RSP1.RULE_RSP_ID=RSP2.RULE_RSP_ID)
/
ALTER TABLE KREW_DLGN_RSP_T RENAME COLUMN RULE_RSP_ID TO RSP_ID
/
DELETE FROM KREW_DOC_TYP_PLCY_RELN_T where DOC_PLCY_NM='PRE_APPROVE'
/

UPDATE KRIM_ATTR_DEFN_T
    SET CMPNT_NM = 'org.kuali.kfs.module.cg.identity.CgKimAttributes'
    WHERE NMSPC_CD = 'KFS-CG'
/
UPDATE KRIM_ATTR_DEFN_T
    SET CMPNT_NM = 'org.kuali.kfs.module.purap.identity.PurapKimAttributes'
    WHERE NMSPC_CD = 'KFS-PURAP'
/

insert into krns_parm_t 
(SELECT 'KFS-PURAP', 'Requisition',
'AUTOMATIC_PURCHASE_ORDER_DEFAULT_LIMIT_AMOUNT', sys_guid(),1,
'CONFG', '1000',
'If the automatic Purchase Order limit amount cannot be determined based on the vendor contract or the organization on the Requisition, use this default limit amount.',
'A'
FROM dual)
/
UPDATE krim_typ_t
    SET nm = 'Financial System User'
      , SRVC_NM = 'financialSystemUserRoleTypeService'
    WHERE KIM_TYP_ID = '36'
/

ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T ADD (OBJ_ID VARCHAR2(36))
/
UPDATE KREW_DOC_TYP_PLCY_RELN_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T ADD CONSTRAINT KREW_DOC_TYP_PLCY_RELN_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
-- delete all KIM group data
DELETE FROM KRIM_GRP_ATTR_DATA_T
/
DELETE FROM KRIM_GRP_MBR_T
/
DELETE FROM KRIM_GRP_T
/
DELETE FROM KRIM_ROLE_MBR_T
      WHERE mbr_typ_cd = 'G'
/
COMMIT
/
-- delete group parameters from the database
DELETE from KRNS_PARM_T
WHERE parm_typ_cd = 'AUTH'
  AND (PARM_NM LIKE '%GROUP' OR parm_nm = 'INITIATE_ACTION')
/
DELETE from KRNS_PARM_T
WHERE parm_typ_cd != 'AUTH'
  AND (PARM_NM LIKE '%GROUP'
    AND parm_nm NOT LIKE '%FUND_GROUP%'
)
/
COMMIT
/
DELETE from KRNS_PARM_T
    WHERE parm_typ_cd = 'HELP'
/
COMMIT
/ 
