/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceStep;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.CalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.PaymentRequestForEInvoiceEvent;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Attachment;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This is a helper service to parse electronic invoice file, match it with a PO and create PREQs based on the eInvoice. Also, it 
 * provides helper methods to the reject document to match it with a PO and create PREQ.
 * 
 * @author vpremcha
 * @author delyea (code copied from EPIC)
 */

@Transactional
public class ElectronicInvoiceHelperServiceImpl implements ElectronicInvoiceHelperService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceHelperServiceImpl.class);

    private final String UNKNOWN_DUNS_IDENTIFIER = "Unknown";
    private final String INVOICE_FILE_MIME_TYPE = "text/xml";  
    
    private StringBuffer emailTextErrorList = new StringBuffer();
    
    private Map<String, Map> itemTypeMappingsCache = new WeakHashMap<String, Map>();  
    private Map<String,ItemType> kualiItemTypes = new HashMap<String, ItemType>();

    private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
    private MailService mailService;
    private ElectronicInvoiceMatchingService matchingService; 
    private ElectronicInvoicingDao electronicInvoicingDao;
    private BatchInputFileService batchInputFileService;
    private VendorService vendorService;
    private PurchaseOrderService purchaseOrderService;
    private PaymentRequestService paymentRequestService;
    private KualiConfigurationService kualiConfigurationService;
    
    public boolean loadElectronicInvoices() {

        String baseDirName = getBaseDirName();
        String rejectDirName = getRejectDirName();
        String acceptDirName = getAcceptDirName();
        String sourceDirName = getSourceDirName();

        boolean moveFiles = BooleanUtils.toBoolean(SpringContext.getBean(ParameterService.class).getParameterValue(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.FILE_MOVE_AFTER_LOAD_IND));

        if (LOG.isInfoEnabled()){
            LOG.info("Invoice Base Directory - " + electronicInvoiceInputFileType.getDirectoryPath());
            LOG.info("Invoice Source Directory - " + sourceDirName);
            LOG.info("Invoice Accept Directory - " + acceptDirName);
            LOG.info("Invoice Reject Directory - " + rejectDirName);
            LOG.info("Is moving files allowed - " + moveFiles);
        }

        if (StringUtils.isBlank(rejectDirName)) {
            throw new RuntimeException("Reject directory name should not be empty");
        }

        if (StringUtils.isBlank(acceptDirName)) {
            throw new RuntimeException("Accept directory name should not be empty");
        }

        if (StringUtils.isBlank(sourceDirName)) {
            throw new RuntimeException("Source directory name should not be empty");
        }

        File baseDir = new File(baseDirName);
        File[] filesToBeProcessed = baseDir.listFiles(new FileFilter() {
                                                            public boolean accept(File file) {
                                                                return (!file.isDirectory() && file.getName().endsWith(".xml"));
                                                            }
                                                        });

        if (!baseDir.exists()){
            throw new RuntimeException("Base dir [" + baseDirName + "] doesn't exists in the system");
        }
        
        if (filesToBeProcessed == null || 
            filesToBeProcessed.length == 0) {

            StringBuffer mailText = new StringBuffer();
            
            mailText.append("\n\n");
            mailText.append(PurapConstants.ElectronicInvoice.NO_FILES_PROCESSED_EMAIL_MESSAGE);
            mailText.append("\n\n");
            
            sendSummary(mailText);
            return false;
        }

        try {
            /**
             * Create, if not there
             */
            FileUtils.forceMkdir(new File(acceptDirName));
            FileUtils.forceMkdir(new File(rejectDirName));
            FileUtils.forceMkdir(new File(sourceDirName));
            
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        boolean hasRejectedFile = false;
        ElectronicInvoiceLoad eInvoiceLoad = new ElectronicInvoiceLoad();

        if (LOG.isInfoEnabled()){
            LOG.info(filesToBeProcessed.length + " file(s) available for processing");
        }

        for (int i = 0; i < filesToBeProcessed.length; i++) {

            LOG.info("Processing " + filesToBeProcessed[i].getName() + "....");

            boolean movedToSourceDir = addNamespaceDefinition(eInvoiceLoad, filesToBeProcessed[i]);
            
            if (!movedToSourceDir){
                if (moveFiles) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(filesToBeProcessed[i].getName() + " has been marked to move to " + rejectDirName);
                    }
                    eInvoiceLoad.addRejectFileToMove(filesToBeProcessed[i], rejectDirName);
                }
                hasRejectedFile = true;
                continue;
            }
            
            File fileInSourceDir = getInvoiceFile(filesToBeProcessed[i].getName());
            
            boolean isRejected = processElectronicInvoice(eInvoiceLoad, fileInSourceDir);

            /**
             * If there is a single order has rejects and the remainings are accepted in a invoice file, 
             * then the entire file has been moved to the reject dir. 
             */
            if (isRejected) {
                if (LOG.isInfoEnabled()){
                    LOG.info(filesToBeProcessed[i].getName() + " has been rejected");
                }
                if (moveFiles) {
                    if (LOG.isDebugEnabled()){
                        LOG.debug(filesToBeProcessed[i].getName() + " has been marked to move to " + rejectDirName);
                    }
                    eInvoiceLoad.addRejectFileToMove(filesToBeProcessed[i], rejectDirName);
                }
                hasRejectedFile = true;
            } else {
                if (LOG.isInfoEnabled()){
                    LOG.info(filesToBeProcessed[i].getName() + " has been accepted");
                }
                if (moveFiles) {
                    if (!moveFile(filesToBeProcessed[i], acceptDirName)) {
                        String msg = filesToBeProcessed[i].getName() + " unable to move";
                        LOG.error(msg);
                        throw new PurError(msg);
                    }
                }
            }
            
            /**
             * It's not needed to have the namespace added file after processing. Original file will be in base/accept/reject dir
             * based on the processing result 
             */
            try {
                FileUtils.forceDelete(getInvoiceFile(filesToBeProcessed[i].getName()));
            }catch (IOException e) {
                throw new PurError(e);
            }
        }

         StringBuffer summaryText = saveLoadSummary(eInvoiceLoad);

         StringBuffer finalText = new StringBuffer();
         finalText.append(summaryText);
         finalText.append("\n");
         finalText.append(emailTextErrorList);
         sendSummary(finalText);

         LOG.debug("Processing completed");

         return hasRejectedFile;

    }
    
    private boolean addNamespaceDefinition(ElectronicInvoiceLoad eInvoiceLoad, 
                                           File invoiceFile) {
        
        
        boolean result = true;
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Adding namespace definition");
        }
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false); // It's not needed to validate here
        builderFactory.setIgnoringElementContentWhitespace(true); 
        
        DocumentBuilder builder = null;
        try {
          builder = builderFactory.newDocumentBuilder();  // Create the parser
        } catch(ParserConfigurationException e) {
            LOG.error("Error getting document builder - " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        Document xmlDoc = null;

        try {
            xmlDoc = builder.parse(invoiceFile);
        } catch(Exception e) {
            if (LOG.isDebugEnabled()){
                LOG.debug("Error parsing the file - " + e.getMessage());
            }
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID);
            return false;
        }
        
        Node node = xmlDoc.getDocumentElement();
        Element element = (Element)node;

        String xmlnsValue = element.getAttribute("xmlns");
        String xmlnsXsiValue = element.getAttribute("xmlns:xsi");
        
        File namespaceAddedFile = getInvoiceFile(invoiceFile.getName());
        
        /**
         * It's not needed to add the namespace if it's already there. This will
         * save some time in processing
         */
        if (StringUtils.equals(xmlnsValue, "http://www.kuali.org/kfs/purap/electronicInvoice") && 
            StringUtils.equals(xmlnsXsiValue, "http://www.w3.org/2001/XMLSchema-instance")){
            if (LOG.isDebugEnabled()){
                LOG.debug("xmlns and xmlns:xsi attributes already exists in the invoice xml");
            }
            try {
                FileUtils.copyFile(invoiceFile, namespaceAddedFile);
            }
            catch (IOException e) {
                throw new PurError(e);
            }
            return true;
        }
        
        element.setAttribute("xmlns", "http://www.kuali.org/kfs/purap/electronicInvoice");
        element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        OutputFormat outputFormat = new OutputFormat(xmlDoc );
        outputFormat.setIndenting(true);
        outputFormat.setLineWidth(0);
        outputFormat.setOmitDocumentType(true);
        outputFormat.setEncoding("UTF-8");
        
        FileOutputStream outputStream;
        
        /**
         * If a file with the same name exists in source dir, delete it. Otherwise, it's not 
         * possible for the serializer to write the file.
         */
        if (namespaceAddedFile.exists()){
            if (LOG.isDebugEnabled()){
                LOG.debug("Deleting existing " + invoiceFile.getName() + " from the source dir");
                try {
                    FileUtils.forceDelete(namespaceAddedFile);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        try {
            
            outputStream = new FileOutputStream(namespaceAddedFile);
            XMLSerializer serializer = new XMLSerializer( outputStream,outputFormat );
            serializer.asDOMSerializer();
            serializer.serialize( xmlDoc.getDocumentElement() );
            outputStream.flush();
            outputStream.close();
        }
        catch (FileNotFoundException e) {
           /**
            * Shallow. This never happen
            */
        }catch (IOException e) {
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.ERROR_ADDING_SCHEMA);
            return false;
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Successfully added the namespace");
        }

        return true;
    }
    
    /**
     * This method processes a single electronic invoice file
     * 
     * @param eInvoiceLoad the load summary to be modified
     * @return boolean where true means there has been some type of reject
     */
    private boolean processElectronicInvoice(ElectronicInvoiceLoad eInvoiceLoad, 
                                             File invoiceFile) {

        ElectronicInvoice eInvoice = null;

        if (LOG.isDebugEnabled()){
            LOG.debug("Processing " + invoiceFile.getName());
        }
        
        try {
            eInvoice = loadElectronicInvoice(invoiceFile);
        }catch (CxmlParseException e) {
            LOG.debug("Error loading file - " + e.getMessage());
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID);
            return true;
        }

        eInvoice.setFileName(invoiceFile.getName());
        
        boolean isCompleteFailure = checkForCompleteFailure(eInvoiceLoad,eInvoice,invoiceFile); 
        
        if (isCompleteFailure){
            return true;
        }

        setVendorDUNSNumber(eInvoice);
        setVendorDetails(eInvoice);
        
        Map itemTypeMappings = getItemTypeMappings(eInvoice.getVendorHeaderID(),eInvoice.getVendorDetailID());
        Map kualiItemTypes = getKualiItemTypes();
        
        if (LOG.isDebugEnabled()){
            if (itemTypeMappings != null && itemTypeMappings.size() > 0){
                LOG.debug("Item mappings found");
            }
        }
        
        boolean validateHeader = true;
        
        for (ElectronicInvoiceOrder order : eInvoice.getInvoiceDetailOrders()) {

            String poID = order.getOrderReferenceOrderID();
            PurchaseOrderDocument po = null;
            
            if (NumberUtils.isDigits(StringUtils.defaultString(poID))){
                po = purchaseOrderService.getCurrentPurchaseOrder(new Integer(poID));    
                if (po != null){
                    order.setInvoicePurchaseOrderID(poID);
                    order.setPurchaseOrderID(po.getPurapDocumentIdentifier());
                    order.setPurchaseOrderCampusCode(po.getDeliveryCampus().getCampusCode());
                    
                    if (LOG.isDebugEnabled()){
                        LOG.debug("PO matching Document found");
                    }
                }
            }
            
            ElectronicInvoiceOrderHolder orderHolder = new ElectronicInvoiceOrderHolder(eInvoice,order,po,itemTypeMappings,kualiItemTypes,validateHeader);
            matchingService.doMatchingProcess(orderHolder);
            
            if (orderHolder.isInvoiceRejected()){
                
                ElectronicInvoiceRejectDocument rejectDocument = createRejectDocument(eInvoice, order,eInvoiceLoad);
                
                ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                loadSummary.addFailedInvoiceOrder(rejectDocument.getTotalAmount(),eInvoice);
                eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
                
            }else{
                
                PaymentRequestDocument preqDoc  = createPaymentRequest(orderHolder);
                
                if (orderHolder.isInvoiceRejected()){
                    /**
                     * This is required. If there is anything in the error map, then it's not possible to route the doc since the rice
                     * is throwing error if errormap is not empty before routing the doc. 
                     */
                    GlobalVariables.getErrorMap().clear();
                    
                    ElectronicInvoiceRejectDocument rejectDocument = createRejectDocument(eInvoice, order,eInvoiceLoad);
                    
                    ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                    loadSummary.addFailedInvoiceOrder(rejectDocument.getTotalAmount(),eInvoice);
                    eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
                    
                }else{
                    ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                    loadSummary.addSuccessfulInvoiceOrder(preqDoc.getTotalDollarAmount(),eInvoice);
                    eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
                }
                
            }
            
            validateHeader = false;
        }
        
        return eInvoice.isFileRejected();
    }
    
    private void setVendorDUNSNumber(ElectronicInvoice eInvoice) {
        
        String dunsNumber = null;
        
        if (StringUtils.equals(eInvoice.getCxmlHeader().getFromDomain(),"DUNS")) {
            dunsNumber = eInvoice.getCxmlHeader().getFromIdentity();
        }else if (StringUtils.equals(eInvoice.getCxmlHeader().getSenderDomain(),"DUNS")) {
            dunsNumber = eInvoice.getCxmlHeader().getSenderIdentity();
        }
        
        if (StringUtils.isNotEmpty((dunsNumber))) {
            if (LOG.isDebugEnabled()){
                LOG.debug("Setting Vendor DUNS number - " + dunsNumber);
            }
            eInvoice.setDunsNumber(dunsNumber);
        }
        
    }
    
    private void setVendorDetails(ElectronicInvoice eInvoice){
        
        if (StringUtils.isNotEmpty(eInvoice.getDunsNumber())){
            
            VendorDetail vendorDetail = vendorService.getVendorByDunsNumber(eInvoice.getDunsNumber());
            
            if (vendorDetail != null) {
                if (LOG.isDebugEnabled()){
                    LOG.debug("Vendor match found - " + vendorDetail.getVendorNumber());
                }
                eInvoice.setVendorHeaderID(vendorDetail.getVendorHeaderGeneratedIdentifier());
                eInvoice.setVendorDetailID(vendorDetail.getVendorDetailAssignedIdentifier());
                eInvoice.setVendorName(vendorDetail.getVendorName());
            }else{
                eInvoice.setVendorHeaderID(null);
                eInvoice.setVendorDetailID(null);
                eInvoice.setVendorName(null);
            }
        }
    }
    
    private void validateVendorDetails(ElectronicInvoiceRejectDocument rejectDocument){
        
        boolean vendorFound = false;
        
        if (StringUtils.isNotEmpty(rejectDocument.getVendorDunsNumber())){
            
            VendorDetail vendorDetail = vendorService.getVendorByDunsNumber(rejectDocument.getVendorDunsNumber());
            
            if (vendorDetail != null) {
                if (LOG.isDebugEnabled()){
                    LOG.debug("Vendor [" + vendorDetail.getVendorNumber() + "] match found for the DUNS - " + rejectDocument.getVendorDunsNumber());
                }
                rejectDocument.setVendorHeaderGeneratedIdentifier(vendorDetail.getVendorHeaderGeneratedIdentifier());
                rejectDocument.setVendorDetailAssignedIdentifier(vendorDetail.getVendorDetailAssignedIdentifier());
                rejectDocument.setVendorDetail(vendorDetail);
                vendorFound = true;
            }
        }
        
        if (!vendorFound){
            rejectDocument.setVendorHeaderGeneratedIdentifier(null);
            rejectDocument.setVendorDetailAssignedIdentifier(null);
            rejectDocument.setVendorDetail(null);
        }
        
        String newDocumentDesc = generateRejectDocumentDescription(rejectDocument); 
        rejectDocument.getDocumentHeader().setDocumentDescription(newDocumentDesc);
    }
    
    private Map getItemTypeMappings(Integer vendorHeaderId,
                                    Integer vendorDetailId) {
        
        Map itemTypeMappings = null;
        
        if (vendorHeaderId != null && vendorDetailId != null) {
            
            String vendorNumber = getVendorNumber(vendorHeaderId,vendorDetailId);
            itemTypeMappings = itemTypeMappingsCache.get(vendorNumber);
            
            if (itemTypeMappings == null){
                itemTypeMappings = electronicInvoicingDao.getItemMappingMap(vendorHeaderId,vendorDetailId);
                itemTypeMappingsCache.put(vendorNumber,itemTypeMappings);
            }
        }
        /*else {
            
            itemTypeMappings = itemTypeMappingsCache.get(UNKNOWN_DUNS_IDENTIFIER);
            
            if (itemTypeMappings == null){
                itemTypeMappings = mappingService.getDefaultItemMappingMap();
                itemTypeMappingsCache.put(UNKNOWN_DUNS_IDENTIFIER,itemTypeMappings);
            }
        }*/

        return itemTypeMappings;
    }
    
    private String getVendorNumber(Integer vendorHeaderId,
                                   Integer vendorDetailId ){
        
        if (vendorHeaderId != null && vendorDetailId != null) {
            VendorDetail forVendorNo = new VendorDetail();
            forVendorNo.setVendorHeaderGeneratedIdentifier(vendorHeaderId);
            forVendorNo.setVendorDetailAssignedIdentifier(vendorDetailId);
            return forVendorNo.getVendorNumber();
        }else{
            return null;
        }
    }
    
    private Map<String, ItemType> getKualiItemTypes(){
        
        if (kualiItemTypes == null){
            
            Collection<ItemType> collection = SpringContext.getBean(BusinessObjectService.class).findAll(ItemType.class);
            Map kualiItemTypes = new HashMap<String, ItemType>();
            
            if (collection == null || collection.size() == 0){
                throw new RuntimeException("Kauli Item types not available");
            }else{
                if (collection != null){
                    ItemType[] itemTypes = new ItemType[collection.size()];
                    collection.toArray(itemTypes);
                    for (int i = 0; i < itemTypes.length; i++) {
                        kualiItemTypes.put(itemTypes[i].getItemTypeCode(),itemTypes[i]);
                    }
                }
            }
        }
        
        return kualiItemTypes;
    }
    
    private boolean checkForCompleteFailure(ElectronicInvoiceLoad electronicInvoiceLoad, 
                                            ElectronicInvoice electronicInvoice,
                                            File invoiceFile){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Checking for complete failure...");
        }
        
        if (electronicInvoice.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator()) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile,PurapConstants.ElectronicInvoice.HEADER_INVOICE_IND_ON);
            return true;
        }
        
        if (electronicInvoice.getInvoiceDetailOrders().size() < 1) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile,PurapConstants.ElectronicInvoice.INVOICE_ORDERS_NOT_FOUND);
            return true;
        }
        
        //IN EPIC, it says - Future Release - Enter valid location for Customer Number from E-Invoice
        //mappingService.getInvoiceCustomerNumber() doesnt have any implementation
//        electronicInvoice.setCustomerNumber(mappingService.getInvoiceCustomerNumber(electronicInvoice));
        
        for (Iterator orderIter = electronicInvoice.getInvoiceDetailOrders().iterator(); orderIter.hasNext();) {
          ElectronicInvoiceOrder invoiceOrder = (ElectronicInvoiceOrder) orderIter.next();
          for (Iterator itemIter = invoiceOrder.getInvoiceItems().iterator(); itemIter.hasNext();) {
            ElectronicInvoiceItem invoiceItem = (ElectronicInvoiceItem) itemIter.next();
            if (invoiceItem != null) {
              invoiceItem.setCatalogNumber(invoiceItem.getReferenceItemIDSupplierPartID());
            }
          }
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("No Complete failure");
        }
        
        return false;
        
    }
    
    private ElectronicInvoiceRejectReasonType getRejectReasonType(String rejectReasonTypeCode){
        return matchingService.getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
    }
    
    private void rejectElectronicInvoiceFile(ElectronicInvoiceLoad eInvoiceLoad, 
                                             String fileDunsNumber, 
                                             File filename, 
                                             String rejectReasonTypeCode) {

        rejectElectronicInvoiceFile(eInvoiceLoad,fileDunsNumber,filename,null,rejectReasonTypeCode);
    }
    
    private void rejectElectronicInvoiceFile(ElectronicInvoiceLoad eInvoiceLoad, 
                                             String fileDunsNumber, 
                                             File invoiceFile, 
                                             String extraDescription,
                                             String rejectReasonTypeCode) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Rejecting the entire invoice file - " + invoiceFile.getName());
        }
        
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary = getOrCreateLoadSummary(eInvoiceLoad, fileDunsNumber);
        eInvoiceLoadSummary.addFailedInvoiceOrder();
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
        
        ElectronicInvoiceRejectDocument eInvoiceRejectDocument = null;
        try {
            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");
            
            eInvoiceRejectDocument.setInvoiceProcessDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            eInvoiceRejectDocument.setVendorDunsNumber(fileDunsNumber);
            eInvoiceRejectDocument.setDocumentCreationInProgress(true);
            
            if (invoiceFile != null){
                eInvoiceRejectDocument.setInvoiceFileName(invoiceFile.getName());
            }
            
            List<ElectronicInvoiceRejectReason> list = new ArrayList<ElectronicInvoiceRejectReason>(1);
            
            String message = "Complete failure document has been created for the Invoice with Filename '" + invoiceFile.getName() + "' due to the following error:\n";
            emailTextErrorList.append(message);
            
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(rejectReasonTypeCode,extraDescription, invoiceFile.getName());
            list.add(rejectReason);
            
            emailTextErrorList.append("    - " + rejectReason.getInvoiceRejectReasonDescription());
            emailTextErrorList.append("\n\n");
            
            eInvoiceRejectDocument.setInvoiceRejectReasons(list);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription("Complete failure");
            
            String noteText = "Invoice file";
            attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument,invoiceFile,noteText);
            
            eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);
            
        }catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        if (LOG.isDebugEnabled()){
            LOG.debug("Complete failure document has been created (DocNo:" + eInvoiceRejectDocument.getDocumentNumber() + ")");
        }
    }
    
    private void attachInvoiceXMLWithRejectDoc(ElectronicInvoiceRejectDocument eInvoiceRejectDocument,
                                               File attachmentFile,
                                               String noteText){
        
        Note note;
        try {
            note = SpringContext.getBean(DocumentService.class).createNoteFromDocument(eInvoiceRejectDocument, noteText);
        }catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        
        String attachmentType = null;
        BufferedInputStream fileStream = null;
        try {
            fileStream = new BufferedInputStream(new FileInputStream(attachmentFile));
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        Attachment attachment = null;
        try {
            attachment = KNSServiceLocator.getAttachmentService().createAttachment(eInvoiceRejectDocument, attachmentFile.getName(),INVOICE_FILE_MIME_TYPE , (int)attachmentFile.length(), fileStream, attachmentType);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            if (fileStream != null){
                try {
                    fileStream.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        note.setAttachment(attachment);
        attachment.setNote(note);
        
        eInvoiceRejectDocument.getDocumentHeader().addNote(note);
    }
    
    public ElectronicInvoiceRejectDocument createRejectDocument(ElectronicInvoice eInvoice,
                                                                ElectronicInvoiceOrder electronicInvoiceOrder,
                                                                ElectronicInvoiceLoad eInvoiceLoad) {

        if (LOG.isDebugEnabled()){
            LOG.debug("Creating reject document [DUNS=" + eInvoice.getDunsNumber() + ",POID=" + electronicInvoiceOrder.getInvoicePurchaseOrderID() + "]");
        }

        ElectronicInvoiceRejectDocument eInvoiceRejectDocument;
        
        try {

            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");

            eInvoiceRejectDocument.setInvoiceProcessDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            String rejectdocDesc = generateRejectDocumentDescription(eInvoice,electronicInvoiceOrder);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription(rejectdocDesc);
            eInvoiceRejectDocument.setDocumentCreationInProgress(true);
            
            eInvoiceRejectDocument.setFileLevelData(eInvoice);
            eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, electronicInvoiceOrder);

            String noteText = "Invoice file";
            attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument, getInvoiceFile(eInvoice.getFileName()), noteText);

            eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);
            
        }catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Reject document has been created (DocNo=" + eInvoiceRejectDocument.getDocumentNumber() + ")");
        }
        
        emailTextErrorList.append("An Invoice from file '" + eInvoice.getFileName() + "' has been rejected due to the following errors:\n");
        for (Iterator iter = eInvoiceRejectDocument.getInvoiceRejectReasons().iterator(); iter.hasNext();) {
          ElectronicInvoiceRejectReason reason = (ElectronicInvoiceRejectReason) iter.next();
          emailTextErrorList.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
        }
        emailTextErrorList.append("\n\n");
        
        return eInvoiceRejectDocument;
    }
    
    private String generateRejectDocumentDescription(ElectronicInvoice eInvoice,
                                                     ElectronicInvoiceOrder electronicInvoiceOrder){
        
        String poID = StringUtils.isEmpty(electronicInvoiceOrder.getInvoicePurchaseOrderID()) ?
                      "UNKNOWN" :
                      electronicInvoiceOrder.getInvoicePurchaseOrderID();
        
        String vendorName = StringUtils.isEmpty(eInvoice.getVendorName()) ? 
                            "UNKNOWN" :
                            eInvoice.getVendorName();
        
        String description = "PO: " + poID + " Vendor: " + vendorName;
        
        return checkDescriptionLengthAndStripIfNeeded(description);
    }
    
    private String generateRejectDocumentDescription(ElectronicInvoiceRejectDocument rejectDoc) {

        String poID = StringUtils.isEmpty(rejectDoc.getInvoicePurchaseOrderNumber()) ? 
                      "UNKNOWN" : 
                      rejectDoc.getInvoicePurchaseOrderNumber();

        String vendorName = "UNKNOWN";
        if (rejectDoc.getVendorDetail() != null){
            vendorName = rejectDoc.getVendorDetail().getVendorName();
        }

        String description = "PO: " + poID + " Vendor: " + vendorName;

        return checkDescriptionLengthAndStripIfNeeded(description);
    }
    
    private String checkDescriptionLengthAndStripIfNeeded(String description){
        
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();

        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }
        
        return description;
    }
    
    public ElectronicInvoiceLoadSummary getOrCreateLoadSummary(ElectronicInvoiceLoad eInvoiceLoad,
                                                               String fileDunsNumber){
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;
        
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }
        
        return eInvoiceLoadSummary;
        
    }
    
    public ElectronicInvoice loadElectronicInvoice(String filename)
    throws CxmlParseException {
        
      File invoiceFile = new File(filename);
      return loadElectronicInvoice(invoiceFile);
      
    }
    
    public ElectronicInvoice loadElectronicInvoice(File invoiceFile)
    throws CxmlParseException {
      
      if (LOG.isDebugEnabled()){
          LOG.debug("Loading Invoice File");
      }
      
      BufferedInputStream fileStream = null;
      try {
          fileStream = new BufferedInputStream(new FileInputStream(invoiceFile));
      }catch (FileNotFoundException e) {
          /**
           * This never happen since we're getting the file name from the existing one
           */
          throw new RuntimeException(invoiceFile.getAbsolutePath() + " not available");
      }

      ElectronicInvoice electronicInvoice = null;
      
      try {
          
          byte[] fileByteContent = IOUtils.toByteArray(fileStream);
          electronicInvoice = (ElectronicInvoice) batchInputFileService.parse(electronicInvoiceInputFileType, fileByteContent);
          
      }catch (IOException e) {
          throw new CxmlParseException(e.getMessage());
      }catch (XMLParseException e) {
          throw new CxmlParseException(e.getMessage());
      }finally{
          try {
              if (fileStream != null){
                  fileStream.close();
              }
          }catch (IOException e1) {
              e1.printStackTrace();
          }
      }
      
      if (LOG.isDebugEnabled()){
          LOG.debug("Successfully loaded the Invoice File");
      }
      
      return electronicInvoice;
      
    }
    
    private StringBuffer saveLoadSummary(ElectronicInvoiceLoad eInvoiceLoad) {

        Map savedLoadSummariesMap = new HashMap();
        StringBuffer summaryMessage = new StringBuffer();
        
        for (Iterator iter = eInvoiceLoad.getInvoiceLoadSummaries().keySet().iterator(); iter.hasNext();) {
            
            String dunsNumber = (String) iter.next();
            ElectronicInvoiceLoadSummary eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(dunsNumber);
            
              if (!eInvoiceLoadSummary.isEmpty().booleanValue()){  
                LOG.info("Saving Load Summary for DUNS '" + dunsNumber + "'");
                
                ElectronicInvoiceLoadSummary currentLoadSummary = saveElectronicInvoiceLoadSummary(eInvoiceLoadSummary);
                
                summaryMessage.append("DUNS Number - " + eInvoiceLoadSummary.getVendorDescriptor() + ":\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadSuccessCount() + " successfully processed invoices for a total of $ " + eInvoiceLoadSummary.getInvoiceLoadSuccessAmount().doubleValue() + "\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadFailCount() + " rejected invoices for an approximate total of $ " + eInvoiceLoadSummary.getInvoiceLoadFailAmount().doubleValue() + "\n");
                summaryMessage.append("\n\n");
                
                savedLoadSummariesMap.put(currentLoadSummary.getVendorDunsNumber(), eInvoiceLoadSummary);
                
            } else {
                LOG.info("Not saving Load Summary for DUNS '" + dunsNumber + "' because empty indicator is '" + eInvoiceLoadSummary.isEmpty().booleanValue() + "'");
            }
        }
        
        summaryMessage.append("\n\n");
        
        for (Iterator rejectIter = eInvoiceLoad.getRejectDocuments().iterator(); rejectIter.hasNext();) {
            ElectronicInvoiceRejectDocument rejectDoc = (ElectronicInvoiceRejectDocument) rejectIter.next();
            routeRejectDocument(rejectDoc,savedLoadSummariesMap);
        }
        
        /**
         * Even if there is an exception in the reject doc routing, all the files marked as reject will
         * be moved to the reject dir
         */
        moveFileList(eInvoiceLoad.getRejectFilesToMove());
        
        return summaryMessage;
    }
    
    private void routeRejectDocument(ElectronicInvoiceRejectDocument rejectDoc,
                                     Map savedLoadSummariesMap){
        
        LOG.info("Saving Invoice Reject for DUNS '" + rejectDoc.getVendorDunsNumber() + "'");
        
        if (savedLoadSummariesMap.containsKey(rejectDoc.getVendorDunsNumber())) {
            rejectDoc.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(rejectDoc.getVendorDunsNumber()));
        }
        else {
            rejectDoc.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(UNKNOWN_DUNS_IDENTIFIER));
        }
        
        try{
            KNSServiceLocator.getDocumentService().routeDocument(rejectDoc, "Routed by electronic invoice batch job", null);
        }
        catch (WorkflowException e) {
            e.printStackTrace();
        }
        
    }
    
    private void sendSummary(StringBuffer message) {

        String fromMailId = SpringContext.getBean(ParameterService.class).getParameterValue(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.DAILY_SUMMARY_REPORT_FROM_EMAIL_ADDRESS);
        String toMailIds = SpringContext.getBean(ParameterService.class).getParameterValue(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.DAILY_SUMMARY_REPORT_TO_EMAIL_ADDRESSES);
        
        /**
         * TODO: check for valid mail id format (use commons validator API) 
         */
        
        if (StringUtils.isBlank(fromMailId) ||
            StringUtils.isBlank(toMailIds)){
            LOG.error("Message not sent due to invalid mail ids [From=" + StringUtils.defaultString(fromMailId) + ",To=" + StringUtils.defaultString(toMailIds) + "]");
        }else{
        
            MailMessage mailMessage = new MailMessage();
            
            mailMessage.setFromAddress(fromMailId);
            setMessageToAddressesAndSubject(mailMessage,toMailIds);
            mailMessage.setMessage(message.toString());
            
            try {
                mailService.sendMessage(mailMessage);
            }catch (InvalidAddressException e) {
                LOG.error("Invalid email address. Message not sent", e);
            }
        }
        
        /**
         * HAVE TO DELETE THIS CODE....
         */

        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter("c:\\test.txt", true));
            bw.write(message.toString());
            bw.newLine();
            bw.flush();
        }
        catch (IOException ioe) {
            LOG.error("Error writing to Email File", ioe);
        }
        finally {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (IOException ioe2) {
                }
            }
        }
        
    }
    
    private MailMessage setMessageToAddressesAndSubject(MailMessage message, String toMailIds) {
        
        String toAddressList[] = toMailIds.split(";");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }

        message.setFromAddress(toAddressList[0]); 

        String mailTitle = "E-Invoice Load Results for " + ElectronicInvoiceUtils.getDateDisplayText(new Date());
        
        if (kualiConfigurationService.isProductionEnvironment()) {
            message.setSubject(mailTitle);
        } else {
            message.setSubject(kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY) + " - " + mailTitle);
        }
        return message;
    }
    
    /**
     * This method is responsible for the matching process for a reject document
     *    
     * @return true if the matching process is succeed
     */
    public boolean doMatchingProcess(ElectronicInvoiceRejectDocument rejectDocument){

        /**
         * This is needed here since if the user changes the DUNS number.
         */
        validateVendorDetails(rejectDocument);
        
        Map itemTypeMappings = getItemTypeMappings(rejectDocument.getVendorHeaderGeneratedIdentifier(),
                                                   rejectDocument.getVendorDetailAssignedIdentifier());
        
        Map kualiItemTypes = getKualiItemTypes();

        ElectronicInvoiceOrderHolder rejectDocHolder = new ElectronicInvoiceOrderHolder(rejectDocument,itemTypeMappings,kualiItemTypes);
        matchingService.doMatchingProcess(rejectDocHolder);
        
        /**
         * Once we're through with the matching process, it's needed to check whether it's possible
         * to create PREQ for the reject doc
         */
        if (!rejectDocHolder.isInvoiceRejected()){
            validateInvoiceOrderValidForPREQCreation(rejectDocHolder);
        }
        
        return !rejectDocHolder.isInvoiceRejected();
    }
    
    public boolean createPaymentRequest(ElectronicInvoiceRejectDocument rejectDocument){
     
        if (rejectDocument.getInvoiceRejectReasons().size() > 0){
            throw new RuntimeException("Not possible to create payment request since the reject document contains " + rejectDocument.getInvoiceRejectReasons().size() + " rejects");
        }
        
        Map itemTypeMappings = getItemTypeMappings(rejectDocument.getVendorHeaderGeneratedIdentifier(),
                                                   rejectDocument.getVendorDetailAssignedIdentifier());

        Map kualiItemTypes = getKualiItemTypes();

        ElectronicInvoiceOrderHolder rejectDocHolder = new ElectronicInvoiceOrderHolder(rejectDocument,itemTypeMappings,kualiItemTypes);
        createPaymentRequest(rejectDocHolder);
        
        return !rejectDocHolder.isInvoiceRejected();
        
    }
    
    private PaymentRequestDocument createPaymentRequest(ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Creating Payment Request document");
        }
        
        GlobalVariables.getMessageList().clear();
        
        validateInvoiceOrderValidForPREQCreation(orderHolder);
        
        if (LOG.isDebugEnabled()){
            if (orderHolder.isInvoiceRejected()){
                LOG.debug("Not possible to convert einvoice details into payment request");
            }else{
                LOG.debug("Payment request document creation validation succeeded");
            }
        }
        
        if (orderHolder.isInvoiceRejected()){
            return null;
        }
        
        PaymentRequestDocument preqDoc = null;
        try {
            preqDoc = (PaymentRequestDocument) KNSServiceLocator.getDocumentService().getNewDocument("PaymentRequestDocument");
        }
        catch (WorkflowException e) {
            String extraDescription = "Error=" + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_WORKLOW_EXCEPTION,          
                                                                                            extraDescription, 
                                                                                            orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            LOG.error("Error creating Payment request document - " + e.getMessage());
            return null;
        }
        
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        if (poDoc == null){
            throw new RuntimeException("Purchase Order document (POId=" + poDoc.getPurapDocumentIdentifier() + ") does not exist in the system");
        }
        
        preqDoc.getDocumentHeader().setDocumentDescription(generatePREQDocumentDescription(poDoc));
        
        preqDoc.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
        preqDoc.setInvoiceDate(orderHolder.getInvoiceDate());
        preqDoc.setInvoiceNumber(orderHolder.getInvoiceNumber());
        preqDoc.setVendorInvoiceAmount(new KualiDecimal(orderHolder.getInvoiceNetAmount()));
        preqDoc.setAccountsPayableProcessorIdentifier("E-Invoice");
        preqDoc.setVendorCustomerNumber(orderHolder.getCustomerNumber());
        preqDoc.setCreatedByElectronicInvoice(true);
        
        //Copied from PaymentRequestServiceImpl.populatePaymentRequest()
        //set bank code to default bank code in the system parameter
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(PaymentRequestDocument.class);
        if (defaultBank != null) {
            preqDoc.setBankCode(defaultBank.getBankCode());
            preqDoc.setBank(defaultBank);
        }
        
        RequisitionDocument reqDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(poDoc.getRequisitionIdentifier());
        String reqDocInitiator = reqDoc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        try {
            Person user = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(reqDocInitiator);
            preqDoc.setProcessingCampusCode(user.getCampusCode());
        }catch(Exception e){
            String extraDescription = "Error setting processing campus code - " + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }
        
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = SpringContext.getBean(AccountsPayableService.class).expiredOrClosedAccountsList(poDoc);
        if (expiredOrClosedAccountList == null){
            expiredOrClosedAccountList = new HashMap();
        }
        
        if (LOG.isDebugEnabled()){
             LOG.debug(expiredOrClosedAccountList.size() + " accounts has been found as Expired or Closed");
        }
        
        preqDoc.populatePaymentRequestFromPurchaseOrder(orderHolder.getPurchaseOrderDocument(),expiredOrClosedAccountList);
        
        populateItemDetails(preqDoc,orderHolder);
        
        /**
         * Validate totals,paydate
         */
        //PaymentRequestDocumentRule.processCalculateAccountsPayableBusinessRules
        SpringContext.getBean(KualiRuleService.class).applyRules(new CalculateAccountsPayableEvent(preqDoc));
        
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc,true);
        
        processItemsForDiscount(preqDoc,orderHolder);
        
        if (orderHolder.isInvoiceRejected()){
            return null;
        }
        
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc,false);
        /**
         * PaymentRequestReview 
         */
        //PaymentRequestDocumentRule.processRouteDocumentBusinessRules
        SpringContext.getBean(KualiRuleService.class).applyRules(new PaymentRequestForEInvoiceEvent(preqDoc));
        
        if(GlobalVariables.getErrorMap().size() > 0){
            if (LOG.isDebugEnabled()){
                LOG.debug("***************Error in rules processing - " + GlobalVariables.getErrorMap());
            }
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, GlobalVariables.getErrorMap().toString(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }
        
        if(GlobalVariables.getMessageList().size() > 0){
            if (LOG.isDebugEnabled()){
                LOG.debug("Payment request contains " + GlobalVariables.getMessageList().size() + " warning message(s)");
                for (int i = 0; i < GlobalVariables.getMessageList().size(); i++) {
                    LOG.debug("Warning " + i + "  - " +GlobalVariables.getMessageList().get(i));
                }
            }
        }
        
        addBillToAndShipToNotes(preqDoc,orderHolder);
        
        String routingAnnotation = null;
        if (!orderHolder.isRejectDocumentHolder()){
            routingAnnotation = "Routed by electronic invoice batch job";
        }
        
        try {
            KNSServiceLocator.getDocumentService().routeDocument(preqDoc,routingAnnotation, null);
        }
        catch (WorkflowException e) {
            e.printStackTrace();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_FAILURE, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }catch(ValidationException e){
            String extraDescription = GlobalVariables.getErrorMap().toString();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }
        
        return preqDoc;
    }
    
    private void addBillToAndShipToNotes(PaymentRequestDocument preqDoc, 
                                         ElectronicInvoiceOrderHolder orderHolder){
        
        String shipToAddress = orderHolder.getInvoiceShipToAddressAsString();
        String billToAddress = orderHolder.getInvoiceBillToAddressAsString();
        
        try {
            Note noteObj = SpringContext.getBean(DocumentService.class).createNoteFromDocument(preqDoc, shipToAddress);
            preqDoc.addNote(noteObj);
         
            noteObj = SpringContext.getBean(DocumentService.class).createNoteFromDocument(preqDoc, billToAddress);
            preqDoc.addNote(noteObj);
        }catch (Exception e) {
             LOG.error("Error creating ShipTo/BillTo notes - " + e.getMessage());
        }
    }
    
    private void processItemsForDiscount(PaymentRequestDocument preqDocument, 
                                         ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Processing payment request items for discount");
        }
        
        if (!orderHolder.isItemTypeAvailableInItemMapping(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT)){
            if (LOG.isDebugEnabled()){
                LOG.debug("Skipping discount processing since there is no mapping of discount type for this vendor");
            }
            return;
        }
        
        if (orderHolder.getInvoiceDiscountAmount() == null ||
            orderHolder.getInvoiceDiscountAmount() == BigDecimal.ZERO){
            if (LOG.isDebugEnabled()){
                LOG.debug("Skipping discount processing since there is no discount amount found in the invoice file");
            }
            return;
        }
        
        KualiDecimal discountValueToUse = new KualiDecimal(orderHolder.getInvoiceDiscountAmount().negate());
        List<PaymentRequestItem> preqItems = preqDocument.getItems();
        
        boolean alreadyProcessedInvoiceDiscount = false;
        boolean hasKualiPaymentTermsDiscountItem = false;
        
        //if e-invoice amount is negative... it is a penalty and we must pay extra 
        for (int i = 0; i < preqItems.size(); i++) {
            
            PaymentRequestItem preqItem = preqItems.get(i);
            
            hasKualiPaymentTermsDiscountItem = hasKualiPaymentTermsDiscountItem || (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE,preqItem.getItemTypeCode()));
            
            /**
             * TODO: Relook into this code
             */
            if (isItemValidForUpdation(preqItem.getItemTypeCode(),ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,orderHolder)){
                
                alreadyProcessedInvoiceDiscount = true;
                
                if (StringUtils.equals(preqItem.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE)){
                    //Item is kuali payment terms discount item... must perform calculation
                    // if discount item exists on PREQ and discount dollar amount exists... use greater amount
                    if (LOG.isDebugEnabled()){
                        LOG.debug("Discount Check - E-Invoice matches PREQ item type '" + preqItem.getItemTypeCode() + "'... now checking for amount");
                    }
                    
                    KualiDecimal preqExtendedPrice = preqItem.getExtendedPrice() == null ? KualiDecimal.ZERO : preqItem.getExtendedPrice();
                    if ( (discountValueToUse.compareTo(preqExtendedPrice)) < 0 ) {
                        if (LOG.isDebugEnabled()){
                            LOG.info("Discount Check - Using E-Invoice amount (" + discountValueToUse + ") as it is more discount than current payment terms amount " + preqExtendedPrice);
                        }
                        preqItem.setItemUnitPrice(discountValueToUse.bigDecimalValue());
                        preqItem.setExtendedPrice(discountValueToUse);
                      }
                }else {
                    /**
                     * FIXME : I dont think this block will gets executed
                     */  
                    // item is not epic payment terms discount item... just add value
                    // if discount item exists on PREQ and discount dollar amount exists... use greater amount
                    if (LOG.isDebugEnabled()){
                        LOG.info("Discount Check - E-Invoice matches PREQ item type '" + preqItem.getItemTypeCode() + "'");
                        LOG.info("Discount Check - Using E-Invoice amount (" + discountValueToUse + ") as it is greater than payment terms amount");
                    }
                    preqItem.addToUnitPrice(discountValueToUse.bigDecimalValue());
                    preqItem.addToExtendedPrice(discountValueToUse);
                  }
                }
         }
        
        /*
         *   If we have not already processed the discount amount then the mapping is pointed
         *   to an item that is not in the PREQ item list
         *   
         *   FYI - FILE DISCOUNT AMOUNT CURRENTLY HARD CODED TO GO INTO PAYMENT TERMS DISCOUNT ITEM ONLY... ALL OTHERS WILL FAIL
         */
        
        /**
         * TODO: Relook into this code
         */
        if (!alreadyProcessedInvoiceDiscount) {
            String itemTypeRequired = PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE;
            // if we already have a PMT TERMS DISC item but the e-invoice discount wasn't processed... error out
            // if the item mapping for e-invoice discount item is not PMT TERMS DISC item and we haven't processed it... error out
            
            if (hasKualiPaymentTermsDiscountItem || 
                !orderHolder.isItemTypeAvailableInItemMapping(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT)) {
                ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_DISCOUNT_ERROR, null, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
                return;
            }
            else {
                PaymentRequestItem newItem = new PaymentRequestItem();
                newItem.setItemUnitPrice(discountValueToUse.bigDecimalValue());
                newItem.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE);
                newItem.setExtendedPrice(discountValueToUse);
                preqDocument.addItem(newItem);
            }
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Completed processing payment request items for discount");
        }
        
    }
    
    private void populateItemDetails(PaymentRequestDocument preqDocument, ElectronicInvoiceOrderHolder orderHolder) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Populating invoice order items into the payment request document");
        }

        List<PurApItem> preqItems = preqDocument.getItems();

        boolean hasShippingItem = false;
        for (int i = 0; i < preqItems.size(); i++) {

            PaymentRequestItem preqItem = (PaymentRequestItem) preqItems.get(i);

            if (isItemValidForUpdation(preqItem.getItemTypeCode(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX, orderHolder)) {
                processTaxItem(preqItem, orderHolder);
            } else if (isItemValidForUpdation(preqItem.getItemTypeCode(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING, orderHolder)) {
                processShippingItem(preqItem, orderHolder);
            } else if (isItemValidForUpdation(preqItem.getItemTypeCode(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING, orderHolder)) {
                processSpecialHandlingItem(preqItem, orderHolder);
            } else if (isItemValidForUpdation(preqItem.getItemTypeCode(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DEPOSIT, orderHolder)) {
                processDepositItem(preqItem, orderHolder);
            } else if (isItemValidForUpdation(preqItem.getItemTypeCode(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DUE, orderHolder)) {
                processDueItem(preqItem, orderHolder);
            }else if (StringUtils.equals(preqItem.getItemTypeCode(), PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) {
                  processAboveTheLineItem(preqItem, orderHolder);
            }
            
            setItemDefaultDescription(preqItem);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Successfully populated the invoice order items");
        }

    }
    
    private void processAboveTheLineItem(PaymentRequestItem purapItem,
                                         ElectronicInvoiceOrderHolder orderHolder){

        if (LOG.isDebugEnabled()){
            LOG.debug("Processing above the line item");
        }
        
        ElectronicInvoiceItemHolder itemHolder = orderHolder.getItemByLineNumber(purapItem.getItemLineNumber().intValue());
        if (itemHolder == null){
            LOG.info("Electronic Invoice does not have item with Ref Item Line number " + purapItem.getItemLineNumber());
            return;
        }
        
        purapItem.setItemUnitPrice(itemHolder.getInvoiceItemUnitPrice());
        purapItem.setItemQuantity(new KualiDecimal(itemHolder.getInvoiceItemQuantity()));
        purapItem.setItemTaxAmount(new KualiDecimal(itemHolder.getTaxAmount()));
        
        if (itemHolder.getSubTotalAmount() != null && 
            itemHolder.getSubTotalAmount().compareTo(KualiDecimal.ZERO) != 0){

            purapItem.setExtendedPrice(itemHolder.getSubTotalAmount());
            
        }else{
            
            if (purapItem.getItemQuantity() != null) {
                if (LOG.isDebugEnabled()){
                    LOG.info("Item number " + purapItem.getItemLineNumber() + " needs calculation of extended " +
                             "price from quantity " + purapItem.getItemQuantity() + " and unit cost " + purapItem.getItemUnitPrice());
                }
                purapItem.setExtendedPrice(purapItem.getItemQuantity().multiply(new KualiDecimal(purapItem.getItemUnitPrice())));
              } else {
                  if (LOG.isDebugEnabled()){
                      LOG.info("Item number " + purapItem.getItemLineNumber() + " has no quantity so extended price " +
                               "equals unit price of " + purapItem.getItemUnitPrice());
                  }
                  purapItem.setExtendedPrice(new KualiDecimal(purapItem.getItemUnitPrice()));
              }
        }
        
    }
    
    private void processSpecialHandlingItem(PaymentRequestItem purapItem,
                                            ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Processing special handling item");
        }

        purapItem.addToUnitPrice(orderHolder.getInvoiceSpecialHandlingAmount());
        purapItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceSpecialHandlingAmount()));
        
        String invoiceSpecialHandlingDescription = orderHolder.getInvoiceSpecialHandlingDescription();
        
        if (StringUtils.isNotEmpty(invoiceSpecialHandlingDescription)) {
            if (StringUtils.isEmpty(purapItem.getItemDescription())) {
                purapItem.setItemDescription(invoiceSpecialHandlingDescription);
            }
            else {
                purapItem.setItemDescription(purapItem.getItemDescription() + " - " + invoiceSpecialHandlingDescription);
            }
        }
        
    }
    
    private void processTaxItem (PaymentRequestItem preqItem,
                                 ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Processing Tax Item");
        }
        
        preqItem.addToUnitPrice(orderHolder.getTaxAmount());
        preqItem.addToExtendedPrice(new KualiDecimal(orderHolder.getTaxAmount()));
        
        if (StringUtils.isNotEmpty(orderHolder.getTaxDescription())) {
            if (StringUtils.isEmpty(preqItem.getItemDescription())) {
                preqItem.setItemDescription(orderHolder.getTaxDescription());
            } else {
                preqItem.setItemDescription(preqItem.getItemDescription() + " - " + orderHolder.getTaxDescription());
            }
        }
        
    }
    
    private void processShippingItem(PaymentRequestItem preqItem,
                                                   ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Processing Shipping Item");
        }
        
        preqItem.addToUnitPrice(orderHolder.getInvoiceShippingAmount());
        preqItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceShippingAmount()));
        
        if (StringUtils.isNotEmpty(orderHolder.getInvoiceShippingDescription())) {
            if (StringUtils.isEmpty(preqItem.getItemDescription())) {
                preqItem.setItemDescription(orderHolder.getInvoiceShippingDescription());
            } else {
                preqItem.setItemDescription(preqItem.getItemDescription() + " - " + orderHolder.getInvoiceShippingDescription());
            }
        }
        
    }
    
    private void processDepositItem(PaymentRequestItem preqItem,
                                    ElectronicInvoiceOrderHolder orderHolder){

        LOG.info("Processing Deposit Item");
        
        preqItem.addToUnitPrice(orderHolder.getInvoiceDepositAmount());
        preqItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceDepositAmount()));
        
    }
    
    private void processDueItem(PaymentRequestItem preqItem,
                                ElectronicInvoiceOrderHolder orderHolder){

        LOG.info("Processing Deposit Item");
        
        preqItem.addToUnitPrice(orderHolder.getInvoiceDueAmount());
        preqItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceDueAmount()));
        
    }
    
    private void setItemDefaultDescription(PaymentRequestItem preqItem){
        
        //If description is empty and item is not type "ITEM"... use default description
        if (StringUtils.isEmpty(preqItem.getItemDescription()) &&
            !StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE, preqItem.getItemTypeCode())){
            if (ArrayUtils.contains(PurapConstants.ElectronicInvoice.ITEM_TYPES_REQUIRES_DESCRIPTION, preqItem.getItemTypeCode())){
                preqItem.setItemDescription(PurapConstants.ElectronicInvoice.DEFAULT_BELOW_LINE_ITEM_DESCRIPTION);
            }
        }
    }
    
    private boolean isItemValidForUpdation(String itemTypeCode,
                                           String invoiceItemTypeCode,
                                           ElectronicInvoiceOrderHolder orderHolder){
        
        return orderHolder.isItemTypeAvailableInItemMapping(invoiceItemTypeCode) && 
               StringUtils.equals(orderHolder.getKauliItemTypeCodeFromMappings(invoiceItemTypeCode),itemTypeCode);
    }
     
    
    private String generatePREQDocumentDescription(PurchaseOrderDocument poDocument) {
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName() + " Electronic Invoice";
        return checkDescriptionLengthAndStripIfNeeded(description);
    }
    
    /**
     * This validates an electronic invoice and makes sure it can be turned into a Payment Request
     * 
     */
    public void validateInvoiceOrderValidForPREQCreation(ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Validiting ElectronicInvoice Order to make sure that it can be turned into a Payment Request document");
        }
        
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        
        if ( poDoc == null){
            throw new RuntimeException("PurchaseOrder not available");
        }
            
        if (!poDoc.getStatusCode().equals(PurchaseOrderStatuses.OPEN)) {
            orderHolder.addInvoiceOrderRejectReason(matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_OPEN,null,orderHolder.getFileName()));
            return;
        }
        
        if (!orderHolder.isInvoiceNumberAcceptIndicatorEnabled()){
            List preqs = paymentRequestService.getPaymentRequestsByVendorNumberInvoiceNumber(poDoc.getVendorHeaderGeneratedIdentifier(), 
                                                                                             poDoc.getVendorDetailAssignedIdentifier(), 
                                                                                             orderHolder.getInvoiceNumber());
            
            if (preqs != null && preqs.size() > 0){
                ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ORDER_DUPLICATE,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_NUMBER,PurapKeyConstants.ERROR_REJECT_INVOICE_DUPLICATE);
                return;
            }
        }
        
        if (orderHolder.getInvoiceDate() == null){
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_DATE,PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_INVALID);
            return;
        }else if (orderHolder.getInvoiceDate().after(new java.util.Date())) {
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_GREATER,null,orderHolder.getFileName()); 
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_DATE,PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_GREATER);
            return;
        }
        
    }
    
    private void moveFileList(Map filesToMove) {
        for (Iterator iter = filesToMove.keySet().iterator(); iter.hasNext();) {
            File fileToMove = (File) iter.next();

            boolean success = this.moveFile(fileToMove, (String) filesToMove.get(fileToMove));
            if (!success) {
                String errorMessage = "File with name '" + fileToMove.getName() + "' could not be moved";
                throw new PurError(errorMessage);
            }
        }
    }

    private boolean moveFile(File fileForMove, String location) {
        File moveDir = new File(location);
        return fileForMove.renameTo(new File(moveDir, fileForMove.getName()));
    }

    private String getBaseDirName(){
        return electronicInvoiceInputFileType.getDirectoryPath() + File.separator;
    }
    
    private String getSourceDirName(){
        return getBaseDirName() + "source" + File.separator;
    }
    
    private String getRejectDirName(){
        return getBaseDirName() + "reject" + File.separator;
    }
    
    private String getAcceptDirName(){
        return getBaseDirName() + "accept" + File.separator;
    }
    
    private File getInvoiceFile(String fileName){
        return new File(getSourceDirName() + fileName);
    }
    
    private ElectronicInvoiceLoadSummary saveElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
        return electronicInvoicingDao.saveElectronicInvoiceLoadSummary(eils);
    }
    
    public void setElectronicInvoiceInputFileType(ElectronicInvoiceInputFileType electronicInvoiceInputFileType) {
        this.electronicInvoiceInputFileType = electronicInvoiceInputFileType;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setElectronicInvoicingDao(ElectronicInvoicingDao electronicInvoicingDao) {
        this.electronicInvoicingDao = electronicInvoicingDao;
    }
    
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setElectronicInvoiceMatchingService(ElectronicInvoiceMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }
    
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}

