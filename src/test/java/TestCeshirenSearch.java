
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCeshirenSearch {

	private WebDriver driver;

	@BeforeEach
	public void setupMethod() {
		// 打开 Chrome 浏览器 data; 空白页面
		driver = new ChromeDriver();
		// 隐式等待，当查找元素时，如果元素不存在会等待最多10秒
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		// --------------- 1. 打开测试人论坛 ---------------
		driver.get("https://ceshiren.com/");
		// 最大化窗口，确保所有元素可见
		driver.manage().window().maximize();
	}

	@AfterEach
	public void teardownMethod() {
		// driver 退出
		if (driver != null) {
			driver.quit();
		}
	}

	public void closeMsg() {
		// 处理可能出现的弹窗
		if (driver.getPageSource().contains("确定")) {
			driver.findElement(By.xpath("//*[text()='确定']")).click();
		}
	}

	// 正向用例
	@ParameterizedTest
	@ValueSource(strings = {
			"pytest",
			"面试题",
			"a",
			"appium desktop连接真机，start session，出现报错，手机上appium setting打开闪退，但是进程显示是进行中。报错内容：An unknown server-side error occurred while processing the command. Original error: Could not find a connected Android device in 20364ms."
	})
	public void testSearch(String searchKey) {
		//点击搜索按钮
		driver.findElement(By.cssSelector("#search-button")).click();

		//输入关键词
		WebElement eleSend = driver.findElement(By.id("search-term"));
		eleSend.clear();
		eleSend.sendKeys(searchKey);

		//使用显式等待确保高级搜索按钮完全加载后再操作
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofMillis(200));
		WebElement ele = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("show-advanced-search"))
		);
		ele.click();

		//等待搜索结果加载并获取第一个结果
		WebDriverWait searchWait = new WebDriverWait(driver, Duration.ofSeconds(5), Duration.ofMillis(200));
		WebElement searchEle = searchWait.until(
				ExpectedConditions.elementToBeClickable(By.className("search-link"))
		);
		//获取搜索结果文本用于后续验证
		String searchText = searchEle.getText();
		System.out.println("高级搜索页面下第一个标题的文本为：" + searchText);

		closeMsg();

		// === 修复点击被拦截的问题 ===
		try {
			// 先尝试关闭弹窗覆盖层
			try {
				//检测并处理可能的弹窗覆盖层
				WebElement overlay = driver.findElement(By.cssSelector(".dialog-overlay"));
				if (overlay.isDisplayed()) {
					overlay.click();
					Thread.sleep(500);
				}
			} catch (Exception e) {
				// 覆盖层不存在，忽略
			}

			// 再次尝试普通点击
			searchEle.click();

		} catch (Exception e) {
			// 如果还是失败，使用JavaScript强制点击
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchEle);
		}
		// === 修复结束 ===
		// 等待帖子详情页的标题元素加载完毕，确认页面跳转成功
		WebDriverWait titleWait = new WebDriverWait(driver, Duration.ofSeconds(5), Duration.ofMillis(100));
		titleWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='fancy-title']"))
		);
		//验证测试结果
		String resultPageTitle = driver.getTitle();
		System.out.println("跳转页面的标题为：" + resultPageTitle);

		// 断言：验证搜索结果的文本是否包含在页面标题中
		assertTrue(resultPageTitle.contains(searchText));
	}

	/**
	 * 边界测试用例 - 测试空搜索关键词的处理
	 * 验证系统对于无效输入的错误处理机制
	 */
	@Test
	public void testSearchNone() {
		// --------------- 2. 点击搜索按钮 ---------------
		driver.findElement(By.cssSelector("#search-button")).click();
		// --------------- 3. 输入搜索关键字---------------
		WebElement eleSend = driver.findElement(By.id("search-term"));
		eleSend.clear();
		eleSend.sendKeys("");

		// 处理可能出现的弹窗
		closeMsg();

		// --------------- 4. 点击进入高级搜索页面 ---------------
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofMillis(200));
		WebElement ele = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("show-advanced-search"))
		);
		ele.click();
		// 点击搜索按钮
		driver.findElement(By.cssSelector(".search-cta")).click();
		// --------------- 5. 断言 ---------------
		String msg = driver.findElement(By.xpath("//*[@class='fps-invalid']")).getText();
		System.out.println("提示信息为：" + msg);
		assertEquals("您的搜索词过短。", msg);
	}

	/**
	 * 异常测试用例 - 测试无搜索结果的情况
	 * 验证系统对于找不到匹配内容时的处理
	 */
	@Test
	public void testSearchNoResult() {
		// --------------- 2. 点击搜索按钮 ---------------
		driver.findElement(By.cssSelector("#search-button")).click();
		// --------------- 3. 输入搜索关键字---------------
		WebElement eleSend = driver.findElement(By.id("search-term"));
		eleSend.clear();
		eleSend.sendKeys("ooooooooooo");

		// 处理可能出现的弹窗
		closeMsg();

		// --------------- 4. 点击进入高级搜索页面 ---------------
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofMillis(200));
		WebElement ele = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.className("show-advanced-search"))
		);
		ele.click();
		// 点击搜索按钮
		driver.findElement(By.cssSelector(".search-cta")).click();
		// --------------- 5. 断言 ---------------
		String msg = driver.findElement(By.cssSelector(".loading-container > h3")).getText();
		System.out.println("提示信息为：" + msg);
		assertEquals("找不到结果。", msg);
	}
}