update krim_role_t set nmspc_cd = 'KR-WKFLW' where nmspc_cd = 'KR_WKFLW'
/

update krim_role_perm_t set role_id = '66' where role_perm_id = '214'
/

ALTER TABLE KREW_RULE_EXPR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_EXPR_T ADD (VER_NBR NUMBER(8) DEFAULT 0)
/
ALTER TABLE KREW_RULE_ATTR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DOC_HDR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DOC_TYP_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_TMPL_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_RULE_RSP_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_STYLE_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_EDL_DEF_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_EDL_ASSCTN_T ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE KREW_DLGN_RSP_T ADD (OBJ_ID VARCHAR2(36))
/
UPDATE KREW_RULE_EXPR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_ATTR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DOC_HDR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DOC_TYP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_TMPL_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_TMPL_ATTR_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_RULE_RSP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_STYLE_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_EDL_DEF_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_EDL_ASSCTN_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
UPDATE KREW_DLGN_RSP_T SET OBJ_ID=SYS_GUID() WHERE OBJ_ID IS NULL
/
ALTER TABLE KREW_RULE_EXPR_T ADD CONSTRAINT KREW_RULE_EXPR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_ATTR_T ADD CONSTRAINT KREW_RULE_ATTR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_HDR_T ADD CONSTRAINT KREW_DOC_HDR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DOC_TYP_T ADD CONSTRAINT KREW_DOC_TYP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_TMPL_T ADD CONSTRAINT KREW_RULE_TMPL_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T ADD CONSTRAINT KREW_RULE_TMPL_ATTR_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_T ADD CONSTRAINT KREW_RULE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_RSP_T ADD CONSTRAINT KREW_RULE_RSP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_STYLE_T ADD CONSTRAINT KREW_STYLE_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_EDL_DEF_T ADD CONSTRAINT KREW_EDL_DEF_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_EDL_ASSCTN_T ADD CONSTRAINT KREW_EDL_ASSCTN_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_DLGN_RSP_T ADD CONSTRAINT KREW_DLGN_RSP_TC0 UNIQUE (OBJ_ID)
/
ALTER TABLE KREW_RULE_EXPR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_ATTR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DOC_HDR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_TMPL_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_TMPL_ATTR_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_RULE_RSP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_STYLE_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_EDL_DEF_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE KREW_DLGN_RSP_T MODIFY (OBJ_ID VARCHAR2(36) NOT NULL)
/

update krim_role_rsp_actn_t set ignore_prev_ind = 'N' where role_rsp_actn_id = '115'
/
