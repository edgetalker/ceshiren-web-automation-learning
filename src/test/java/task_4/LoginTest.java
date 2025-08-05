package task_4;

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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 登录功能测试
 */
public class LoginTest {

	private WebDriver driver;
	private WebDriverWait wait;

	@BeforeEach
	public void setupMethod(){
		driver =new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.get("https://ceshiren.com/");
		driver.manage().window().maximize();

		wait=new WebDriverWait(driver,Duration.ofSeconds(5),Duration.ofMillis(200));
	}

	@AfterEach
	public void teardownMethod(){
		if(driver!=null){
			driver.quit();
		}
	}

	/**
	 * 正面用例
	 * @param email
	 * @param password
	 */
	@ParameterizedTest
	@CsvSource({
			"2504986774@qq.com,2004818p"
	})
	public void testLogin(String email,String password){

		performLogin(email,password);

		try{
			//浏览器会向服务器发送请求 - 如何处理页面跳转 - 特别元素
			WebElement body=wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.className("raw-topic-link"))
			);
			String text=body.getText();
			System.out.println(text);
			assertTrue(text.contains("欢迎光临测试人社区"));
			System.out.println("登录成功");
		}catch(TimeoutException e){
			fail("登录成功后未能找到预期的页面元素，可能登录失败或页面加载异常");
		}
	}

	/**
	 * 反面用例
	 * @param email
	 * @param password
	 */
	@ParameterizedTest
	@CsvSource({
			"2504986774@qq.com,11111111",
			"12345,20048181p",
			"12345,1111111"
	})
	public void testLogin2(String email,String password){

		performLogin(email,password);

		try{
			// 等待错误提示信息出现
			WebElement errorElement = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.id("modal-alert"))
			);
			String errorText = errorElement.getText();
			System.out.println("登录失败，错误信息: " + errorText);
			assertTrue(errorText.contains("不正确"), "登录失败时应该显示错误提示信息");

		} catch(TimeoutException e){
			// 如果没有找到错误提示，检查是否意外登录成功
			try{
				WebElement successElement = driver.findElement(By.className("raw-topic-link"));
				if(successElement.isDisplayed()){
					fail("使用无效凭据登录应该失败，但却登录成功了");
				}
			} catch(Exception ex){
				fail("登录后既没有成功页面也没有错误提示，页面状态异常");
			}
		}
	}

	/**
	 * 重构 ： 提取公共操作
	 * 输入 email 和 password
	 */
	private void performLogin(String email, String password){
		try{
			// 点击登录按钮
			WebElement loginBtn = wait.until(
					ExpectedConditions.elementToBeClickable(By.className("login-button"))
			);
			loginBtn.click();

			// 等待登录界面完全加载
			WebElement username = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.id("login-account-name"))
			);

			// 输入用户名
			username.clear();
			System.out.println("输入邮箱: " + email);
			username.sendKeys(email);

			// 输入密码
			WebElement passwordField = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.id("login-account-password"))
			);
			passwordField.clear();
			System.out.println("输入密码: " + password);
			passwordField.sendKeys(password);

			// 点击登录确认按钮
			WebElement loginButton = wait.until(
					ExpectedConditions.elementToBeClickable(By.id("login-button"))
			);
			loginButton.click();

			// 给服务器处理时间
			Thread.sleep(1000);

		} catch(Exception e){
			fail("执行登录操作时发生异常: " + e.getMessage());
		}
	}
}
