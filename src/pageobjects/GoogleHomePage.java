package pageobjects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import framework_bps.BRIJ_Config;
import framework_bps.BRIJ_Reporter;

public class GoogleHomePage {
	WebDriver driver;
	WebDriverWait wait;
	
	//@@ Constructor to set the Webdriver
	public GoogleHomePage(WebDriver ldriver) {
		driver = ldriver;
		wait = new WebDriverWait(driver, 30);
	}
	
	//@@start your objects
	@FindBy(id="gb_70")
	WebElement gSignIn;
	
	@FindBy(xpath="//*[@id='gbwa']/div[1]/a")
	WebElement gApp;
	
	@FindBy(linkText="News")
	WebElement gNews;
	
	@FindBy(linkText="Play")
	WebElement gPlay;
	
	@FindBy(xpath="//*[@id='gbwa']/div[2]/ul[1]/li")
	List<WebElement> gAppIcons;
	
	//@@ Object Methodes
	public void click_gApp(){
		wait.until(ExpectedConditions.elementToBeClickable(gApp));
		try {
			gApp.click();
			BRIJ_Reporter.logPASS("Google app Icon Click","Done");
		}catch(Throwable e) {
			BRIJ_Reporter.logFATAL("Google App Icon Click, NOT DONE", e.getMessage());
		}		
	}
	
	public void SelectClick_GoogApp(String AppName){
		int RetVAL=0;
		if (gAppIcons.size()>0){
			for (int i=0;i<gAppIcons.size();i++){
				if (gAppIcons.get(i).getText().equalsIgnoreCase(AppName)){
					wait.until(ExpectedConditions.elementToBeClickable(gAppIcons.get(i)));
					gAppIcons.get(i).click();
					RetVAL=1;
					BRIJ_Reporter.logPASS("Google Application Selected", AppName);
					break;
				}
			}
		}
		if ( RetVAL==0){
			BRIJ_Reporter.logFAIL("Google Application not Selected", AppName);
		}
	}
	
	
	public void click_gSignIn(){
		gSignIn.click();
	}
	

	public void click_gNews(){
		try {
			gNews.click();
			BRIJ_Reporter.logPASS("Google News click","Done");
		}catch(Throwable e) {
			BRIJ_Reporter.logFATAL("Google News Click Error", e.getMessage());
			BRIJ_Config.config.setProperty("TerminateTC", "YES");
		}
	}	
	
	public void click_gPlay(){
		try {
			gPlay.click();
			BRIJ_Reporter.logPASS("Google Play click","Done");
		}catch(Throwable e) {
			BRIJ_Reporter.logFATAL("Google Play Click Error", e.getMessage());
		}
	}
}
