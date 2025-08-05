package task_1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务一：页面基础验证
 */
public class BasicPageTest {
	private WebDriver driver;

	@BeforeEach
	public void setupMethod(){
		driver=new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.get("https://ceshiren.com");
		driver.manage().window().maximize();
	}

	@AfterEach
	public void teardownMethod(){
		if(driver!=null){
			driver.quit();
		}
	}

	/**
	 * 验证网站标题
	 */
	@Test
	public void testPageTitle(){
		String title=driver.getTitle();
		System.out.println("页面标题为："+title);
		assertTrue(title.contains("测试人社区"));
	}

	/**
	 * 验证当前url
	 */
	@Test
	public void testPageUrl(){
		String url= driver.getCurrentUrl();
		System.out.println("页面的url为："+url);
		assertTrue(url.contains("ceshiren.com"));
	}

	/**
	 * 验证页面源码
	 */
	@Test
	public void testPageSource(){
		String pageSource=driver.getPageSource();

		assertTrue(pageSource.contains("测试人"), "页面应该包含'测试人'文字");
		assertTrue(pageSource.contains("社区"), "页面应该包含'社区'文字");
		assertFalse(pageSource.contains("Error"), "页面不应该包含Error错误");

		System.out.println("页面源码长度：" + pageSource.length() + " 字符");
	}

	/**
	 * 验证页面加载状态
	 */
	@Test
	public void testPageLoading(){
		String status=(String)((JavascriptExecutor) driver).executeScript("return document.readyState");
		System.out.println("页面状态为："+status);
		assertEquals("complete",status);
	}
	/**
	 * 页面导航测试
	 */
	@Test
	public void testPageNavigation(){
		String page1=driver.getTitle();

		driver.get("https://ceshiren.com/about");
		String page2=driver.getTitle();

		assertNotEquals(page1,page2,"两个页面的标题应该不同");
		driver.navigate().back();
		String page3=driver.getTitle();
		assertEquals(page1,page3,"后退后应该回到第一个页面");
		driver.navigate().forward();
		String page4=driver.getTitle();
		assertEquals(page2,page4,"前进后应该到达第二个页面");
	}
}
