package web_po.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
// 测试功能
public class demo1 {
	private WebDriver driver;

	@BeforeEach
	public void setupMethod(){
		driver=new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://ceshiren.com");
		driver.manage().window().maximize();
	}

	@AfterEach
	public void teardownMethod(){
		if(driver!=null){
			driver.quit();
		}
	}
	@ParameterizedTest
	@ValueSource(strings={
			"pytest"
	})
	public void testSearch(String searchKey){
		driver.findElement(By.id("search-button")).click();

		WebElement eleSend=driver.findElement(By.id("search-term"));
		eleSend.clear();
		eleSend.sendKeys(searchKey);

		WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10),Duration.ofMillis(200));
		WebElement ele=wait.until(
				ExpectedConditions.elementToBeClickable(By.className("show-advanced-search"))
		);
		ele.click();

		WebDriverWait searchWait=new WebDriverWait(driver,Duration.ofSeconds(5),Duration.ofMillis(200));
		WebElement searchEle=searchWait.until(
				ExpectedConditions.elementToBeClickable(By.className("search-link"))
		);
	}

}
