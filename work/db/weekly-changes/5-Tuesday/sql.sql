ALTER TABLE KRIM_DLGN_MBR_T
	ADD ROLE_MBR_ID VARCHAR2(40)
/
ALTER TABLE KRIM_DLGN_MBR_T
	ADD CONSTRAINT KRIM_DLGN_MBR_TR1
	FOREIGN KEY (ROLE_MBR_ID)
	REFERENCES KRIM_ROLE_MBR_T(ROLE_MBR_ID)
/

ALTER TABLE KRIM_DLGN_MBR_T
	ADD CONSTRAINT KRIM_DLGN_MBR_TR2
	FOREIGN KEY (DLGN_ID)
	REFERENCES KRIM_DLGN_T(DLGN_ID)
/
