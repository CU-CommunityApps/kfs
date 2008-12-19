package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.ltd.getahead.dwr.impl.DTDEntityResolver;
import uk.ltd.getahead.dwr.util.LogErrorHandler;

public class CheckModularization {
    private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/integration/SpringBeansModules.xml,org/kuali/kfs/sys/spring-sys.xml,org/kuali/kfs/coa/spring-coa.xml,org/kuali/kfs/fp/spring-fp.xml,org/kuali/kfs/gl/spring-gl.xml,org/kuali/kfs/pdp/spring-pdp.xml,org/kuali/kfs/vnd/spring-vnd.xml";

    private static final Map<String, String> OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-AR", "ar");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-BC", "bc");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CAB", "cab");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CAM", "cam");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CG", "cg");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-EC", "ec");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-LD", "ld");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-PURAP", "purap");
    }
    
    private static final Map<String, String> OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES =
            new TreeBidiMap(OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX).inverseBidiMap();
    
    private static final Map<String, String> SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-COA", "coa");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-FP", "fp");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-GL", "gl");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-PDP", "pdp");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-SYS", "sys");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-VND", "vnd");
    }
//    private String[] allContextFiles;
    private KualiModuleService kualiModuleService;
    
    private void setUp() {
        ConfigurableApplicationContext context = null;
        try {
            new ClassPathXmlApplicationContext(SpringContext.TEST_CONTEXT_DEFINITION);
            // pull the Rice application context into here for further use and efficiency
            context = RiceResourceLoaderFactory.getSpringResourceLoader().getContext();
            kualiModuleService = (KualiModuleService)context.getBean("kualiModuleService");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            stopSpring(context);
        }
    }
    
    public static void main(String[] args) {
        boolean testSucceeded = true;
        CheckModularization mt = new CheckModularization();
        try {
            mt.setUp();
            if (!(mt.testSpring() & mt.testOjb() & mt.testDwr())) {
                System.exit(1);
            }
        }
        catch (Exception e) {
            System.exit(1);
        }
        System.exit(0);
    }

    public boolean testSpring() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration:");
        List<ModuleGroup> optionalModuleGroups = retrieveOptionalModuleGroups();
        for (ModuleGroup optionalModuleGroup : optionalModuleGroups) {
            testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(optionalModuleGroup, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }

    private boolean testOptionalModuleSpringConfiguration(ModuleGroup optionalModuleGroup, StringBuffer errorMessage) {
        String springFileSuffix = OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get(optionalModuleGroup.namespaceCode);
        ClassPathXmlApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext(new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/kfs/module/").append(springFileSuffix).append("/spring-").append(springFileSuffix).append(".xml").toString().split(","));
            Map<String, DataDictionaryService> ddServiceBeans = context.getBeansOfType(DataDictionaryService.class);
            if (ddServiceBeans.size() != 1) {
                throw new RuntimeException("There should only be one DataDictionaryService bean, but " + ddServiceBeans.size() + " were found");
            }
            DataDictionaryService dataDictionaryService = ddServiceBeans.entrySet().iterator().next().getValue();
            // DO NOT allow for concurrent validation of the DD
            dataDictionaryService.getDataDictionary().parseDataDictionaryConfigurationFiles( false );
            return true;
        }
        catch (Exception e) {
            errorMessage.append("\n").append(optionalModuleGroup.namespaceCode).append("\n\t").append(e.getMessage());
            return false;
        }
        finally {
            stopSpring(context);
        }
    }

    public boolean testOjb() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in OJB configuration:");
        List<ModuleGroup> allModuleGroups = retrieveModuleGroups();
        for (ModuleGroup moduleGroup : allModuleGroups) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleGroup, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }

    private boolean testOptionalModuleOjbConfiguration(ModuleGroup moduleGroup, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedNamespaceCode : OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!(moduleGroup.namespaceCode.equals(referencedNamespaceCode) || moduleGroup.optionalModuleDependencyNamespaceCodes.contains(referencedNamespaceCode))) {
                String firstDatabaseRepositoryFilePath = kualiModuleService.getModuleService(moduleGroup.namespaceCode).getModuleConfiguration().getDatabaseRepositoryFilePaths().iterator().next();
                // the first database repository file path is typically the file that comes shipped with KFS.  If institutions override it, this unit test will not test them
                Scanner scanner = new Scanner(new File("work/src/" + firstDatabaseRepositoryFilePath));
                int count = 0;
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    String firstPackagePrefix = (kualiModuleService.getModuleService(referencedNamespaceCode).getModuleConfiguration()).getPackagePrefixes().iterator().next();
                    // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                    if (token.contains(firstPackagePrefix)) {
                        count++;
                    }
                }
                if (count > 0) {
                    if (testSucceeded) {
                        testSucceeded = false;
                        errorMessage.append("\n").append(moduleGroup.namespaceCode).append(": ");
                    }
                    else {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(count).append(" references to ").append(referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }
    
    public boolean testDwr() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in DWR configuration:");
        
        List<ModuleGroup> allModuleGroups = retrieveModuleGroups();
        for (ModuleGroup moduleGroup : allModuleGroups) {
            testSucceeded &= testDwrModuleConfiguration(moduleGroup, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }
    
    private boolean testDwrModuleConfiguration(ModuleGroup moduleGroup, StringBuffer errorMessage) throws Exception {
        List<String> dwrFiles = kualiModuleService.getModuleServiceByNamespaceCode(moduleGroup.namespaceCode).getModuleConfiguration().getScriptConfigurationFilePaths();
        boolean testSucceeded = true;
        if (dwrFiles != null && dwrFiles.size() > 0) {
            // the DWR file delivered with KFS (i.e. the base) should be the first element of the list
            String baseDwrFileName = dwrFiles.get(0);
            Document dwrDocument = generateDwrConfigDocument(baseDwrFileName);
            testSucceeded = testDwrModuleConfiguration(baseDwrFileName, dwrDocument, moduleGroup, errorMessage);
        }
        return testSucceeded;
    }
    
    private boolean testDwrModuleConfiguration(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) throws Exception {
       boolean beanClassNamesOK = testDwrBeanClassNames(dwrFileName, dwrDocument, moduleGroup, errorMessage);
       boolean springServicesOK = testDwrSpringServices(dwrFileName, dwrDocument, moduleGroup, errorMessage);
       return beanClassNamesOK && springServicesOK;
    }
    
    private boolean testDwrBeanClassNames(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        List<String> dwrBeanClassNames = retrieveDwrBeanClassNames(dwrDocument);
        for (String referencedNamespaceCode : OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!(referencedNamespaceCode.equals(moduleGroup.namespaceCode) || moduleGroup.optionalModuleDependencyNamespaceCodes.contains(referencedNamespaceCode))) {
                String firstPackagePrefix = (kualiModuleService.getModuleService(referencedNamespaceCode).getModuleConfiguration()).getPackagePrefixes().iterator().next();
                // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                if (!firstPackagePrefix.endsWith(".")) {
                    firstPackagePrefix = firstPackagePrefix + ".";
                }
                int count = 0;
                for (String className : dwrBeanClassNames) {
                    if (className.contains(firstPackagePrefix)) {
                        count++;
                    }
                }
                if (count > 0) {
                    testSucceeded = false;
                    errorMessage.append("\n").append(dwrFileName).append(" (in module ").append(moduleGroup.namespaceCode).append(") has ").append(count).append(" references to business objects from ").append(referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }
    
    private boolean testDwrSpringServices(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        boolean isSystemModule = SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.containsKey(moduleGroup.namespaceCode);
        
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        ClassPathXmlApplicationContext context = null;
        try {
            String[] configLocations = null;
            if (isSystemModule) {
                // if we're testing a system module, then we will only load up the base spring files in our new app context
                configLocations = BASE_SPRING_FILESET.split(",");
            }
            else {
                // if we're testing an optional module, then we will need to load up the base spring files as well as the optional module's beans
                String springFileSuffix = OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get(moduleGroup.namespaceCode);
                configLocations = new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/kfs/module/").append(springFileSuffix).append("/spring-").append(springFileSuffix).append(".xml").toString().split(",");
            }
            context = new ClassPathXmlApplicationContext(configLocations);
            context.refresh();
            
            List<String> serviceNames = retrieveDwrServiceNames(dwrDocument);
            for (String serviceName : serviceNames) {
                if (!context.containsBean(serviceName)) {
                    testSucceeded = false;
                    errorMessage.append("\n").append(dwrFileName).append(" (in module ").append(moduleGroup.namespaceCode).append(") has references to spring bean \"").append(serviceName).append("\" that is not defined in the base spring files");
                    if (!isSystemModule) {
                        String springFileSuffix = OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get(moduleGroup.namespaceCode);
                        errorMessage.append(" or in ").append(",org/kuali/kfs/module/").append(springFileSuffix).append("/spring-").append(springFileSuffix).append(".xml").toString().split(",");
                    }
                }
            }
        }
        catch (Exception e) {
            errorMessage.append("\n").append(moduleGroup.namespaceCode).append("\n\t").append(e.getMessage());
            return false;
        }
        finally {
            try {
                if (context != null) {
                    context.close();
                }
            }
            catch (Exception e) {
            }
            binder.unbind();
        }
        
        return testSucceeded;
    }

    
    private Document generateDwrConfigDocument(String fileName) throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(fileName).getInputStream();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new DTDEntityResolver());
        db.setErrorHandler(new LogErrorHandler());

        Document doc = db.parse(in);
        return doc;
    }
    
    private List<String> retrieveDwrServiceNames(Document dwrDocument) {
        List<String> serviceNames = new ArrayList<String>();
        // service names are in "create" elements
        Element root = dwrDocument.getDocumentElement();
        NodeList allows = root.getElementsByTagName("allow");
        for (int i = 0; i < allows.getLength(); i++) {
            Element allowElement = (Element) allows.item(i);
            NodeList creates = allowElement.getElementsByTagName("create");
            for (int j = 0; j < creates.getLength(); j++) {
                Element createElement = (Element) creates.item(j);
                if ("spring".equals(createElement.getAttribute("creator"))) {
                    NodeList params = createElement.getElementsByTagName("param");
                    for (int k = 0; k < params.getLength(); k++) {
                        Element paramElement = (Element) params.item(k);
                        if ("beanName".equals(paramElement.getAttribute("name"))) {
                            serviceNames.add(paramElement.getAttribute("value"));
                        }
                    }
                }
                
            }
        }
        return serviceNames;
    }
    
    private List<String> retrieveDwrBeanClassNames(Document dwrDocument) {
        List<String> classNames = new ArrayList<String>();
        // class names are in "convert" elements
        Element root = dwrDocument.getDocumentElement();
        NodeList allows = root.getElementsByTagName("allow");
        for (int i = 0; i < allows.getLength(); i++) {
            Element allowElement = (Element) allows.item(i);
            NodeList converts = allowElement.getElementsByTagName("convert");
            for (int j = 0; j < converts.getLength(); j++) {
                Element convertElement = (Element) converts.item(j);
                if ("bean".equals(convertElement.getAttribute("converter"))) {
                    classNames.add(convertElement.getAttribute("match"));
                }
            }
        }
        return classNames;
    }
    
    public class ModuleGroup {
        public String namespaceCode;
        public HashSet<String> optionalModuleDependencyNamespaceCodes = new HashSet<String>();
    }
    
    public List<ModuleGroup> retrieveModuleGroups() throws Exception {
        List<ModuleGroup> moduleGroups = new ArrayList<ModuleGroup>();
        
        for (String systemNamespaceCode : SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            ModuleGroup systemModuleGroup = new ModuleGroup();
            systemModuleGroup.namespaceCode = systemNamespaceCode;
            moduleGroups.add(systemModuleGroup);
        }
        
        moduleGroups.addAll(retrieveOptionalModuleGroups());
        
        return moduleGroups;
    }
    
    public List<ModuleGroup> retrieveOptionalModuleGroups() throws Exception {
        Document designXmlDocument = getDesignXmlDocument();
        List<Element> optionalModuleDefinitions = retrieveOptionalModuleDefinitions(designXmlDocument);
        List<ModuleGroup> optionalModuleGroups = new ArrayList<ModuleGroup>();
        
        for (Element optionalModuleDefinition : optionalModuleDefinitions) {
            ModuleGroup optionalModuleGroup = buildOptionalModuleGroup(optionalModuleDefinition);
            if (optionalModuleGroup != null) {
                optionalModuleGroups.add(optionalModuleGroup);
            }
        }
        
        return optionalModuleGroups;
    }
    
    public Document getDesignXmlDocument() throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(DefaultResourceLoader.CLASSPATH_URL_PREFIX + "design.xml").getInputStream();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(in);
        return doc;
    }
    
    public List<Element> retrieveOptionalModuleDefinitions(Document designXmlDocument) throws Exception {
        List<Element> optionalModuleDefinitions = new ArrayList<Element>();
        Element root = designXmlDocument.getDocumentElement();
        
        // in the design.xml file, an optional module/package is specified by a <package> tag that does not have the needdeclarations attribute equal false
        NodeList packages = root.getElementsByTagName("package");
        for (int i = 0; i < packages.getLength(); i++) {
            Element packageElement = (Element) packages.item(i);
            if (!"false".equals(packageElement.getAttribute("needdeclarations"))) {
                optionalModuleDefinitions.add(packageElement);
            }
        }
        return optionalModuleDefinitions;
    }
    
    public ModuleGroup buildOptionalModuleGroup(Element optionalPackageElement) {
        ModuleGroup moduleGroup = null;
        if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(optionalPackageElement.getAttribute("name"))) {
            moduleGroup = new ModuleGroup();
            moduleGroup.namespaceCode = OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(optionalPackageElement.getAttribute("name"));
            if (StringUtils.isNotBlank(optionalPackageElement.getAttribute("depends"))) {
                if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(optionalPackageElement.getAttribute("depends"))) {
                    moduleGroup.optionalModuleDependencyNamespaceCodes.add(OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(optionalPackageElement.getAttribute("depends")));
                }
            }
            NodeList dependsElements = optionalPackageElement.getElementsByTagName("depends");
            for (int i = 0; i < dependsElements.getLength(); i++) {
                Element dependsElement = (Element) dependsElements.item(i);
                if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(StringUtils.trim(dependsElement.getTextContent()))) {
                    moduleGroup.optionalModuleDependencyNamespaceCodes.add(OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(StringUtils.trim(dependsElement.getTextContent())));
                }
            }
        }
        return moduleGroup;
    }
    
    private void stopSpring(ConfigurableApplicationContext context) {
        try {
            if (context != null) {
                context.close();
            }
        }
        catch (Exception e) {
            System.out.println("Caught exception shutting down spring");
            e.printStackTrace();
        }
    }
}
