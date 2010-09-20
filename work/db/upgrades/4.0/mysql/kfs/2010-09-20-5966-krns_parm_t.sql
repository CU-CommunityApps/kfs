-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/20/10 11:43 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-20-5966-1-KRNS_PARM_T.xml::5966-1-1::Winston::(MD5Sum: d3337d58efbd185f7bc2856d254c89b)
-- New Parameter: Fix CAB Hardcoding of Asset Condition on AA document
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-CAM', 'Asset', 'NEW_ASSET_CONDITION_CODE', uuid(), 'The Asset Condition code to be used when the Asset Global Add document is generated by processing a transaction using the Capital Asset Builder CAB create asset function', 1, 'E', 'CONFG');


-- Release Database Lock

-- Release Database Lock

