INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('330', sys_guid(), 1, '29', null, null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('476', sys_guid(), 1, '330', '12', '2', 'org.kuali.kfs.fp.document.web.struts.DepositWizardAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('613', sys_guid(), 1, '11', '330', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('331', sys_guid(), 1, '29', null, null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('477', sys_guid(), 1, '331', '12', '2', 'org.kuali.kfs.fp.web.struts.CashDrawerCorrectionAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('614', sys_guid(), 1, '11', '331', 'Y')
/

/* clean up the route nodes */
update krew_rte_node_t set mndtry_rte_ind = 0 where mndtry_rte_ind = 1
/
/* make everything false to clear out old values */
update krim_rsp_attr_data_t set attr_val = 'false' where kim_attr_defn_id = '40'
/
/* now set the responsibilities that need to be true */
update krim_rsp_attr_data_t set attr_val = 'true' where kim_attr_defn_id = '40' and target_primary_key in ('11', '12', '13', '14', '20', '21', '24', '26', '27', '28', '29', '30', '32', '33', '34', '35', '36', '37', '38', '39', '40', '42', '44', '45', '52', '55', '57', '59', '60', '62', '64', '66', '69', '72', '84', '98', '99', '102', '103', '104', '105', '107')
/
/* finally, let's get the Research doc type right */
update krim_rsp_attr_data_t set attr_val = 'ResearchTransactionalDocument' where kim_attr_defn_id = '13' and attr_val = 'ResearchTransactionalDocument - needs created'
/

