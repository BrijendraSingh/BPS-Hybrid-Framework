/**
 * 
 */
package pageobjects;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Brijendra Singh
 *
 */
public class GoogleNewsPage {
	WebDriver driver;
	
	public GoogleNewsPage(WebDriver driver) {
		// TODO Auto-generated constructor stub
		this.driver = driver;
	}
	
	@FindBy(className="titletext")
	List<WebElement> text_title;
	
	@FindBy(className="esc-lead-article-title")
	List<WebElement> text_title_1;
	
	public List<WebElement> newsTitles(){
		return text_title;
	}
	
	public List<WebElement> newsTitle1(){
		return text_title_1;
		
	}
}
