delete from end_pool_fnd_val_t;
delete from end_avail_csh_t;
delete from END_CRNT_TAX_LOT_BAL_T;
delete from END_HIST_CSH_T;
delete from end_hldg_hist_t;
delete from END_ME_DT_T;
delete from END_CRNT_CSH_T;
delete from END_HLDG_TAX_LOT_T;
delete from END_ENDOW_CORPUS_T;
delete from end_curr_endow_corpus_t;
delete from end_kemid_donr_t;
delete from end_kemid_corpus_val_t;
delete from end_kemid_spcl_instrc_t;
delete from end_kemid_use_crit_t;
delete from end_use_crit_t;
delete from end_kemid_pay_inc_t;
delete from end_kemid_gl_lnk_t;
delete from end_kemid_bene_org_t;
delete from END_KEMID_FND_SRC_T;
delete from end_kemid_agrmnt_t;
delete from end_kemid_fee_t;
delete from END_KEMID_CMB_GRP_T;
alter table END_POOL_FND_CTRL_T  
drop constraint END_POOL_FND_CTRL_TR5;  
delete from end_kemid_t;
delete from END_TYP_FEE_MTHD_T;
delete from end_typ_t; 
delete from END_AUTO_CSH_INVEST_MDL_T;  
delete from END_CSH_SWEEP_MDL_T;
delete from END_POOL_FND_CTRL_T;           
alter table END_POOL_FND_CTRL_T  
        add constraint END_POOL_FND_CTRL_TR5 
        foreign key (KEMID) 
        references END_KEMID_T(KEMID);
-- pool kemid
delete from END_PRPS_CD_T;
-- delete from end_cae_cd_t;
-- pool end_typ_t
delete from end_typ_restr_cd_t;
delete from END_REGIS_CD_T;
delete from end_sec_t;
delete from end_tklr_typ_cd_t;
delete from end_role_cd_t;
delete from END_RESP_ADMIN_CD_T;
delete from end_fnd_src_cd_t;
delete from end_fee_cls_cd_t;
delete from END_FEE_PMT_TYP_T;
delete from END_FEE_TRAN_TYP_T;
delete from END_FEE_ETRAN_CD_T;
delete from end_fee_mthd_t;
delete from end_fee_typ_cd_t;
delete from end_cls_cd_t;
delete from end_sec_rpt_grp_t;
delete from END_DONR_STMT_CD_T;
delete from end_donr_t;
delete from end_close_cd_t;
delete from end_agrmnt_typ_cd_t;
delete from end_agrmnt_stat_cd_t;
delete from end_tran_restr_cd_t;
delete from END_AGRMNT_SPCL_INSTRC_CD_T;
delete from end_etran_gl_lnk_t;
delete from end_etran_cd_t;