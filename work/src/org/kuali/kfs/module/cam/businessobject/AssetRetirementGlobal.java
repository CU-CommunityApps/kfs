package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;
    private Long mergedTargetCapitalAssetNumber;
    private String mergedTargetCapitalAssetDescription;
    private String retirementReasonCode;
    private Date retirementDate;
    private Asset mergedTargetCapitalAsset;
    private AssetRetirementReason retirementReason;
    private FinancialSystemDocumentHeader documentHeader;
    private List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails;
    // non-persistent relation
    private AssetRetirementGlobalDetail sharedRetirementInfo;

    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;


    public AssetRetirementGlobalDetail getSharedRetirementInfo() {
        return sharedRetirementInfo;
    }


    public void setSharedRetirementInfo(AssetRetirementGlobalDetail sharedRetirementInfo) {
        this.sharedRetirementInfo = sharedRetirementInfo;
    }


    /**
     * Default constructor.
     */
    public AssetRetirementGlobal() {
        this.assetRetirementGlobalDetails = new TypedArrayList(AssetRetirementGlobalDetail.class);
        this.generalLedgerPendingEntries = new TypedArrayList(GeneralLedgerPendingEntry.class);
    }


    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * @see org.kuali.rice.kns.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        AssetRetirementService retirementService = SpringContext.getBean(AssetRetirementService.class);

        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        if (retirementService.isAssetRetiredByMerged(this) && mergedTargetCapitalAsset != null) {
            setMergeObjectsForPersist(persistables, retirementService);
        }

        for (AssetRetirementGlobalDetail detail : assetRetirementGlobalDetails) {
            setAssetForPersist(detail, persistables, retirementService);
        }

        return persistables;
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedList = super.buildListOfDeletionAwareLists();

        managedList.add(getAssetRetirementGlobalDetails());

        return managedList;
    }

    /**
     * 
     * This method set asset fields for update
     * 
     * @param detail
     * @param persistables
     */
    private void setAssetForPersist(AssetRetirementGlobalDetail detail, List<PersistableBusinessObject> persistables, AssetRetirementService retirementService) {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        // load the object by key
        Asset asset = detail.getAsset();
        asset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED);
        asset.setRetirementReasonCode(retirementReasonCode);
        
        // set retirement fiscal year and period code into asset
        UniversityDate currentUniversityDate = universityDateService.getCurrentUniversityDate();
        if (ObjectUtils.isNotNull(currentUniversityDate)) {
            asset.setRetirementFiscalYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
            asset.setRetirementPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
        }


        if (retirementService.isAssetRetiredByTheft(this) && StringUtils.isNotBlank(sharedRetirementInfo.getPaidCaseNumber())) {
            asset.setCampusPoliceDepartmentCaseNumber(sharedRetirementInfo.getPaidCaseNumber());
        }
        else if (retirementService.isAssetRetiredBySold(this) || retirementService.isAssetRetiredByAuction(this)) {
            asset.setRetirementChartOfAccountsCode(detail.getRetirementChartOfAccountsCode());
            asset.setRetirementAccountNumber(detail.getRetirementAccountNumber());
            asset.setCashReceiptFinancialDocumentNumber(detail.getCashReceiptFinancialDocumentNumber());
            asset.setSalePrice(detail.getSalePrice());
            asset.setEstimatedSellingPrice(detail.getEstimatedSellingPrice());
        }
        else if (retirementService.isAssetRetiredByMerged(this)) {
            asset.setTotalCostAmount(KualiDecimal.ZERO);
            asset.setSalvageAmount(KualiDecimal.ZERO);
        }else if (retirementService.isAssetRetiredByExternalTransferOrGift(this)) {
            persistables.add(setOffCampusLocationObjectsForPersist(detail, asset));
        }
        asset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        persistables.add(asset);
    }

    /**
     * This method set off campus location for persist
     * 
     * @param AssetGlobalDetail and Asset to populate AssetLocation
     * @return Returns the AssetLocation.
     */
    private AssetLocation setOffCampusLocationObjectsForPersist(AssetRetirementGlobalDetail detail, Asset asset) {
        AssetLocation offCampusLocation = new AssetLocation();
        offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.RETIREMENT);
        offCampusLocation = (AssetLocation) SpringContext.getBean(BusinessObjectService.class).retrieve(offCampusLocation);
        if (offCampusLocation == null) {
            offCampusLocation = new AssetLocation();
            offCampusLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
            offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.RETIREMENT);
            asset.getAssetLocations().add(offCampusLocation);
        }

        offCampusLocation.setAssetLocationContactName(detail.getRetirementContactName());
        offCampusLocation.setAssetLocationInstitutionName(detail.getRetirementInstitutionName());
        offCampusLocation.setAssetLocationPhoneNumber(detail.getRetirementPhoneNumber());
        offCampusLocation.setAssetLocationStreetAddress(detail.getRetirementStreetAddress());
        offCampusLocation.setAssetLocationCityName(detail.getRetirementCityName());
        offCampusLocation.setAssetLocationStateCode(detail.getRetirementStateCode());
        offCampusLocation.setAssetLocationCountryCode(detail.getRetirementCountryCode());
        offCampusLocation.setAssetLocationZipCode(detail.getRetirementZipCode());

        return offCampusLocation;
    }

    /**
     * 
     * This method set target payment and source payment; set target/source asset salvageAmount/totalCostAmount
     * 
     * @param persistables
     */
    private void setMergeObjectsForPersist(List<PersistableBusinessObject> persistables, AssetRetirementService retirementService) {
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        AssetPaymentService assetPaymentService = SpringContext.getBean(AssetPaymentService.class);

        Integer maxTargetSequenceNo = assetPaymentService.getMaxSequenceNumber(mergedTargetCapitalAssetNumber);

        KualiDecimal salvageAmount = KualiDecimal.ZERO;
        KualiDecimal totalCostAmount = KualiDecimal.ZERO;
        Asset sourceAsset;

        // update for each merge source asset
        for (AssetRetirementGlobalDetail detail : getAssetRetirementGlobalDetails()) {
            detail.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);
            sourceAsset = detail.getAsset();

            totalCostAmount = totalCostAmount.add(paymentSummaryService.calculatePaymentTotalCost(sourceAsset));
            salvageAmount = salvageAmount.add(sourceAsset.getSalvageAmount());

            retirementService.generateOffsetPaymentsForEachSource(sourceAsset, persistables, detail.getDocumentNumber());
            maxTargetSequenceNo = retirementService.generateNewPaymentForTarget(mergedTargetCapitalAsset, sourceAsset, persistables, maxTargetSequenceNo, detail.getDocumentNumber());

        }
        KualiDecimal mergedTargetSalvageAmount = (mergedTargetCapitalAsset.getSalvageAmount() != null ? mergedTargetCapitalAsset.getSalvageAmount() : KualiDecimal.ZERO);
        
        // update merget target asset
        mergedTargetCapitalAsset.setTotalCostAmount(totalCostAmount.add(paymentSummaryService.calculatePaymentTotalCost(mergedTargetCapitalAsset)));
        mergedTargetCapitalAsset.setSalvageAmount(salvageAmount.add(mergedTargetSalvageAmount));
        mergedTargetCapitalAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));
        mergedTargetCapitalAsset.setCapitalAssetDescription(this.getMergedTargetCapitalAssetDescription());
        persistables.add(mergedTargetCapitalAsset);
    }


    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetRetirementGlobalDetails();
    }

    public boolean isPersistable() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @return Returns the mergedTargetCapitalAssetNumber
     */
    public Long getMergedTargetCapitalAssetNumber() {
        return mergedTargetCapitalAssetNumber;
    }

    /**
     * Sets the mergedTargetCapitalAssetNumber attribute.
     * 
     * @param mergedTargetCapitalAssetNumber The mergedTargetCapitalAssetNumber to set.
     */
    public void setMergedTargetCapitalAssetNumber(Long mergedTargetCapitalAssetNumber) {
        this.mergedTargetCapitalAssetNumber = mergedTargetCapitalAssetNumber;
    }


    /**
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementDate attribute.
     * 
     * @return Returns the retirementDate
     */
    public Date getRetirementDate() {
        return retirementDate;
    }

    /**
     * Sets the retirementDate attribute.
     * 
     * @param retirementDate The retirementDate to set.
     */
    public void setRetirementDate(Date remeretirementDatentDate) {
        this.retirementDate = remeretirementDatentDate;
    }

    /**
     * Gets the mergedTargetCapitalAsset attribute.
     * 
     * @return Returns the mergedTargetCapitalAsset.
     */
    public Asset getMergedTargetCapitalAsset() {
        return mergedTargetCapitalAsset;
    }

    /**
     * Sets the mergedTargetCapitalAsset attribute value.
     * 
     * @param mergedTargetCapitalAsset The mergedTargetCapitalAsset to set.
     * @deprecated
     */
    public void setMergedTargetCapitalAsset(Asset mergedTargetCapitalAsset) {
        this.mergedTargetCapitalAsset = mergedTargetCapitalAsset;
    }

    /**
     * Gets the retirementReason attribute.
     * 
     * @return Returns the retirementReason.
     */
    public AssetRetirementReason getRetirementReason() {
        return retirementReason;
    }

    /**
     * Sets the retirementReason attribute value.
     * 
     * @param retirementReason The retirementReason to set.
     * @deprecated
     */
    public void setRetirementReason(AssetRetirementReason retirementReason) {
        this.retirementReason = retirementReason;
    }


    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public List<AssetRetirementGlobalDetail> getAssetRetirementGlobalDetails() {
        return assetRetirementGlobalDetails;
    }

    public void setAssetRetirementGlobalDetails(List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails) {
        this.assetRetirementGlobalDetails = assetRetirementGlobalDetails;
    }


    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }


    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> glPendingEntries) {
        this.generalLedgerPendingEntries = glPendingEntries;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }


    public String getMergedTargetCapitalAssetDescription() {
        return mergedTargetCapitalAssetDescription;
    }


    public void setMergedTargetCapitalAssetDescription(String mergedTargetCapitalAssetDescription) {
        this.mergedTargetCapitalAssetDescription = mergedTargetCapitalAssetDescription;
    }

}
