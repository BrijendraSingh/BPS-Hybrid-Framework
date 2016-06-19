package businessComponents;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import framework_bps.BrowserSetup;
import framework_bps.ExcelUtil;
import framework_bps.FW_Config;
import framework_bps.Reporter;
import pageobjects.GoogleHomePage;
import pageobjects.GoogleNewsPage;
import pageobjects.GooglePlayPage;


public class Keywords {
	
	//@@ WebDriver 
	static WebDriver driver;
	
	//@@ Page Class declaration
	static GoogleHomePage gHomePage;
	static GoogleNewsPage gNewsPage;
	static GooglePlayPage gPlayPage;
	
	//@@ Constructor to set the page classes constructor and webdriver
	public Keywords(WebDriver ldriver) {
		driver=ldriver;
		gHomePage =  PageFactory.initElements(driver, GoogleHomePage.class);
		gNewsPage =  PageFactory.initElements(driver, GoogleNewsPage.class);
		gPlayPage =  PageFactory.initElements(driver, GooglePlayPage.class);
	}
	
	public static WebDriver getdriver(){
		return driver;
	}
	

	/**3. appNavigateBack
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: appNavigateBack, print news headers from specific class to logFile
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void appNavigateBack(){
		driver.navigate().back();
		Reporter.logPASS("Open Previous Page", "Done");
	}
	
	/**4. QuitApp
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: QuitApp, close the browser
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void QuitApp(){
		try{
			BrowserSetup.getMainDriver().close();
			BrowserSetup.getMainDriver().quit();
			Reporter.logINFO("Close Browser" , "Done");
			driver = null;
		}catch (Throwable e){
			Reporter.logERROR("Close Browser error", e.getMessage());
		}
	}
	
	public static void putWAIT(int wait){
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//*****************************************************************************************************
	/**----------------------------------------------------------------------------------------------------
	 *					User Define Methods- Keywords
	 -----------------------------------------------------------------------------------------------------*/
	//*****************************************************************************************************
	/** OpenGnewsPage
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: OpenGnewsPage, open up the google news page
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void OpenGnewsPage(){					
		gHomePage.click_gApp();
		gHomePage.click_gNews();
		if (driver.getTitle().equalsIgnoreCase("Google News")){
			Reporter.logPASS("Google News Page Open", "Done");
		}else{
			Reporter.logFATAL("Google News Page Open", "NOT OPEN");
			FW_Config.config.setProperty("TerminateTC", "YES");
		}
	}
	
	/** SelectGapp
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: SelectGapp, select and click google app type from the icons sections
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void SelectGapp(){
		String strAppName 	=ExcelUtil.get_TestData("AppName");
		String strIndex 	=ExcelUtil.get_TestData("AppNameIndex"); 
		int index=1000;
		
		String AppName=null;
		if(!strIndex.toString().isEmpty()){ 
			index = Integer.parseInt(strIndex);
			String[] arrAppName = strAppName.split(",");
			if (arrAppName.length > index){
				AppName = arrAppName[index];
				index=index+1;
				ExcelUtil.set_TestData("AppNameIndex", Integer.toString(index));
			}else{
				AppName = arrAppName[index];
				index=0;
				ExcelUtil.set_TestData("AppNameIndex", Integer.toString(index));
			}
		}else{
			AppName=strAppName;
		}
		gHomePage.click_gApp();
		gHomePage.SelectClick_GoogApp(AppName);
		index=index+1;
	}
	
	/** SelectGapp_trad
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: SelectGapp, select and click google app type from the icons sections
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void SelectGapp_trad(){
		String AppName = ExcelUtil.get_TestData("AppName");
		putWAIT(5000);
		gHomePage.click_gApp();
		gHomePage.SelectClick_GoogApp(AppName);
		ExcelUtil.set_TestData("putHere", "parafst83");
	}
	/** print_news
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: print_news, print news headers from specific class to logFile
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void print_news(){
		List<WebElement> gntitle =gNewsPage.newsTitles();
		if (gntitle.size()>0){
			int i=1; 
			for(WebElement ele: gntitle){		
				if (ele.getText()!=""){
					Reporter.logINFO("News Item is - ", ele.getText());
				}
				i=i+1;
			}	
			Reporter.logPASS("Google News Page", "News Printed");
		}else{
			Reporter.logFAIL("Google news page", "News Items are not found");
		}
		//Reporter.logFATAL("print Google news", "Check if pass/fail is available !!");
	}
		
	/** print_news1
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: print_news1, print news headers to Extent report
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void print_news1(){
		
		List<WebElement> gntitle =gNewsPage.newsTitles();
		if (gntitle.size()>0){
			int i=1; 
			for(WebElement ele: gntitle){
				Reporter.logINFO("News " + i + "is - ", ele.getText());
				i=i+1;
			}
			Reporter.logPASS("Google news page", "News Printed");
		}else{
			Reporter.logFAIL("Google news page", "News Items are not found");
		}
		
	}
	
	/** LaunchApp
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: LaunchApp, open the application url in browser
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void LaunchApp(){
		String appUrl = ExcelUtil.get_TestData("App_URL");
		//set_testdata validation
		//framework_ExcelSupportMethods.set_TestData("testData", "mozila");
		
		driver.navigate().to(appUrl);
		if (driver.getTitle().equalsIgnoreCase("Google")){
			Reporter.logPASS("Google Home Page", "Title Validated");
		}else{
			Reporter.logFAIL("Google Home Page", "Title is not Validated");
		}						
	}
		
	/** LaunchApp
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: LaunchApp, open the application url in browser
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void Select_Click_gPlaySection(){	
		
		String section=ExcelUtil.get_TestData("gApp_section");	
		
		if (gPlayPage.click_gAppSection(section)==1){
			Reporter.logPASS(section + " is clicked","Google Play Page");
		}else{
			Reporter.logFATAL("Google Play Page", section + " is not Clicked");
		}		
		gPlayPage.click_gPlayAccountSection(ExcelUtil.get_TestData("gPlayAccountSection"));
		gPlayPage.click_CancelButton();
	}
	
	/** click_Gplay
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: click_Gplay, click google play option from app icons
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void click_Gplay(){	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gHomePage.click_gApp();
		gHomePage.click_gPlay();	
		
		ExcelUtil.set_TestData("putHere", "gplay clicked");
	}
	
	/** Validate
	 * ----------------------------------------------------------------------------------------------------
	 * @author: Brijendra Singh
	 * @Date  : May 03, 2016 
	 * @Discription: Validate, this method is under development and need to be evolve
	 * -----------------------------------------------------------------------------------------------------
	 */
	public void Validate(String actual, String compareWith){
		if (actual.equalsIgnoreCase(compareWith)){
			Reporter.logPASS(actual + " is compared with :", compareWith);
		}else{		
			Reporter.logFAIL( actual + " is compared with :" ,compareWith );
		}
	}
	
	public void dummyOne(){
		Reporter.logPASS("Test step", "Log pass");
		Reporter.logWARNING("Test Step ", "Log warning");
	}
	
	public void dummyTwo(){
		Reporter.logFATAL("Test step", "Log fatel");
		Reporter.logPASS("Test step", "Log pass");
		Reporter.logWARNING("Test Step ", "Log warning");
	}
	
	public void dummyThree(){
		Reporter.logFAIL("Test step", "Log fail");
		Reporter.logPASS("Test step", "Log pass");
		Reporter.logWARNING("Test Step ", "Log warning");
	}
}
