INSERT INTO KRNS_PARM_T 
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD) 
VALUES ('KFS-CAM', 'AssetGlobal', 'NEW_ACQUISITION_CODE', SYS_GUID(), 1, 'CONFG', 
'A', 'The Asset Acquisition Type code to be used when the Asset Global (Add) document is generated by processing a transaction using the Capital Asset Builder (CAB) create asset function', 'A', 'KUALI');

INSERT INTO KRNS_PARM_T 
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD) 
VALUES ('KFS-CAM', 'AssetGlobal', 'NON_NEW_ACQUISITION_CODES', SYS_GUID(), 1, 'CONFG', 
'G;F', 'Capital Asset Acquisition Types codes representing non-expenditure additions (not purchased). These Type codes are not associated with and do not require the user to enter the following fields on the Asset Global (Add) document: Document Number, Document Type Code and Posted Date.', 'A', 'KUALI');

INSERT INTO KRNS_PARM_T 
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD) 
VALUES ('KFS-CAM', 'AssetGlobal', 'CAPITAL_OBJECT_ACQUISITION_CODES', SYS_GUID(), 1, 'CONFG', 
'F;G;S', 'All Capital Asset Acquisition Type codes not otherwise specified in the NEW_AQUISITION_CODE parameter. Allows system to identify non-capital asset acquisition type code(s) and then determines the availability of the following fields on the Asset Global (Add) document: Document Number, Document Type Code and Posted Date.', 'D', 'KUALI');

INSERT INTO KRNS_PARM_T 
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD) 
VALUES ('KFS-CAM', 'Asset', 'FABRICATED_ACQUISITION_CODE', SYS_GUID(), 1, 'CONFG', 
'C', 'The Asset Acquisition Type code that will populate the "Acquisition Type Code" field when the Asset Fabrication document is initiated.', 'A', 'KFS');

INSERT INTO KRNS_PARM_T 
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD) 
VALUES ('KFS-CAM', 'AssetGlobal', 'PRE_TAGGING_ACQUISITION_CODE', SYS_GUID(), 1, 'CONFG', 
'P', 'When tagging information exists for Pre-Asset Tagging on a Purchase Order, this is the Asset Acquisition Type code to be used on the Asset Global (Add) document generated by processing a transaction using the Capital Asset Builder (CAB) create asset function.', 'A', 'KFS');
