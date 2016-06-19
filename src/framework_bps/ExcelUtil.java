package framework_bps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.relevantcodes.extentreports.ExtentTest;

import businessComponents.Keywords;
import freemarker.log.Logger;


public class ExcelUtil {
	
	static String testCaseName, iterationMode, strCurrentKeyword, strTestCaseDescription;
	static Sheet currentSheet, dataSheet;
	static int sheet_rowCount,IterationStartRow_TestData,IterationCrntRow_TestData,subIterationRow_TestData ,startIteration, endIteration, currentIteration, intSubIterations, keywordCount, runSubiterationCount, intMasterTDRow,intBPSsubIteration;
	static Row keywordRow, dataRow;
	static String[] arrCurrentFlowData;
	static List<String> groupedKeywords;
	//static boolean breakWhileLoop;
	static Workbook RunManager,TestData;
	static Sheet main, currentSheet_key, currentSheet_Data;
	static String currentSheetName;
	static WebDriver driver;
	static ExtentTest ChildTest,IterationTest;
	static Keywords B_lib;
	static String ResultFolder;


	public static Workbook connectXl(String Wname){
		Workbook xl=null;		
		try {
			File xlFile = new File(System.getProperty("user.dir")+"\\Files\\" + Wname);
			FileInputStream fis = new FileInputStream(xlFile);
			xl = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			LogFW.warning("File not found " + Wname);
			//System.out.println("File not found " + Wname);
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IO exception while opening XSSF workbook");
			LogFW.warning("IO exception while opening XSSF workbook");
			e.printStackTrace();
		}
		return xl;
	}
	

	public static void executeSheet(Sheet exSheet){
		currentSheet=exSheet;
		//System.out.println("Sheet Name is " + currentSheet.getSheetName().toString());
		sheet_rowCount=currentSheet.getLastRowNum()-currentSheet.getFirstRowNum();
		for (int i=1; i<=sheet_rowCount;i++){
			if(currentSheet.getRow(i).getCell(1).toString().equalsIgnoreCase("True")){
				testCaseName=currentSheet.getRow(i).getCell(0).toString();
				strTestCaseDescription = currentSheet.getRow(i).getCell(2).toString();
				keywordRow=currentSheet.getRow(i);
				
				//@ Start Reporter
				Reporter.StartReporterTest(testCaseName, strTestCaseDescription);
				Reporter.setMODULE(currentSheet.getSheetName().toString());
				//@ Start cases execution 
				executeTestCase();
				
				//@ End Reporter
				Reporter.endTest();
			}
		}
		LogFW.log("@@Module {" + currentSheet.getSheetName() + "} Execution Completed");
	}
	
	public static boolean setIterationRow(){
		int intUsedRow=dataSheet.getLastRowNum()-dataSheet.getFirstRowNum();
		boolean flag=false;
		for (int sb=1; sb<=intUsedRow; sb++){
			if (dataSheet.getRow(sb).getCell(0).toString().equalsIgnoreCase(testCaseName) && dataSheet.getRow(sb).getCell(1).getNumericCellValue()==currentIteration){
				//System.out.println("Data Row pointer at " + sb + " ,Testcases is " + testCaseName + " , itration is " + currentIteration);
				LogFW.log("Test Data Row is Pointing at {" + sb + "} ,TESTCASE {" + testCaseName + "} , ITERATION {" + currentIteration + "}");
				IterationCrntRow_TestData=sb;
				flag=true;
				break;
			}
		}
		return flag;
	}
	

	public static void executeTestCase(){
		
		LogFW.log("TEST CASE under Execution [" + testCaseName +"]");
		
		//-----------------------
		readIteration();
		setDataRow();
		//----------------------
		
		IterationCrntRow_TestData=IterationStartRow_TestData;
		subIterationRow_TestData = IterationCrntRow_TestData;
		
		while(currentIteration <= endIteration){
			//logic to validate incorrect iteration row
			if(!dataSheet.getRow(IterationCrntRow_TestData).getCell(0).toString().equalsIgnoreCase(testCaseName)){
				if (iterationMode.equalsIgnoreCase("Run from <Start Iteration> to <End Iteration>") || currentIteration==1){
					LogFW.log("Iteration started (" + currentIteration + ")");
					LogFW.warning("No test data found for this test case iteration ! All subsequent iterations aborted");
					LogFW.log("Iteration Completed (" + currentIteration + ")");
				}
				break;
			}
			
			//set iteration row number and check for correct data row
			if (!setIterationRow()){
				if (endIteration==65535){
					LogFW.warning("ITERATION COMPLETED - NO TEST DATA ROW AVAILABLE");
					break;
				}else{
					LogFW.warning("Test Data row not found for Iteration {" + currentIteration + "} , ! All subsequent iterations aborted");
					break;
				}
			}
			
			LogFW.log("ITERATION STARTED [" + currentIteration + "]");
			//IterationTest = Reporter.StartIteration_ReporterTest("Iteration " + Integer.toString(currentIteration));
			//ChildTest = Reporter.StartChild_ReporterTest("Iteration " + Integer.toString(currentIteration));
			//run loop for available keywords
			keywordCount=keywordRow.getLastCellNum()-6;
			
			//KEYWORDS LOOOP----------------------------------------------------------------------
			groupedKeywords = new ArrayList<String>();
			for (int k=6;k<=keywordCount;k++){
				if (!keywordRow.getCell(k).toString().isEmpty()){					
					arrCurrentFlowData = keywordRow.getCell(k).toString().split(",");
					strCurrentKeyword=arrCurrentFlowData[0];
					//intBPSsubIteration=0;
						//NO SUB ITERATION KEYWORD , without [,]
						if (arrCurrentFlowData.length==1){
							intSubIterations=1;
							groupedKeywords.add(strCurrentKeyword);
							intMasterTDRow=IterationCrntRow_TestData;
							
							//LogFW.log("Master test data Row " + intMasterTDRow);
							LogFW.log("TEST_DATA_ROW {" + IterationCrntRow_TestData + "}   , TEST_CASE {" + testCaseName + "}, ITERATION {" + currentIteration +"}"  );
					
							//EXECUTION OF NON SUB ITERATION KEYWORDS
							for (String subg: groupedKeywords){
								//LogFW.log(subg);
								executeKeyword(subg);
							}
							
							groupedKeywords.clear();
							
						//SUB ITERATION KEYWORDS, 	WITH [,]	
						}else{
							intSubIterations= Integer.valueOf(arrCurrentFlowData[1]);
							
							//GROUPING KEYWORDS FOR SAME SUBITERATION
							if (intSubIterations==0){
								groupedKeywords.add(strCurrentKeyword);
							
							//EXECUTION OF GROUPED KEYWORDS
							}else if (intSubIterations>0){
								groupedKeywords.add(strCurrentKeyword);
								runSubiterationCount=intSubIterations;
						
								int groupIndex=1;
								for ( int subIt = IterationCrntRow_TestData; subIt<IterationCrntRow_TestData+intSubIterations;subIt++ ){
									intMasterTDRow=subIt;
									
									LogFW.log("Master test data Row " + intMasterTDRow);
									LogFW.log("TEST_DATA_ROW {" + subIt + "}   , TEST_CASE {" + testCaseName + "}, ITERATION {" + currentIteration + "}, SUBITERATION {" + groupIndex +"}" );
									intBPSsubIteration = groupIndex;
									for (String subg: groupedKeywords){
										//LogFW.log(subg);
										executeKeyword(subg);
									}
									groupIndex=groupIndex+1;
								}
								//intBPSsubIteration=0;
								groupedKeywords.clear();
								//LogFW.log("------------------------------------------------------------------");
							}
						}										
					}else{
						//LogFW.log("------------------------------------------------------------------");
						break;
					}
				}
			//Reporter.Append_IterationTest(ChildTest);
			//Reporter.Append_ChildTest(ChildTest);
			LogFW.log("ITERATION COMPLETED [" + currentIteration + "] ");
			//LogFW.log("------------------------------------------------------------------");
			currentIteration=currentIteration+1;  			
		}
		LogFW.log("TEST CASE EXECUTION COMPLETED [" + testCaseName +"]");
		LogFW.log("------------------------------------------------------------------");
	}
	
	public static void readIteration(){
		iterationMode=keywordRow.getCell(3).toString();
		//System.out.println();
		//System.out.println("@@@@@@@@@@@@----------Iteration Mode is: " + iterationMode);
		//LogFW.log("------------------------------------------------------------------");
		LogFW.log("ITERATION MODE: [" + iterationMode + "]");
		if (iterationMode.equalsIgnoreCase("Run one iteration only")){
			startIteration=1; 
			endIteration=1;
			currentIteration=1;
			
		}else if(iterationMode.equalsIgnoreCase("Run all iterations")){
			startIteration=1; 
			endIteration=65535;
			currentIteration=1;
			
		}else if(iterationMode.equalsIgnoreCase("Run from <Start Iteration> to <End Iteration>")){
			if (keywordRow.getCell(4).toString().isEmpty()){
				startIteration=1;
			}else{
				startIteration=(int)keywordRow.getCell(4).getNumericCellValue(); 
			}
			
			if (keywordRow.getCell(5).toString().isEmpty()){
				endIteration=1;
			}else{
				endIteration=(int)keywordRow.getCell(5).getNumericCellValue(); 
			}
			LogFW.log("Start_Iteration [" + startIteration + "]  !! End_Iterattion [" + endIteration +"]");
			//System.out.println("Start iteration: " + startIteration + " , End iterattion is: " + endIteration);
			
			currentIteration=startIteration;
		}else{
			LogFW.warning("No iteration mode is found do assuming only 1 iteration");
			//System.out.println("No iteration mode is found do assuming only 1 iteration");
			startIteration=1; 
			endIteration=1;
			currentIteration=1;
		}
		//LogFW.log("------------------------------------------------------------------");
	}
	

	public static void setDataSheet(Sheet datasheet){
		dataSheet=datasheet;
	}
	

	static void setTestDataWB(Workbook testDataWB){
		TestData=testDataWB;
	}
	

	public static void setDataRow(){
		int intRow=0;
		//System.out.println("Test Case name from Data sheet -" + dataSheet.getRow(intRow).getCell(0));
		while(!dataSheet.getRow(intRow).getCell(0).toString().isEmpty()){
			//System.out.println("0000000000 - test data sheet - TC " + dataSheet.getRow(intRow).getCell(0) + " , row num  " + dataSheet.getRow(intRow).getCell(10) );
			if (dataSheet.getRow(intRow).getCell(0).toString().equalsIgnoreCase(testCaseName) && dataSheet.getRow(intRow).getCell(1).getNumericCellValue()==startIteration ){
				dataRow=dataSheet.getRow(intRow);
				//System.out.println("~~~~~~~~~Data sheet TC name- " + dataSheet.getRow(intRow).getCell(0) + " , data row Num " + IterationStartRow_TestData);
				break;
			}
			intRow=intRow+1;
			IterationStartRow_TestData= intRow;
		}
		
	}
	
	public static void executeKeyword(String keyword){
		//@@ Child test started
		ChildTest = Reporter.StartChild_ReporterTest("Iteration [" + currentIteration + "] ......." + keyword);

		
		//@@ check for launch browser to setup the browser and pass the driver
		if (keyword.equalsIgnoreCase("LaunchBrowser")){
			driver=BrowserSetup.LaunchBrowser();
			B_lib =  new Keywords(driver);
		}else{
			Method method;
			try {
				//launch keyword
				method = Keywords.class.getDeclaredMethod(keyword,null);
				method.invoke(B_lib, null);	
				if (FW_Config.config.getProperty("TerminateTC").equalsIgnoreCase("yes")){
					//framework_ReportClass.logFATAL("Terminating This TC Execution", "FATAL ERROR");
					FW_Config.config.setProperty("TerminateTC", "NO");
					driver.close();
					driver.quit();
					//break;
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LogFW.log("Problem execution/Invoking the Keyword " + keyword + ", Casue - " + e.getCause()+ " , Message: " + e.getStackTrace());
				Reporter.logFATAL(keyword + "could not be executed ", e.getCause() + " | " + e.getStackTrace());
			}
		}
		Reporter.Append_ChildTest(ChildTest);
	}
	
	
	/** get_TestData
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: get_TestData, Take the testData from the testData sheet for corresponding parameter
	 * -----------------------------------------------------------------------------------------------------
	 */
	public static String get_TestData(String paramname){
		
		String paramvalue="",commonDataIdentifier,CommonDataSheetName,keyVal ;
		
		int intindexrow, intparamCol=0;
		boolean paramFlag=false, cmnFlag=false;;
			
		intindexrow = dataSheet.getRow(0).getLastCellNum()-dataSheet.getRow(0).getFirstCellNum();
		for (int ic=0; ic<=intindexrow; ic++){
			if (dataSheet.getRow(0).getCell(ic).toString().equalsIgnoreCase(paramname)){
				intparamCol=ic;
				paramFlag=true;
				LogFW.log("get_TestData (" + paramname + ") discovered at col: " + intparamCol);
				break;
			}
		}
		
		if (paramFlag){
			
			keyVal = dataSheet.getRow(intMasterTDRow).getCell(intparamCol).toString();
			
			commonDataIdentifier=FW_Config.config.getProperty("CommonDataIdentifier");
			CommonDataSheetName =FW_Config.config.getProperty("CommonnDataSheet_Name");
			
			if (keyVal.indexOf(commonDataIdentifier)==0){
				Sheet CDSheet = TestData.getSheet(CommonDataSheetName);
				String CD_name = keyVal.substring(1).toString();
				
				int CD_usedRow, CD_usedCol;
				CD_usedRow=CDSheet.getLastRowNum()-CDSheet.getFirstRowNum();
				CD_usedCol=CDSheet.getRow(0).getLastCellNum()-CDSheet.getRow(0).getFirstCellNum();
				
				for(int loopR=1;loopR<CD_usedRow;loopR++){
					Row CD_row = CDSheet.getRow(loopR);
					if (CD_row.getCell(0).toString().equalsIgnoreCase(CD_name)){
						//find out the coulumn
						for (int loopC=1;loopC<CD_usedCol;loopC++){
							if (CDSheet.getRow(0).getCell(loopC).toString().equalsIgnoreCase(paramname)){
								paramvalue=CDSheet.getRow(loopR).getCell(loopC).toString();
								//System.out.println(key + " is(CommonData) " + keyVal);
								LogFW.log("{" + paramname + "} (CommonData) Value: " + paramvalue);
								cmnFlag=true;
								break;
							}
						}
						if (!cmnFlag){
							LogFW.error(paramname + " is(CommonData) and could not be identified " );
						}
					}
				}
			}else{
				paramvalue=keyVal;
				LogFW.log("{"+paramname + "} Value: " + paramvalue);
			}
			
		}else{
			LogFW.error("get_TestData param key " + paramname + " is not discovered in data sheet module " + dataSheet.getSheetName());
		}
		return paramvalue;
	}
	
	
	/** set_TestData
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: set_TestData, set the testData from the run time and set it in test data sheet
	 * -----------------------------------------------------------------------------------------------------
	 */
	public static void set_TestData(String paramName, String paramVal){
		
		int intindexrow, intparamCol=0;
		boolean paramFlag=false ;
			
		intindexrow = dataSheet.getRow(0).getLastCellNum()-dataSheet.getRow(0).getFirstCellNum();
		
		for (int ic=0; ic<=intindexrow; ic++){
			if (dataSheet.getRow(0).getCell(ic).toString().equalsIgnoreCase(paramName)){
				intparamCol=ic;
				paramFlag=true;
				LogFW.log("set_TestData param key " + paramName + " discovered at col " + intparamCol);
				break;
			}
		}
		
		if (paramFlag){
			dataSheet.getRow(intMasterTDRow).createCell(intparamCol).setCellValue(paramVal.toString());
			LogFW.log(paramName + " is set to value " + dataSheet.getRow(intMasterTDRow).getCell(intparamCol).toString());
			Reporter.logINFO("set_TestData [" + paramName + "]", "Is set to [" + dataSheet.getRow(intMasterTDRow).getCell(intparamCol).toString() + "]");
		}else{
			LogFW.error(paramName + " is not found in test data sheet");
		}
	}
	
	public static void launchResult(){
		String ResultLauncher;
		DesiredCapabilities cap;
		WebDriver driver1;
		ResultLauncher= FW_Config.config.getProperty("LaunchResultInBrowser");
		
		switch (ResultLauncher){
			case "Chrome":
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\Drivers\\chromedriver.exe");
				driver1 = new ChromeDriver();
				break;
			case "IE":
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\Drivers\\IEDriverServer.exe");
				cap = new DesiredCapabilities();
				cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				driver1 = new InternetExplorerDriver(cap);
				break;
			case "Mozila":
				String FF_Profile = FW_Config.config.getProperty("Firfox_Profile");
				ProfilesIni profile = new ProfilesIni();
				FirefoxProfile ffprofile = profile.getProfile(FF_Profile);
				
				cap = new DesiredCapabilities();
				cap.setCapability(FirefoxDriver.PROFILE, ffprofile);			
				driver1 = new FirefoxDriver(cap);
				break;
			default:
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\Drivers\\IEDriverServer.exe");
				cap = new DesiredCapabilities();
				cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				driver1 = new InternetExplorerDriver(cap);
				break;
		}	
		ResultFolder = Reporter.ResultFolderName;
		driver1.get(System.getProperty("user.dir")+"\\Results\\Run_"+ResultFolder+"\\Report.html");
	}

}
