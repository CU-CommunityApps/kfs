update krim_role_mbr_t set mbr_id = '4034202067' where mbr_id = '4034202607' and mbr_typ_cd = 'P'
/

-- make org hierarchy nodes run in parallel
UPDATE KREW_RTE_NODE_T
    SET ACTVN_TYP = 'P'
    WHERE nm LIKE '%OrganizationHierarchy'
/
-- fix document type names in the role member table
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'KBD'
    WHERE attr_val = 'BudgetDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'ACCT'
    WHERE attr_val = 'AccountMaintenanceDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'DV'
    WHERE attr_val = 'DisbursementVoucherDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'TF'
    WHERE attr_val = 'TransferOfFundsDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'BA'
    WHERE attr_val = 'BudgetAdjustmentDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'REQS'
    WHERE attr_val = 'RequisitionDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
update krns_parm_t t
   set t.txt = 'ACLO;TF;YETF;AA;SACH'
 where t.nmspc_cd = 'KFS-CAB'
   and t.parm_dtl_typ_cd = 'Batch'
   and t.parm_nm = 'DOCUMENT_TYPES'; 
/
delete from krns_parm_t
where parm_nm in ('INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE','VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE')
/