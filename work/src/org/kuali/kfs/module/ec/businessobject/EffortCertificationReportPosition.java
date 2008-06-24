package org.kuali.kfs.module.ec.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.integration.businessobject.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.integration.service.LaborModuleService;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Business Object for the Effort Certification Report Position Table.
 */
public class EffortCertificationReportPosition extends PersistableBusinessObjectBase implements Inactivateable {
    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;
    private String effortCertificationReportPositionObjectGroupCode;
    private boolean active;

    private LaborLedgerPositionObjectGroup positionObjectGroup;

    /**
     * Default constructor.
     */
    public EffortCertificationReportPosition() {
        super();
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the effortCertificationReportNumber attribute.
     * 
     * @return Returns the effortCertificationReportNumber.
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * 
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets the effortCertificationReportPositionObjectGroupCode attribute.
     * 
     * @return Returns the effortCertificationReportPositionObjectGroupCode.
     */
    public String getEffortCertificationReportPositionObjectGroupCode() {
        return effortCertificationReportPositionObjectGroupCode;
    }

    /**
     * Sets the effortCertificationReportPositionObjectGroupCode attribute value.
     * 
     * @param effortCertificationReportPositionObjectGroupCode The effortCertificationReportPositionObjectGroupCode to set.
     */
    public void setEffortCertificationReportPositionObjectGroupCode(String effortCertificationReportPositionObjectGroupCode) {
        this.effortCertificationReportPositionObjectGroupCode = effortCertificationReportPositionObjectGroupCode;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * gets the positionObjetGroup
     * 
     * @return
     */
    public LaborLedgerPositionObjectGroup getPositionObjectGroup() {
        if (StringUtils.isBlank(effortCertificationReportPositionObjectGroupCode)) {
            if (positionObjectGroup != null) {
                positionObjectGroup = null;
            }
        } else {
            if (positionObjectGroup == null || !positionObjectGroup.getPositionObjectGroupCode().equals(effortCertificationReportPositionObjectGroupCode)) {
                positionObjectGroup = SpringContext.getBean(LaborModuleService.class).getLaborLedgerPositionObjectGroup(effortCertificationReportPositionObjectGroupCode);
            }
        }
        return positionObjectGroup;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("effortCertificationReportNumber", this.effortCertificationReportNumber);
        m.put("effortCertificationReportPositionObjectGroupCode", this.effortCertificationReportPositionObjectGroupCode);
        return m;
    }
}
