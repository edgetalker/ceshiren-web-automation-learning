package orangeHRM;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class loginTest {
	private WebDriver driver;
	private WebDriverWait wait;

	@BeforeEach
	public void setupMethod(){
		driver=new ChromeDriver();
		wait=new WebDriverWait(driver, Duration.ofSeconds(5),Duration.ofMillis(200));
		driver.get("https://opensource-demo.orangehrmlive.com");
		driver.manage().window().maximize();
	}

	@AfterEach
	public void teardownMethod(){
		if(driver!=null){
			driver.quit();
		}
	}

	/**
	 * username:Admin
	 * password:admin123
	 */
	@ParameterizedTest
	@CsvSource({
			"Admin,admin123"
	})
	public void testLogin(String username,String password){
		WebElement name =wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='username']"))
		);
		name.clear();
		name.sendKeys(username);

		WebElement pw=wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='password']"))
		);
		pw.clear();
		pw.sendKeys(password);

		driver.findElement(By.xpath("//button[@type='submit']")).click();

		try{
			WebDriverWait loginWait=new WebDriverWait(driver,Duration.ofSeconds(10),Duration.ofMillis(200));

			WebElement loginEle=loginWait.until(
					ExpectedConditions.visibilityOfElementLocated(By.className("oxd-topbar-header-breadcrumb-module"))
			);

			System.out.println(loginEle.getText());
			assertEquals(loginEle.getText(),"Dashboard");
			System.out.println("登录成功");
		}catch (TimeoutException e){
			fail("登录成功后未能找到预期的页面元素，可能登录失败或页面加载异常");
		}
	}

	@ParameterizedTest
	@CsvSource({
			"Admin1,11111",
			"Admin,123",
			"Ad,admin123"
	})
	public void testLogin2(String username,String password){
		WebElement name =wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='username']"))
		);
		name.clear();
		name.sendKeys(username);

		WebElement pw=wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='password']"))
		);
		pw.clear();
		pw.sendKeys(password);

		driver.findElement(By.xpath("//button[@type='submit']")).click();

		try{
			WebDriverWait loginWait=new WebDriverWait(driver,Duration.ofSeconds(10),Duration.ofMillis(200));

			WebElement errorEle=loginWait.until(
					ExpectedConditions.visibilityOfElementLocated(By.className("oxd-alert-content-text"))
			);

			System.out.println(errorEle.getText());
			assertEquals(errorEle.getText(),"Invalid credentials");
			System.out.println("登录失败");
		}catch(Exception e){
			fail("登录后既没有成功页面也没有错误提示，页面状态异常");
		}
	}
}
