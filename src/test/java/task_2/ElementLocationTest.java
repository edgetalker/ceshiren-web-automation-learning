package task_2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

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

	
}
