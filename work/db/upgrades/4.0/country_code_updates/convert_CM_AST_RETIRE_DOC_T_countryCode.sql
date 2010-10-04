
-- This script will migrate a column in a table from the former Rice country 
-- codes which were based on FIPS 10-4 (with some minor differences) to the new
-- Rice country codes which are based on ISO 3166-1.  This script may not 
-- properly execute on columns with a primary key or unique constraint as some
-- of the former codes have merged (i.e. - all US Minor Outlying Islands have 
-- been unified under a single code, UM).  This script also does not take any 
-- action on codes that are not part of the list of former Rice country codes.
--
-- Table Name: 	CM_AST_RETIRE_DOC_T
-- Column Name: AST_RETIR_CNTRY_CD
--
-- In order to avoid collisions between the former codes and the new codes, the 
-- codes are first changed to an interim, unique code.  Once that change is 
-- complete, they are changed to the new, correct code.
--
-- Change to temporary code
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='A0' where AST_RETIR_CNTRY_CD='AA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='A1' where AST_RETIR_CNTRY_CD='AC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='A3' where AST_RETIR_CNTRY_CD='AG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='A4' where AST_RETIR_CNTRY_CD='AJ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='A7' where AST_RETIR_CNTRY_CD='AN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='A9' where AST_RETIR_CNTRY_CD='AQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B1' where AST_RETIR_CNTRY_CD='AS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B2' where AST_RETIR_CNTRY_CD='AT';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B3' where AST_RETIR_CNTRY_CD='AU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B4' where AST_RETIR_CNTRY_CD='AV';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B5' where AST_RETIR_CNTRY_CD='AY';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B6' where AST_RETIR_CNTRY_CD='BA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B8' where AST_RETIR_CNTRY_CD='BC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='B9' where AST_RETIR_CNTRY_CD='BD';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C1' where AST_RETIR_CNTRY_CD='BF';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C2' where AST_RETIR_CNTRY_CD='BG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C3' where AST_RETIR_CNTRY_CD='BH';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C4' where AST_RETIR_CNTRY_CD='BK';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C5' where AST_RETIR_CNTRY_CD='BL';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C6' where AST_RETIR_CNTRY_CD='BM';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C7' where AST_RETIR_CNTRY_CD='BN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C8' where AST_RETIR_CNTRY_CD='BO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='C9' where AST_RETIR_CNTRY_CD='BP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='D0' where AST_RETIR_CNTRY_CD='BQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='D2' where AST_RETIR_CNTRY_CD='BS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='D4' where AST_RETIR_CNTRY_CD='BU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='D6' where AST_RETIR_CNTRY_CD='BX';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='D7' where AST_RETIR_CNTRY_CD='BY';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='D9' where AST_RETIR_CNTRY_CD='CB';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E0' where AST_RETIR_CNTRY_CD='CD';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E1' where AST_RETIR_CNTRY_CD='CE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E2' where AST_RETIR_CNTRY_CD='CF';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E3' where AST_RETIR_CNTRY_CD='CG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E4' where AST_RETIR_CNTRY_CD='CH';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E5' where AST_RETIR_CNTRY_CD='CI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E6' where AST_RETIR_CNTRY_CD='CJ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E7' where AST_RETIR_CNTRY_CD='CK';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='E9' where AST_RETIR_CNTRY_CD='CN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='F1' where AST_RETIR_CNTRY_CD='CQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='F2' where AST_RETIR_CNTRY_CD='CR';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='F3' where AST_RETIR_CNTRY_CD='CS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='F4' where AST_RETIR_CNTRY_CD='CT';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='F7' where AST_RETIR_CNTRY_CD='CW';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='F9' where AST_RETIR_CNTRY_CD='CZ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='G0' where AST_RETIR_CNTRY_CD='DA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='G2' where AST_RETIR_CNTRY_CD='DO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='G3' where AST_RETIR_CNTRY_CD='DR';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='G6' where AST_RETIR_CNTRY_CD='EI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='G7' where AST_RETIR_CNTRY_CD='EK';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='G8' where AST_RETIR_CNTRY_CD='EN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='H0' where AST_RETIR_CNTRY_CD='ES';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='H2' where AST_RETIR_CNTRY_CD='EU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='H3' where AST_RETIR_CNTRY_CD='EZ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='H4' where AST_RETIR_CNTRY_CD='FA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='H5' where AST_RETIR_CNTRY_CD='FG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I0' where AST_RETIR_CNTRY_CD='FP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I1' where AST_RETIR_CNTRY_CD='FQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I3' where AST_RETIR_CNTRY_CD='FS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I4' where AST_RETIR_CNTRY_CD='GA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I5' where AST_RETIR_CNTRY_CD='GB';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I6' where AST_RETIR_CNTRY_CD='GE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='I7' where AST_RETIR_CNTRY_CD='GG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='J0' where AST_RETIR_CNTRY_CD='GJ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='J1' where AST_RETIR_CNTRY_CD='GK';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='J3' where AST_RETIR_CNTRY_CD='GM';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='J4' where AST_RETIR_CNTRY_CD='GO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='J6' where AST_RETIR_CNTRY_CD='GQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='J9' where AST_RETIR_CNTRY_CD='GV';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='K1' where AST_RETIR_CNTRY_CD='GZ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='K2' where AST_RETIR_CNTRY_CD='HA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='K5' where AST_RETIR_CNTRY_CD='HO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='K6' where AST_RETIR_CNTRY_CD='HQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='K9' where AST_RETIR_CNTRY_CD='IC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='L4' where AST_RETIR_CNTRY_CD='IP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='L6' where AST_RETIR_CNTRY_CD='IS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='L8' where AST_RETIR_CNTRY_CD='IV';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='L9' where AST_RETIR_CNTRY_CD='IY';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='M0' where AST_RETIR_CNTRY_CD='IZ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='M1' where AST_RETIR_CNTRY_CD='JA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='M4' where AST_RETIR_CNTRY_CD='JN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='M6' where AST_RETIR_CNTRY_CD='JQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='M7' where AST_RETIR_CNTRY_CD='JU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N0' where AST_RETIR_CNTRY_CD='KN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N1' where AST_RETIR_CNTRY_CD='KQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N2' where AST_RETIR_CNTRY_CD='KR';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N3' where AST_RETIR_CNTRY_CD='KS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N4' where AST_RETIR_CNTRY_CD='KT';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N5' where AST_RETIR_CNTRY_CD='KU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N8' where AST_RETIR_CNTRY_CD='LE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='N9' where AST_RETIR_CNTRY_CD='LG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O0' where AST_RETIR_CNTRY_CD='LH';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O1' where AST_RETIR_CNTRY_CD='LI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O2' where AST_RETIR_CNTRY_CD='LO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O3' where AST_RETIR_CNTRY_CD='LQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O4' where AST_RETIR_CNTRY_CD='LS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O5' where AST_RETIR_CNTRY_CD='LT';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O8' where AST_RETIR_CNTRY_CD='MA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='O9' where AST_RETIR_CNTRY_CD='MB';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P0' where AST_RETIR_CNTRY_CD='MC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P2' where AST_RETIR_CNTRY_CD='MF';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P3' where AST_RETIR_CNTRY_CD='MG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P4' where AST_RETIR_CNTRY_CD='MH';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P5' where AST_RETIR_CNTRY_CD='MI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P8' where AST_RETIR_CNTRY_CD='MN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='P9' where AST_RETIR_CNTRY_CD='MO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Q0' where AST_RETIR_CNTRY_CD='MP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Q1' where AST_RETIR_CNTRY_CD='MQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Q4' where AST_RETIR_CNTRY_CD='MU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Q6' where AST_RETIR_CNTRY_CD='MW';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='R0' where AST_RETIR_CNTRY_CD='NA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='R2' where AST_RETIR_CNTRY_CD='NE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='R4' where AST_RETIR_CNTRY_CD='NG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='R5' where AST_RETIR_CNTRY_CD='NH';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='R6' where AST_RETIR_CNTRY_CD='NI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S1' where AST_RETIR_CNTRY_CD='NS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S2' where AST_RETIR_CNTRY_CD='NU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S4' where AST_RETIR_CNTRY_CD='OC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S5' where AST_RETIR_CNTRY_CD='PA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S6' where AST_RETIR_CNTRY_CD='PC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S8' where AST_RETIR_CNTRY_CD='PF';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='S9' where AST_RETIR_CNTRY_CD='PG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='T2' where AST_RETIR_CNTRY_CD='PM';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='T3' where AST_RETIR_CNTRY_CD='PO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='T4' where AST_RETIR_CNTRY_CD='PP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='T5' where AST_RETIR_CNTRY_CD='PS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='T6' where AST_RETIR_CNTRY_CD='PU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='T9' where AST_RETIR_CNTRY_CD='RM';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U1' where AST_RETIR_CNTRY_CD='RP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U2' where AST_RETIR_CNTRY_CD='RQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U3' where AST_RETIR_CNTRY_CD='RS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U6' where AST_RETIR_CNTRY_CD='SB';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U7' where AST_RETIR_CNTRY_CD='SC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U8' where AST_RETIR_CNTRY_CD='SE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='U9' where AST_RETIR_CNTRY_CD='SF';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='V0' where AST_RETIR_CNTRY_CD='SG';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='V5' where AST_RETIR_CNTRY_CD='SN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='V7' where AST_RETIR_CNTRY_CD='SP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='V8' where AST_RETIR_CNTRY_CD='SR';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='V9' where AST_RETIR_CNTRY_CD='ST';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W0' where AST_RETIR_CNTRY_CD='SU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W1' where AST_RETIR_CNTRY_CD='SV';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W2' where AST_RETIR_CNTRY_CD='SW';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W4' where AST_RETIR_CNTRY_CD='SZ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W5' where AST_RETIR_CNTRY_CD='TC';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W6' where AST_RETIR_CNTRY_CD='TD';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W7' where AST_RETIR_CNTRY_CD='TE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='W9' where AST_RETIR_CNTRY_CD='TI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X0' where AST_RETIR_CNTRY_CD='TK';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X1' where AST_RETIR_CNTRY_CD='TL';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X2' where AST_RETIR_CNTRY_CD='TN';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X3' where AST_RETIR_CNTRY_CD='TO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X4' where AST_RETIR_CNTRY_CD='TP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X5' where AST_RETIR_CNTRY_CD='TS';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X6' where AST_RETIR_CNTRY_CD='TU';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='X9' where AST_RETIR_CNTRY_CD='TX';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Y2' where AST_RETIR_CNTRY_CD='UK';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Y3' where AST_RETIR_CNTRY_CD='UP';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Y4' where AST_RETIR_CNTRY_CD='UR';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z0' where AST_RETIR_CNTRY_CD='VI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z1' where AST_RETIR_CNTRY_CD='VM';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z2' where AST_RETIR_CNTRY_CD='VQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z3' where AST_RETIR_CNTRY_CD='VT';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z4' where AST_RETIR_CNTRY_CD='WA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z5' where AST_RETIR_CNTRY_CD='WE';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z7' where AST_RETIR_CNTRY_CD='WI';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='Z8' where AST_RETIR_CNTRY_CD='WQ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='00' where AST_RETIR_CNTRY_CD='WZ';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='01' where AST_RETIR_CNTRY_CD='YM';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='02' where AST_RETIR_CNTRY_CD='YO';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='03' where AST_RETIR_CNTRY_CD='ZA';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='04' where AST_RETIR_CNTRY_CD='ZI';
-- Change to final code
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AW' where AST_RETIR_CNTRY_CD='A0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AG' where AST_RETIR_CNTRY_CD='A1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='DZ' where AST_RETIR_CNTRY_CD='A3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AZ' where AST_RETIR_CNTRY_CD='A4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AD' where AST_RETIR_CNTRY_CD='A7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AS' where AST_RETIR_CNTRY_CD='A9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AU' where AST_RETIR_CNTRY_CD='B1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AU' where AST_RETIR_CNTRY_CD='B2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AT' where AST_RETIR_CNTRY_CD='B3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AI' where AST_RETIR_CNTRY_CD='B4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AQ' where AST_RETIR_CNTRY_CD='B5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BH' where AST_RETIR_CNTRY_CD='B6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BW' where AST_RETIR_CNTRY_CD='B8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BM' where AST_RETIR_CNTRY_CD='B9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BS' where AST_RETIR_CNTRY_CD='C1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BD' where AST_RETIR_CNTRY_CD='C2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BZ' where AST_RETIR_CNTRY_CD='C3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BA' where AST_RETIR_CNTRY_CD='C4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BO' where AST_RETIR_CNTRY_CD='C5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MM' where AST_RETIR_CNTRY_CD='C6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BJ' where AST_RETIR_CNTRY_CD='C7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BY' where AST_RETIR_CNTRY_CD='C8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SB' where AST_RETIR_CNTRY_CD='C9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='D0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TF' where AST_RETIR_CNTRY_CD='D2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BG' where AST_RETIR_CNTRY_CD='D4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BN' where AST_RETIR_CNTRY_CD='D6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='BI' where AST_RETIR_CNTRY_CD='D7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KH' where AST_RETIR_CNTRY_CD='D9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TD' where AST_RETIR_CNTRY_CD='E0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LK' where AST_RETIR_CNTRY_CD='E1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CG' where AST_RETIR_CNTRY_CD='E2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CD' where AST_RETIR_CNTRY_CD='E3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CN' where AST_RETIR_CNTRY_CD='E4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CL' where AST_RETIR_CNTRY_CD='E5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KY' where AST_RETIR_CNTRY_CD='E6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CC' where AST_RETIR_CNTRY_CD='E7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KM' where AST_RETIR_CNTRY_CD='E9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MP' where AST_RETIR_CNTRY_CD='F1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AU' where AST_RETIR_CNTRY_CD='F2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CR' where AST_RETIR_CNTRY_CD='F3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CF' where AST_RETIR_CNTRY_CD='F4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CK' where AST_RETIR_CNTRY_CD='F7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CS' where AST_RETIR_CNTRY_CD='F9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='DK' where AST_RETIR_CNTRY_CD='G0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='DM' where AST_RETIR_CNTRY_CD='G2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='DO' where AST_RETIR_CNTRY_CD='G3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='IE' where AST_RETIR_CNTRY_CD='G6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GQ' where AST_RETIR_CNTRY_CD='G7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='EE' where AST_RETIR_CNTRY_CD='G8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SV' where AST_RETIR_CNTRY_CD='H0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TF' where AST_RETIR_CNTRY_CD='H2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CZ' where AST_RETIR_CNTRY_CD='H3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='FK' where AST_RETIR_CNTRY_CD='H4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GF' where AST_RETIR_CNTRY_CD='H5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PF' where AST_RETIR_CNTRY_CD='I0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='I1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TF' where AST_RETIR_CNTRY_CD='I3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GM' where AST_RETIR_CNTRY_CD='I4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GA' where AST_RETIR_CNTRY_CD='I5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='DE' where AST_RETIR_CNTRY_CD='I6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GE' where AST_RETIR_CNTRY_CD='I7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GD' where AST_RETIR_CNTRY_CD='J0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GG' where AST_RETIR_CNTRY_CD='J1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='DE' where AST_RETIR_CNTRY_CD='J3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TF' where AST_RETIR_CNTRY_CD='J4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GU' where AST_RETIR_CNTRY_CD='J6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GN' where AST_RETIR_CNTRY_CD='J9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PS' where AST_RETIR_CNTRY_CD='K1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='HT' where AST_RETIR_CNTRY_CD='K2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='HN' where AST_RETIR_CNTRY_CD='K5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='K6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='IS' where AST_RETIR_CNTRY_CD='K9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='FR' where AST_RETIR_CNTRY_CD='L4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='IL' where AST_RETIR_CNTRY_CD='L6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CI' where AST_RETIR_CNTRY_CD='L8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NT' where AST_RETIR_CNTRY_CD='L9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='IQ' where AST_RETIR_CNTRY_CD='M0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='JP' where AST_RETIR_CNTRY_CD='M1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NO' where AST_RETIR_CNTRY_CD='M4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='M6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TF' where AST_RETIR_CNTRY_CD='M7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KP' where AST_RETIR_CNTRY_CD='N0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='N1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KI' where AST_RETIR_CNTRY_CD='N2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KR' where AST_RETIR_CNTRY_CD='N3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CX' where AST_RETIR_CNTRY_CD='N4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KW' where AST_RETIR_CNTRY_CD='N5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LB' where AST_RETIR_CNTRY_CD='N8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LV' where AST_RETIR_CNTRY_CD='N9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LT' where AST_RETIR_CNTRY_CD='O0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LR' where AST_RETIR_CNTRY_CD='O1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SK' where AST_RETIR_CNTRY_CD='O2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='O3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LI' where AST_RETIR_CNTRY_CD='O4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LS' where AST_RETIR_CNTRY_CD='O5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MG' where AST_RETIR_CNTRY_CD='O8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MQ' where AST_RETIR_CNTRY_CD='O9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MO' where AST_RETIR_CNTRY_CD='P0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='YT' where AST_RETIR_CNTRY_CD='P2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MN' where AST_RETIR_CNTRY_CD='P3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MS' where AST_RETIR_CNTRY_CD='P4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MW' where AST_RETIR_CNTRY_CD='P5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MC' where AST_RETIR_CNTRY_CD='P8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MA' where AST_RETIR_CNTRY_CD='P9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MU' where AST_RETIR_CNTRY_CD='Q0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='Q1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='OM' where AST_RETIR_CNTRY_CD='Q4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ME' where AST_RETIR_CNTRY_CD='Q6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AN' where AST_RETIR_CNTRY_CD='R0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NU' where AST_RETIR_CNTRY_CD='R2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NE' where AST_RETIR_CNTRY_CD='R4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='VU' where AST_RETIR_CNTRY_CD='R5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NG' where AST_RETIR_CNTRY_CD='R6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SR' where AST_RETIR_CNTRY_CD='S1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NI' where AST_RETIR_CNTRY_CD='S2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ZZ' where AST_RETIR_CNTRY_CD='S4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PY' where AST_RETIR_CNTRY_CD='S5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PN' where AST_RETIR_CNTRY_CD='S6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='XP' where AST_RETIR_CNTRY_CD='S8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='XS' where AST_RETIR_CNTRY_CD='S9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PA' where AST_RETIR_CNTRY_CD='T2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PT' where AST_RETIR_CNTRY_CD='T3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PG' where AST_RETIR_CNTRY_CD='T4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PW' where AST_RETIR_CNTRY_CD='T5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GW' where AST_RETIR_CNTRY_CD='T6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='MH' where AST_RETIR_CNTRY_CD='T9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PH' where AST_RETIR_CNTRY_CD='U1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PR' where AST_RETIR_CNTRY_CD='U2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='RU' where AST_RETIR_CNTRY_CD='U3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PM' where AST_RETIR_CNTRY_CD='U6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='KN' where AST_RETIR_CNTRY_CD='U7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SC' where AST_RETIR_CNTRY_CD='U8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ZA' where AST_RETIR_CNTRY_CD='U9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SN' where AST_RETIR_CNTRY_CD='V0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SG' where AST_RETIR_CNTRY_CD='V5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ES' where AST_RETIR_CNTRY_CD='V7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='RS' where AST_RETIR_CNTRY_CD='V8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='LC' where AST_RETIR_CNTRY_CD='V9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SD' where AST_RETIR_CNTRY_CD='W0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SJ' where AST_RETIR_CNTRY_CD='W1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SE' where AST_RETIR_CNTRY_CD='W2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='CH' where AST_RETIR_CNTRY_CD='W4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='AE' where AST_RETIR_CNTRY_CD='W5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TT' where AST_RETIR_CNTRY_CD='W6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TF' where AST_RETIR_CNTRY_CD='W7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TJ' where AST_RETIR_CNTRY_CD='W9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TC' where AST_RETIR_CNTRY_CD='X0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TK' where AST_RETIR_CNTRY_CD='X1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TO' where AST_RETIR_CNTRY_CD='X2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TG' where AST_RETIR_CNTRY_CD='X3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ST' where AST_RETIR_CNTRY_CD='X4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TN' where AST_RETIR_CNTRY_CD='X5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TR' where AST_RETIR_CNTRY_CD='X6';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='TM' where AST_RETIR_CNTRY_CD='X9';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='GB' where AST_RETIR_CNTRY_CD='Y2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UA' where AST_RETIR_CNTRY_CD='Y3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SU' where AST_RETIR_CNTRY_CD='Y4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='VG' where AST_RETIR_CNTRY_CD='Z0';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='VN' where AST_RETIR_CNTRY_CD='Z1';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='VI' where AST_RETIR_CNTRY_CD='Z2';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='VA' where AST_RETIR_CNTRY_CD='Z3';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='NA' where AST_RETIR_CNTRY_CD='Z4';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='PS' where AST_RETIR_CNTRY_CD='Z5';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='EH' where AST_RETIR_CNTRY_CD='Z7';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='UM' where AST_RETIR_CNTRY_CD='Z8';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='SZ' where AST_RETIR_CNTRY_CD='00';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='YE' where AST_RETIR_CNTRY_CD='01';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='YU' where AST_RETIR_CNTRY_CD='02';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ZM' where AST_RETIR_CNTRY_CD='03';
UPDATE CM_AST_RETIRE_DOC_T SET AST_RETIR_CNTRY_CD='ZW' where AST_RETIR_CNTRY_CD='04';

