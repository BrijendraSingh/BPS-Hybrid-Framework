package framework_bps;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;



public class BrowserSetup {
static WebDriver driver;
	
	public static WebDriver LaunchBrowser(){
		//@Browser type - from config file
		String typeBrowser= ExcelUtil.get_TestData("Browser_Name");
		
		//@ Chrom Browser setup
		if(typeBrowser.equalsIgnoreCase("chrome")){
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"Drivers\\chromedriver.exe");
			driver = new ChromeDriver();
			
		//@ Firefox Browser Setup - "mozila"	
		}else if (typeBrowser.equalsIgnoreCase("mozila")){
			String FF_Profile = FW_Config.config.getProperty("Firfox_Profile");
			ProfilesIni profile = new ProfilesIni();
			FirefoxProfile ffprofile = profile.getProfile(FF_Profile);
			
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(FirefoxDriver.PROFILE, ffprofile);			
			driver = new FirefoxDriver(cap);
			
		//@ Internet Explorer Setup	
		}else if (typeBrowser.equalsIgnoreCase("ie")){
			System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"Drivers\\IEDriverServer.exe");
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(cap);
		
		// Default case will launch the Firefox	
		}else{
			String FF_Profile = FW_Config.config.getProperty("Firfox_Profile");
			ProfilesIni profile = new ProfilesIni();
			FirefoxProfile ffprofile = profile.getProfile(FF_Profile);
			
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(FirefoxDriver.PROFILE, ffprofile);			
			driver = new FirefoxDriver(cap);
			
		}
			
		driver.manage().window().maximize();
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		
		LogFW.log(typeBrowser + " Browser launched");	
		Reporter.logINFO("Launch Browser", typeBrowser + " is launched with all desired capabilities");
		return driver;
	}
	
	public static WebDriver getMainDriver(){
		return driver;
	}
}
