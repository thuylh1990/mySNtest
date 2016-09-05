/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.cyberspace.safenetselenium.safenetfiltering;

import vn.cyberspace.safenetselenium.Utils.ConfigFile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author thuylh
 */
public class SafeNetFiltering {
    private String safenet_matched_pattern;
    private ConfigFile config = new ConfigFile("web.properties");

    public String getMatched_pattern() {
        return safenet_matched_pattern;
    }

    public void setMatched_pattern(String matched_pattern) {
        this.safenet_matched_pattern = matched_pattern;
    }
    
    public SafeNetFiltering(){
        
        this.safenet_matched_pattern = config.getConfig("safenet_matched_pattern");
       
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
        //Create printWriter
        String attached_file = new SimpleDateFormat("yyyyMMddhhmm'" + ".log" + "'").format(new Date());
        PrintWriter writer = new PrintWriter(attached_file);

        //Create HTTP Object
        HTTPURL res = new HTTPURL(writer);

        //Create Report Testcase
        Report.TestCase testcase = new Report.TestCase();
        testcase.testcasename = "Filtering domain Testing";
        testcase.summary = "To examine filtering function working correctly";
        testcase.isPassed = false;

        //Read domain from file
        List<String> domains = new ArrayList<String>();
        domains = readFromFile("list3.txt");

        //Create array to store result domain list
        List<String> failedDomains = new ArrayList<String>();
        final List<String> passedDomains = new ArrayList<String>();
        List<String> errorDomains = new ArrayList<String>();

        for (String domain : domains) {
            String buff = new String();

            //buff = res.getUnicodeContent("file:///E:/A-Work-FW/Tester/AutomationTest/SafeNetFiltering/vechai.info.html");
            buff = res.getContentBySocket(domain.trim());
            if (buff == null) {
                System.out.println("Cant read the content of domain " + domain);
                writer.println("Cant read the content of domain" + domain);
                testcase.note = "FAIL! Cant read the content of domain" + domain;
                errorDomains.add(domain);
            } else {
                int found = buff.indexOf("đã bị chặn bởi <strong>SafeNet</strong> để bảo vệ bạn khỏi những nội dung không phù hợp.</p><p>Chặn theo nhóm:");
                if (found > 0) {
                    System.out.println(domain + " has contained modifying content => PASS!");
                    writer.println(domain + " has contained modifying content => PASS!");
                    passedDomains.add(domain);
                } else {
                    failedDomains.add(domain);
                    System.out.println(domain + " has NOT contained modifying content => FAIL!");
                    writer.println(domain + " has NOT contained modifying content => FAIL!");
                }
            }
        }

        System.out.println("======================================");

        if (failedDomains.size() == 0) {
            testcase.isPassed = true;
            testcase.note = "PASS!";
            System.out.println("PASS!");
        } else {
            testcase.note = "FAIL!";
            System.out.println("FAIL!");
        }
        
        int result = 0;
        Report.ReturnValues_write2File returnValue = new Report.ReturnValues_write2File();
        
        returnValue = writeToFile(failedDomains, "failedDomains.txt");
        result = returnValue.result;
        
        if(result == -1){
            writer.println("Ghi file failedDomain lỗi!");
        }
        
        returnValue= writeToFile(errorDomains, "errorDomains.txt");
        result = returnValue.result;
        
        if(result == -1){
            writer.println("Ghi file errorDomain lỗi!");
        }
        
        returnValue = writeToFile(passedDomains, "passedDomains.txt");
        result = returnValue.result;
        
        if(result == -1){
            writer.println("Ghi file passedDomain lỗi!");
        }

        writer.close();

    }
    
    
    public Report.ReturnValues_write2File run(String domains_file) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
        
        Report.ReturnValues_write2File _result = new Report.ReturnValues_write2File();
        
        _result.result = -1;
        _result.string = "";
        
        PrintWriter writer = null;
        try{
        //Create printWriter
        String log_file = new SimpleDateFormat("yyyyMMddhhmm'" + ".log" + "'").format(new Date());
        writer = new PrintWriter(log_file);

        //Create HTTP Object
        HTTPURL res = new HTTPURL(writer);

        //Create Report Testcase
        Report.TestCase testcase = new Report.TestCase();
        testcase.testcasename = "Filtering domain Testing";
        testcase.summary = "To examine filtering function working correctly";
        testcase.isPassed = false;

        //Read domain from file
        List<String> domains = new ArrayList<String>();
        domains = readFromFile(domains_file);

        //Create array to store result domain list
        List<String> unblocked_Domains = new ArrayList<String>();
        List<String> blocked_Domains = new ArrayList<String>();
        List<String> nonExisted_Domains = new ArrayList<String>();

        for (String domain : domains) {
            String buff = new String();

            //buff = res.getUnicodeContent("file:///E:/A-Work-FW/Tester/AutomationTest/SafeNetFiltering/vechai.info.html");
            buff = res.getContentBySocket(domain.trim());
            if (buff == null) {
                System.out.println("Cant read the content of domain " + domain);
                writer.println("Cant read the content of domain" + domain);
                testcase.note = "FAIL! Cant read the content of domain" + domain;
                nonExisted_Domains.add(domain);
            } else {
                int found = buff.indexOf(this.safenet_matched_pattern);
                if (found > 0) {
                    System.out.println(domain + " has contained modifying content => PASS!");
                    writer.println(domain + " has contained modifying content => PASS!");
                    blocked_Domains.add(domain);
                } else {
                    unblocked_Domains.add(domain);
                    System.out.println(domain + " has NOT contained modifying content => FAIL!");
                    writer.println(domain + " has NOT contained modifying content => FAIL!");
                }
            }
        }

        System.out.println("======================================");

        if (unblocked_Domains.size() == 0) {
            testcase.isPassed = true;
            testcase.note = "PASS!";
            System.out.println("PASS!");
        } else {
            testcase.note = "FAIL!";
            System.out.println("FAIL!");
        }
        
        int result = 0;
        Report.ReturnValues_write2File returnValue;
        
        returnValue = writeToFile(unblocked_Domains, "unblocked_Domains.txt");
        result = returnValue.result;
        config.setConfig("unblocked_Domains", returnValue.string);
        
        if(result == -1){
            writer.println("Ghi file failedDomain lỗi!");
        }
        
        returnValue = writeToFile(nonExisted_Domains, "nonExisted_Domains.txt");
        result = returnValue.result;
        config.setConfig("nonExisted_Domains", returnValue.string);
        
        
        if(result == -1){
            writer.println("Ghi file errorDomain lỗi!");
        }
        
        returnValue = writeToFile(blocked_Domains, "blocked_Domains.txt");
        result = returnValue.result;
        config.setConfig("blocked_Domains", returnValue.string);
        
        
        if(result == -1){
            writer.println("Ghi file passedDomain lỗi!");
        }

        writer.close();
        }catch(IOException | InterruptedException | ExecutionException e){
            System.out.println(e.toString());
        }
        return _result;
    }
    

    public static List<String> readFromFile(String filename) throws FileNotFoundException {
        List<String> queries = new ArrayList<String>();

        BufferedReader br = null;

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                queries.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (queries != null) {
            return queries;
        }
        return null;
    }

    private static Report.ReturnValues_write2File writeToFile(List<String> domainlist, String outputFileName) {
        Report.ReturnValues_write2File result = new Report.ReturnValues_write2File();
        String prefix = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
        String outputFile = prefix + "_" + outputFileName;
        
        result.result = -1;
        result.string = outputFile;
        try{
        
            PrintWriter os = new PrintWriter(outputFile, "UTF-8");
            for(String domain: domainlist){
                os.println(domain);
            }
            os.close();       
        
        }catch(IOException e){
            result.result = -1;
            return result;
        }
        
        return result;
        
        

    }
}
