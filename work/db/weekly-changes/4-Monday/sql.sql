insert into FS_DOC_TYP_CD_T (FS_DOC_TYP_CD, OBJ_ID, VER_NBR, DOC_TYP_NM, ACTIVE_IND, TRNSCTN_SCRBR_OFFST_GEN_IND)
values ('AVAD', sys_guid(), 1, 'AuxiliaryVoucherDocument', 'Y', 'N')
/
insert into FS_DOC_TYP_CD_T (FS_DOC_TYP_CD, OBJ_ID, VER_NBR, DOC_TYP_NM, ACTIVE_IND, TRNSCTN_SCRBR_OFFST_GEN_IND)
values ('AVRC', sys_guid(), 1, 'AuxiliaryVoucherDocument', 'Y', 'N')
/
insert into FS_DOC_TYP_CD_T (FS_DOC_TYP_CD, OBJ_ID, VER_NBR, DOC_TYP_NM, ACTIVE_IND, TRNSCTN_SCRBR_OFFST_GEN_IND)
values ('AVAE', sys_guid(), 1, 'AuxiliaryVoucherDocument', 'Y', 'N')
/
insert into FS_DOC_TYP_CD_T (FS_DOC_TYP_CD, OBJ_ID, VER_NBR, DOC_TYP_NM, ACTIVE_IND, TRNSCTN_SCRBR_OFFST_GEN_IND)
values ('DVCA', sys_guid(), 1, 'DisbursementVoucherDocument', 'Y', 'N')
/
insert into FS_DOC_TYP_CD_T (FS_DOC_TYP_CD, OBJ_ID, VER_NBR, DOC_TYP_NM, ACTIVE_IND, TRNSCTN_SCRBR_OFFST_GEN_IND)
values ('DVWF', sys_guid(), 1, 'DisbursementVoucherDocument', 'Y', 'N')
/
ALTER TABLE AR_AGE_TRIAL_BAL_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE AR_DOC_RCURRNC_T MODIFY (DOC_INITR_USR_ID VARCHAR2(40));                   
ALTER TABLE AR_INV_RCURRNC_DTL_T MODIFY (DOC_INITR_USR_ID VARCHAR2(40));               
ALTER TABLE AR_SYS_INFO_T MODIFY (FDOC_INITIATOR_ID VARCHAR2(40));                     
ALTER TABLE CA_ACCOUNT_T MODIFY (ACCT_SPVSR_UNVL_ID VARCHAR2(40));                     
ALTER TABLE CA_ACCT_CHG_DOC_T MODIFY (ACCT_FSC_OFC_UID VARCHAR2(40));                  
ALTER TABLE CA_ACCT_CHG_DOC_T MODIFY (ACCT_MGR_UNVL_ID VARCHAR2(40));                  
ALTER TABLE CA_ACCT_CHG_DOC_T MODIFY (ACCT_SPVSR_UNVL_ID VARCHAR2(40));                
ALTER TABLE CA_ACCT_DELEGATE_T MODIFY (ACCT_DLGT_UNVL_ID VARCHAR2(40));                
ALTER TABLE CA_ACCT_MGR_RTE_OPTN_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));               
ALTER TABLE CA_DLGT_CHG_DOC_T MODIFY (ACCT_DLGT_UNVL_ID VARCHAR2(40));                 
ALTER TABLE CA_ORG_EXTNS_T MODIFY (MANAGER_UNVL_ID VARCHAR2(40));                      
ALTER TABLE CA_ORG_EXTNS_T MODIFY (ORG_FSCL_APRVR_UID VARCHAR2(40));                   
ALTER TABLE CA_ORG_RTNG_MDL_T MODIFY (ACCT_DLGT_UNVL_ID VARCHAR2(40));                 
ALTER TABLE CA_PRIOR_YR_ACCT_T MODIFY (ACCT_FSC_OFC_UID VARCHAR2(40));                 
ALTER TABLE CA_PRIOR_YR_ACCT_T MODIFY (ACCT_SPVSR_UNVL_ID VARCHAR2(40));               
ALTER TABLE CA_PRIOR_YR_ACCT_T MODIFY (ACCT_MGR_UNVL_ID VARCHAR2(40));                 
ALTER TABLE CA_PRIOR_YR_ORG_T MODIFY (ORG_MGR_UNVL_ID VARCHAR2(40));                   
ALTER TABLE CA_PROJECT_T MODIFY (PROJ_MGR_UNVL_ID VARCHAR2(40));                       
ALTER TABLE CB_PRTG_T MODIFY (AST_REP_UNVL_ID VARCHAR2(40));                           
ALTER TABLE CG_AWD_ACCT_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                        
ALTER TABLE CG_AWD_PRJDR_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                       
ALTER TABLE CG_PRPSL_CLOSE_T MODIFY (PERSON_USER_ID VARCHAR2(40));                     
ALTER TABLE CG_PRPSL_PRJDR_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                     
ALTER TABLE CG_SECURITY_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                        
ALTER TABLE CM_AST_TRNFR_DOC_T MODIFY (AST_REP_UNVL_ID VARCHAR2(40));                  
ALTER TABLE CM_CPTLAST_DOC_T MODIFY (AST_REP_UNVL_ID VARCHAR2(40));                    
ALTER TABLE CM_CPTLAST_DTL_T MODIFY (AST_REP_UNVL_ID VARCHAR2(40));                    
ALTER TABLE CM_CPTLAST_T MODIFY (AST_REP_UNVL_ID VARCHAR2(40));                        
ALTER TABLE CM_EQPLNRTRN_DOC_T MODIFY (AST_BORWR_UNVL_ID VARCHAR2(40));                
ALTER TABLE CM_INVN_ERR_DOC_T MODIFY (AST_UPLDR_UNVL_ID VARCHAR2(40));                 
ALTER TABLE CM_INVN_ERR_DTL_T MODIFY (AST_COR_UNVL_ID VARCHAR2(40));                   
ALTER TABLE ER_ADHOC_ORG_T MODIFY (PRSN_ADD_BY_UNVL_ID VARCHAR2(40));                  
ALTER TABLE ER_ADHOC_PRSN_T MODIFY (PRSN_UNVL_ID VARCHAR2(40));                        
ALTER TABLE ER_ADHOC_PRSN_T MODIFY (PRSN_ADD_BY_UNVL_ID VARCHAR2(40));                 
ALTER TABLE ER_BDGT_T MODIFY (BDGT_PROJ_DRCTR_UNVL_ID VARCHAR2(40));                   
ALTER TABLE ER_BDGT_USR_T MODIFY (PRSN_UNVL_ID VARCHAR2(40));                          
ALTER TABLE ER_RF_PSNL_T MODIFY (PRSN_UNVL_ID VARCHAR2(40));                           
ALTER TABLE FS_UNIVERSAL_USR_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE FS_UNIVERSAL_USR_T MODIFY (PERSON_USER_ID VARCHAR2(40));                   
ALTER TABLE GL_ENTRY_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));                          
ALTER TABLE GL_PENDING_BALANCES_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));               
ALTER TABLE KREW_ACTN_ITM_T MODIFY (PRNCPL_ID VARCHAR2(40));                           
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DLGN_PRNCPL_ID VARCHAR2(40));                      
ALTER TABLE KREW_ACTN_RQST_T MODIFY (PRNCPL_ID VARCHAR2(40));                          
ALTER TABLE KREW_ACTN_TKN_T MODIFY (PRNCPL_ID VARCHAR2(40));                           
ALTER TABLE KREW_ACTN_TKN_T MODIFY (DLGTR_PRNCPL_ID VARCHAR2(40));                     
ALTER TABLE KREW_DOC_HDR_T MODIFY (INITR_PRNCPL_ID VARCHAR2(40));                      
ALTER TABLE KREW_DOC_HDR_T MODIFY (RTE_PRNCPL_ID VARCHAR2(40));                        
ALTER TABLE KREW_DOC_NTE_T MODIFY (AUTH_PRNCPL_ID VARCHAR2(40));                       
ALTER TABLE KREW_EDL_DMP_T MODIFY (DOC_HDR_INITR_PRNCPL_ID VARCHAR2(40));              
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (PRNCPL_ID VARCHAR2(40));                        
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DLGN_PRNCPL_ID VARCHAR2(40));                   
ALTER TABLE KREW_RMV_RPLC_DOC_T MODIFY (PRNCPL_ID VARCHAR2(40));                       
ALTER TABLE KREW_RMV_RPLC_DOC_T MODIFY (RPLC_PRNCPL_ID VARCHAR2(40));                  
ALTER TABLE KREW_USR_OPTN_T MODIFY (PRNCPL_ID VARCHAR2(40));                           
ALTER TABLE KRIM_PERSON_DOCUMENT_T MODIFY (PRNCPL_NM VARCHAR2(40));                    
ALTER TABLE KRIM_PERSON_DOCUMENT_T MODIFY (PRNCPL_PSWD VARCHAR2(40));                  
ALTER TABLE KRIM_PRNCPL_T MODIFY (PRNCPL_PSWD VARCHAR2(40));                           
ALTER TABLE KRIM_PRNCPL_T MODIFY (PRNCPL_NM VARCHAR2(40));                             
ALTER TABLE KRNS_LOOKUP_RSLT_T MODIFY (PRNCPL_ID VARCHAR2(40));                        
ALTER TABLE KRNS_LOOKUP_SEL_T MODIFY (PRNCPL_ID VARCHAR2(40));                         
ALTER TABLE KRNS_NTE_T MODIFY (AUTH_PRNCPL_ID VARCHAR2(40));                           
ALTER TABLE KRNS_PESSIMISTIC_LOCK_T MODIFY (PRNCPL_ID VARCHAR2(40));                   
ALTER TABLE LD_BAL_BY_GL_KEY_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BAL_GLBL_CSF_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                    
ALTER TABLE LD_BCNSTR_HDR_T MODIFY (BDGT_LOCK_USR_ID VARCHAR2(40));                    
ALTER TABLE LD_BCNSTR_HDR_T MODIFY (BDGT_TRNLCK_USR_ID VARCHAR2(40));                  
ALTER TABLE LD_BCN_2PLG_LIST_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));                  
ALTER TABLE LD_BCN_ACCTSEL_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                     
ALTER TABLE LD_BCN_ACCTSEL_T MODIFY (FDOC_INITIATOR_ID VARCHAR2(40));                  
ALTER TABLE LD_BCN_ACCT_BAL_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                    
ALTER TABLE LD_BCN_ACCT_DUMP_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_ACCT_SUMM_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_ACTV_JOB_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_BAL_BY_ACCT_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                 
ALTER TABLE LD_BCN_BUILD_CTRL_LIST02_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));          
ALTER TABLE LD_BCN_CTRL_LIST_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_FND_LOCK_T MODIFY (APPT_FNDLCK_USR_ID VARCHAR2(40));                
ALTER TABLE LD_BCN_II_INIT_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));                    
ALTER TABLE LD_BCN_INCUMBENT_SEL_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));               
ALTER TABLE LD_BCN_LEVL_SUMM_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_MNTH_SUMM_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_OBJT_DUMP_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_OBJT_SUMM_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_OBJ_PICK_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                    
ALTER TABLE LD_BCN_PAYRT_HLDG_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                  
ALTER TABLE LD_BCN_POS_FND_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                     
ALTER TABLE LD_BCN_POS_INIT_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_POS_SEL_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                     
ALTER TABLE LD_BCN_POS_T MODIFY (POS_LOCK_USR_ID VARCHAR2(40));                        
ALTER TABLE LD_BCN_PULLUP_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                      
ALTER TABLE LD_BCN_RQST_MT MODIFY (PERSON_UNVL_ID VARCHAR2(40));                       
ALTER TABLE LD_BCN_RSN_CD_PK_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                   
ALTER TABLE LD_BCN_SAL_FND_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                     
ALTER TABLE LD_BCN_SAL_SSN_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                     
ALTER TABLE LD_BCN_SLRY_TOT_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                    
ALTER TABLE LD_BCN_SUBFUND_PICK_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                
ALTER TABLE PDP_FIL_T MODIFY (SBMTR_USR_ID VARCHAR2(40));                              
ALTER TABLE PDP_PAYEE_ACH_ACCT_T MODIFY (PERSON_UNVL_ID VARCHAR2(40));                 
ALTER TABLE PDP_PMT_GRP_HIST_T MODIFY (PMT_CHG_USR_ID VARCHAR2(40));                   
ALTER TABLE PDP_PROC_T MODIFY (PROC_USR_ID VARCHAR2(40));   
