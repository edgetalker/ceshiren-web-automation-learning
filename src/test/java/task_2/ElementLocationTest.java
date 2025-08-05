package task_2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 任务二：元素定位联系
 */
public class ElementLocationTest {
	private WebDriver driver;

	@BeforeEach
	public void setupMethod() {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.get("https://ceshiren.com");
		driver.manage().window().maximize();
	}

	@AfterEach
	public void teardownMethod() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void testById(){
		WebElement ele=driver.findElement(By.id("search-button"));

		// 验证元素存在
		assertNotNull(ele, "搜索按钮应该存在");
		assertTrue(ele.isDisplayed(), "搜索按钮应该可见");
		System.out.println("元素标签名：" + ele.getTagName());
	}

	@Test
	public void testByClass(){
		try {

			WebElement loginButton = driver.findElement(By.className("login-button"));

			assertNotNull(loginButton, "登录按钮应该存在");
			assertTrue(loginButton.isDisplayed(), "登录按钮应该可见");

			System.out.println("通过ClassName成功定位到登录按钮");
			System.out.println("按钮文本：" + loginButton.getText());
		} catch (Exception e) {
			System.out.println("ClassName定位失败：" + e.getMessage());
			// 如果主要定位失败，尝试其他相关类名
			try {
				WebElement altLoginButton = driver.findElement(By.className("btn-primary"));
				System.out.println("通过备用ClassName找到相关按钮");
			} catch (Exception ex) {
				System.out.println("备用ClassName也失败");
			}
		}
	}

	@Test
	public void testByCss(){
		// CSS选择器示例
		String[] cssSelectors = {
				"#search-button",                    // ID选择器
				".login-button",                     // 类选择器
				"button[title*='搜索']",              // 属性选择器
				"header .login-button",              // 后代选择器
		};

		for (String selector : cssSelectors) {
			try {
				List<WebElement> elements = driver.findElements(By.cssSelector(selector));
				if (!elements.isEmpty()) {
					System.out.println("✓ CSS选择器 '" + selector + "' 找到 " + elements.size() + " 个元素");
					WebElement firstElement = elements.getFirst();
					System.out.println("  - 第一个元素标签：" + firstElement.getTagName());
					if (!firstElement.getText().isEmpty()) {
						System.out.println("  - 元素文本：" + firstElement.getText());
					}
				} else {
					System.out.println("× CSS选择器 '" + selector + "' 未找到元素");
				}
			} catch (Exception e) {
				System.out.println("× CSS选择器 '" + selector + "' 出错：" + e.getMessage());
			}
		}
	}

	@Test
	public void testByXpath(){
		String[] xpaths={
				"//button[@id='search-button']",
				"//button[@contains(@class,'login']",
				"//a[contains(text(),'登录')]",
				"//nav//a[position()=1]"
		};

		for(String xpath:xpaths){
			try{
				List<WebElement> eles=driver.findElements(By.xpath(xpath));
				if(!eles.isEmpty()){
					System.out.println("Xpath"+xpath+"找到"+eles.size()+"个元素");
				}else{
					System.out.println("Xpath"+xpath+"未找到元素");
				}
			}catch(Exception e){
				System.out.println("XPath"+xpath+"出错："+e.getMessage());
			}
		}
	}
}
