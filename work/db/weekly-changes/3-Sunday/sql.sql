ALTER TABLE KRIM_ROLE_RSP_ACTN_T
    ADD ( IGNORE_PREV_IND VARCHAR2(1) DEFAULT 'Y' )
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('551', sys_guid(), 1, '60', '101', 'Y')
/

update krim_rsp_attr_data_t set attr_val = 'PurchasingTransactionalDocument' where attr_val = 'PurchasingTransactionalDocument '
/

alter table KRIM_PND_ROLE_MBR_MT rename column ACTV_FROM_DT to ACTV_FRM_DT
/
CREATE TABLE KRIM_PND_ROLE_RSP_ACTN_MT ( 
    ROLE_RSP_ACTN_ID	VARCHAR2(40) NOT NULL,
    FDOC_NBR          VARCHAR2(14)  NOT NULL,
    OBJ_ID          	VARCHAR2(36) NOT NULL,
    VER_NBR         	NUMBER(8,0) DEFAULT 1 NOT NULL,
    ACTN_TYP_CD     	VARCHAR2(40) NULL,
    PRIORITY_NBR    	NUMBER(3,0) NULL,
    ACTN_PLCY_CD    	VARCHAR2(40) NULL,
    ROLE_MBR_ID     	VARCHAR2(40) NULL,
    ROLE_RSP_ID     	VARCHAR2(40) NULL,
    EDIT_FLAG    	VARCHAR2(1) DEFAULT 'N' NULL,
    CONSTRAINT KRIM_PND_ROLE_RSP_ACTN_MTP1 PRIMARY KEY(ROLE_RSP_ACTN_ID,FDOC_NBR)
)
/

update krim_perm_attr_data_t set ATTR_VAL = 'PayeeACHAccount' where ATTR_VAL = 'PayeeAchAccount'
/
update krim_perm_attr_data_t set ATTR_VAL = 'ACHBank' where ATTR_VAL = 'AchBank'
/

update krim_perm_t set nm = 'Edit Inactive Account' where perm_id = '58'
/
update krim_role_perm_t set role_id = '44' where perm_id = '58'
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, VER_NBR, RSP_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('106', sys_guid(), 1, '1', null, null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('388', sys_guid(), 1, '106', '7', '16', 'Management')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('389', sys_guid(), 1, '106', '7', '13', 'CustomerMaintenanceDocument')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('390', sys_guid(), 1, '106', '7', '41', 'false')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('391', sys_guid(), 1, '106', '7', '40', 'true')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
    VALUES('1106', sys_guid(), 1, '3', '106', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_ACTN_T(ROLE_RSP_ACTN_ID, OBJ_ID, VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID)
    VALUES('131', sys_guid(), 1, 'A', null, 'F', '*', '1106')
/

