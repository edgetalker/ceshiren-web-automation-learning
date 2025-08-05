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

import static org.junit.jupiter.api.Assertions.assertTrue;

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

	public void closeMsg() {
		// 处理可能出现的弹窗
		if (driver.getPageSource().contains("确定")) {
			driver.findElement(By.xpath("//*[text()='确定']")).click();
		}
	}

	@ParameterizedTest
	@ValueSource(strings={
			"pytest",
			"面试题",
			"a",
			"appium desktop连接真机，start session，出现报错，手机上appium setting打开闪退，但是进程显示是进行中。报错内容：An unknown server-side error occurred while processing the command. Original error: Could not find a connected Android device in 20364ms."
	})
	public void testSearch(String searchKey) {
		//点击搜索按钮
		driver.findElement(By.id("search-button")).click();

		//输入key
		WebElement eleSend=driver.findElement(By.id("search-term"));
		eleSend.clear();
		eleSend.sendKeys(searchKey);

		//等待高级筛选按钮加载完成
		WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(10),Duration.ofMillis(200));
		WebElement ele=wait.until(
				ExpectedConditions.elementToBeClickable(By.className("show-advanced-search"))
		);
		ele.click();

		//等待第一个结果加载完毕
		WebDriverWait searchWait=new WebDriverWait(driver,Duration.ofSeconds(5),Duration.ofMillis(200));
		WebElement searchEle=searchWait.until(
				ExpectedConditions.elementToBeClickable(By.className("search-link"))
		);
		String searchText=searchEle.getText();
		System.out.println("高级搜索页面下的第一个标题的文本为:"+searchText);

		closeMsg();
		//点击链接
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

		}catch (Exception e) {
			// 如果还是失败，使用JavaScript强制点击
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchEle);
		}

		//等待帖子详情页加载完毕，确认页面跳转成功
		WebDriverWait titleWait=new WebDriverWait(driver,Duration.ofSeconds(5),Duration.ofMillis(200));
		titleWait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='fancy-title']"))
		);
		String resultPage=driver.getTitle();
		System.out.println("跳转页面的标题为:"+resultPage);

		//断言
		assertTrue(resultPage.contains(searchText));
	}

	/**
	 * 边界值测试
	 */
	@Test
	public void testSearchNone(){
		//点击搜索按钮
		driver.findElement(By.id("search-button")).click();
		//输入key并点击
		WebElement eleSend=driver.findElement(By.id("search-term"));
		eleSend.clear();
		eleSend.sendKeys("");
		//关闭弹窗
		closeMsg();
		//等待高级筛选按钮加载完毕
		WebDriverWait wait=new WebDriverWait(driver,Duration.ofSeconds(5),Duration.ofMillis(200));
		WebElement ele=wait.until(
				ExpectedConditions.elementToBeClickable(By.className("show-advanced-search"))
		);
		ele.click();
		//

	}
}
