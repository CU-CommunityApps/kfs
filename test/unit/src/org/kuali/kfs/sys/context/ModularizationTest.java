package org.kuali.kfs.sys.context;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.kuali.core.authorization.KualiModuleAuthorizerBase;
import org.kuali.core.service.KualiModuleService;
import org.kuali.kfs.sys.ConfigureContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ModularizationTest extends KualiTestBase {
    private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/sys/KualiSpringBeansKfs.xml,org/kuali/kfs/integration/SpringBeansModules.xml,org/kuali/kfs/coa/KualiSpringBeansChart.xml,org/kuali/kfs/fp/KualiSpringBeansFinancial.xml,org/kuali/kfs/gl/KualiSpringBeansGl.xml,org/kuali/kfs/pdp/KualiSpringBeansPdp.xml,org/kuali/kfs/vnd/KualiSpringBeansVendor.xml";
    private static final Map<String,String> OPTIONAL_MODULE_IDS = new HashMap<String,String>();
    static {
        OPTIONAL_MODULE_IDS.put("ar", "KualiSpringBeansAr.xml");
        OPTIONAL_MODULE_IDS.put("bc", "KualiSpringBeansBudget.xml");
        OPTIONAL_MODULE_IDS.put("cab", "KualiSpringBeansCapitalAssetBuilder.xml");
        OPTIONAL_MODULE_IDS.put("cam", "KualiSpringBeansCams.xml");
        OPTIONAL_MODULE_IDS.put("cg", "KualiSpringBeansCg.xml");
        OPTIONAL_MODULE_IDS.put("ec", "KualiSpringBeansEffort.xml");
        OPTIONAL_MODULE_IDS.put("ld", "KualiSpringBeansLabor.xml");
        OPTIONAL_MODULE_IDS.put("purap", "KualiSpringBeansPurap.xml");
    }
    private static final Set<String> SYSTEM_MODULE_IDS = new HashSet<String>();
    static {
        SYSTEM_MODULE_IDS.add("coa");
        SYSTEM_MODULE_IDS.add("fp");
        SYSTEM_MODULE_IDS.add("gl");
        SYSTEM_MODULE_IDS.add("pdp");
        SYSTEM_MODULE_IDS.add("sys");
        SYSTEM_MODULE_IDS.add("vnd");
    }
    private KualiModuleService moduleService;

//    public void testSpring() throws Exception {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(BASE_SPRING_FILESET.split(","));
//        moduleService = (KualiModuleService)context.getBean("kualiModuleService");
//        context.close();
//        boolean testSucceeded = true;
//        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration:");
//        for (String moduleId : OPTIONAL_MODULE_IDS.keySet()) {
//            testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(moduleId, errorMessage);
//        }
//        System.out.print(errorMessage.toString());
//        assertTrue(errorMessage.toString(), testSucceeded);
//    }
    
    private boolean testOptionalModuleSpringConfiguration(String moduleId, StringBuffer errorMessage) {
        ClassPathXmlApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext(new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/kfs/module/").append(moduleId).append("/").append(OPTIONAL_MODULE_IDS.get(moduleId)).toString().split(","));
            return true;
        }
        catch (Exception e) {
            errorMessage.append("\n").append(moduleId).append("\n\t").append(e.getMessage());
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
        }
    }
    
    @ConfigureContext
    public void testOjb() throws Exception {
        moduleService = SpringContext.getBean(KualiModuleService.class);
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in OJB configuration:");
        HashSet<String> allModuleIds = new HashSet();
        for(String moduleId : SYSTEM_MODULE_IDS) {
            allModuleIds.add(moduleId);
        }
        for(String moduleId : OPTIONAL_MODULE_IDS.keySet()) {
            allModuleIds.add(moduleId);
        }
        for (String moduleId : allModuleIds) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleId, errorMessage);
        }
        assertTrue(errorMessage.toString(), testSucceeded);
    }

    private boolean testOptionalModuleOjbConfiguration(String moduleId, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedModuleId : OPTIONAL_MODULE_IDS.keySet()) {
            if (!moduleId.equals(referencedModuleId)) {
                Scanner scanner = null;
                scanner = new Scanner("work/src/" + moduleService.getModule(referencedModuleId).getDatabaseRepositoryFilePaths().iterator().next());
                scanner.useDelimiter(" ");
                int count = 0;
                while (scanner.hasNext()) {
                    if (scanner.next().contains(((KualiModuleAuthorizerBase) moduleService.getModule(referencedModuleId).getModuleAuthorizer()).getPackagePrefixes().iterator().next())) {
                        count++;
                    }
                }
                if (count > 0) {
                    if (testSucceeded) {
                        testSucceeded = false;
                        errorMessage.append("\n").append(moduleId).append(": ");
                    }
                    else {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(count).append(" references to ").append(referencedModuleId);
                }
            }
        }
        return testSucceeded;
    }
}
