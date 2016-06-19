package framework_bps;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.relevantcodes.extentreports.ExtentTest;

public class ExcelDriver {
	static Workbook RunManager,TestData;
	static Sheet main, currentSheet_key, currentSheet_Data;
	static String currentSheetName;
	static ExtentTest child;

	public static void main(String[] args){
		int main_userRow;		
		
		LogFW.createLogger();
		FW_Config.ConfiGFileSetup();
		Reporter.StartReporter();
		
		//connect the test data and run manager sheet
		String Runamanger = "RunManager.xlsx", Testdata="TestData.xlsx";
		RunManager = ExcelUtil.connectXl(Runamanger);
		TestData = ExcelUtil.connectXl(Testdata);
		
		ExcelUtil.setTestDataWB(TestData);
		//connecting with main sheet in Runmanager
		main = RunManager.getSheet("Main");
		main_userRow = main.getLastRowNum()-main.getFirstRowNum();
		
		//read main sheet-run module
		for(int i=1; i<main_userRow ; i++){
			if(main.getRow(i).getCell(1).toString().equalsIgnoreCase("TRUE")){
				currentSheetName = main.getRow(i).getCell(0).toString();
				LogFW.log("");
				LogFW.log("MODULE UNDER EXECUTION [" + currentSheetName + "]");
				
				//Bases on the Execution flag, take the control over the current sheet for both the Exel files
				currentSheet_key = RunManager.getSheet(currentSheetName);
				currentSheet_Data=TestData.getSheet(currentSheetName);
				ExcelUtil.setDataSheet(currentSheet_Data);
				//read moduleSheet to run test cases
				ExcelUtil.executeSheet(currentSheet_key);
			}
		}
		Reporter.flushReporter();
		
		LogFW.log("%%%%%%%%%%%%%     All applicable Modules are executed       %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		//System.out.println("%%%%%%%%%%%%%All applicable Modules are executed%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
		//launch result
		ExcelUtil.launchResult();
		
	}
	
}
