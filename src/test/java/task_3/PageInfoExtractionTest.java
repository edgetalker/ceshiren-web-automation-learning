package task_3;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 任务3：页面信息提取
 * 学习目标：获取页面各种信息进行验证
 * 技能点：getText()、getAttribute()、getCurrentUrl()等
 */
public class PageInfoExtractionTest {

	private WebDriver driver;

	@BeforeEach
	public void setup() {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();
		driver.get("https://ceshiren.com/");
	}

	@AfterEach
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	/**
	 * 子任务3.1：提取元素文本内容
	 * 学习getText()方法的使用
	 */
	@Test
	public void testExtractElementText() {
		System.out.println("=== 提取页面文本内容 ===");

		try {
			// 提取页面标题区域的文本
			List<WebElement> headings = driver.findElements(By.cssSelector("h1, h2, h3, h4, h5, h6"));
			if (!headings.isEmpty()) {
				System.out.println("页面标题信息：");
				for (int i = 0; i < Math.min(5, headings.size()); i++) {
					WebElement heading = headings.get(i);
					String text = heading.getText().trim();
					if (!text.isEmpty()) {
						System.out.println("  " + heading.getTagName().toUpperCase() + ": " + text);
					}
				}
			}

			// 提取导航菜单文本
			List<WebElement> navItems = driver.findElements(By.cssSelector("nav a, .nav a, .navigation a"));
			if (!navItems.isEmpty()) {
				System.out.println("\n导航菜单项：");
				for (int i = 0; i < Math.min(8, navItems.size()); i++) {
					WebElement navItem = navItems.get(i);
					String text = navItem.getText().trim();
					if (!text.isEmpty() && text.length() < 20) {
						System.out.println("  - " + text);
					}
				}
			}

			// 提取页面主要内容
			List<WebElement> contentElements = driver.findElements(By.cssSelector("p, .content, .description"));
			if (!contentElements.isEmpty()) {
				System.out.println("\n页面内容摘要：");
				for (int i = 0; i < Math.min(3, contentElements.size()); i++) {
					WebElement content = contentElements.get(i);
					String text = content.getText().trim();
					if (text.length() > 10 && text.length() < 100) {
						System.out.println("  " + text);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("文本提取过程中出现错误：" + e.getMessage());
		}
	}

	/**
	 * 子任务3.2：提取元素属性信息
	 * 学习getAttribute()方法的使用
	 */
	@Test
	public void testExtractElementAttributes() {
		System.out.println("=== 提取元素属性信息 ===");

		// 提取所有链接的href属性
		List<WebElement> links = driver.findElements(By.tagName("a"));
		System.out.println("页面链接信息（前10个）：");
		int linkCount = 0;
		for (WebElement link : links) {
			if (linkCount >= 10) break;

			String href = link.getAttribute("href");
			String text = link.getText().trim();
			String title = link.getAttribute("title");

			if (href != null && !href.isEmpty()) {
				System.out.println("  链接" + (linkCount + 1) + ":");
				System.out.println("    文本: " + (text.isEmpty() ? "[无文本]" : text));
				System.out.println("    链接: " + href);
				if (title != null && !title.isEmpty()) {
					System.out.println("    标题: " + title);
				}
				linkCount++;
			}
		}

		// 提取所有图片的src和alt属性
		List<WebElement> images = driver.findElements(By.tagName("img"));
		if (!images.isEmpty()) {
			System.out.println("\n页面图片信息：");
			for (int i = 0; i < Math.min(5, images.size()); i++) {
				WebElement img = images.get(i);
				String src = img.getAttribute("src");
				String alt = img.getAttribute("alt");
				String width = img.getAttribute("width");
				String height = img.getAttribute("height");

				System.out.println("  图片" + (i + 1) + ":");
				System.out.println("    src: " + (src != null ? src : "无"));
				System.out.println("    alt: " + (alt != null ? alt : "无"));
				if (width != null || height != null) {
					System.out.println("    尺寸: " + width + "x" + height);
				}
			}
		}

		// 提取表单元素属性
		List<WebElement> inputs = driver.findElements(By.tagName("input"));
		if (!inputs.isEmpty()) {
			System.out.println("\n表单输入框信息：");
			for (int i = 0; i < Math.min(5, inputs.size()); i++) {
				WebElement input = inputs.get(i);
				String type = input.getAttribute("type");
				String name = input.getAttribute("name");
				String placeholder = input.getAttribute("placeholder");
				String value = input.getAttribute("value");

				System.out.println("  输入框" + (i + 1) + ":");
				System.out.println("    类型: " + (type != null ? type : "text"));
				System.out.println("    名称: " + (name != null ? name : "无"));
				if (placeholder != null && !placeholder.isEmpty()) {
					System.out.println("    占位符: " + placeholder);
				}
				if (value != null && !value.isEmpty()) {
					System.out.println("    默认值: " + value);
				}
			}
		}
	}

	/**
	 * 子任务3.3：提取页面窗口和浏览器信息
	 * 学习浏览器和窗口相关信息的获取
	 */
	@Test
	public void testExtractBrowserInfo() {
		System.out.println("=== 浏览器和页面信息 ===");

		// 页面基本信息
		System.out.println("页面标题: " + driver.getTitle());
		System.out.println("当前URL: " + driver.getCurrentUrl());
		System.out.println("页面源码长度: " + driver.getPageSource().length() + " 字符");

		// 窗口信息
		org.openqa.selenium.Dimension windowSize = driver.manage().window().getSize();
		System.out.println("窗口大小: " + windowSize.getWidth() + "x" + windowSize.getHeight());

		org.openqa.selenium.Point windowPosition = driver.manage().window().getPosition();
		System.out.println("窗口位置: (" + windowPosition.getX() + ", " + windowPosition.getY() + ")");

		// 窗口句柄信息
		String currentWindow = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		System.out.println("当前窗口句柄: " + currentWindow);
		System.out.println("所有窗口数量: " + allWindows.size());

		// JavaScript执行获取更多信息
		try {
			Object userAgent = ((org.openqa.selenium.JavascriptExecutor) driver)
					.executeScript("return navigator.userAgent;");
			System.out.println("浏览器信息: " + userAgent);

			Object screenInfo = ((org.openqa.selenium.JavascriptExecutor) driver)
					.executeScript("return screen.width + 'x' + screen.height;");
			System.out.println("屏幕分辨率: " + screenInfo);

			Object loadTime = ((org.openqa.selenium.JavascriptExecutor) driver)
					.executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
			System.out.println("页面加载时间: " + loadTime + "ms");

		} catch (Exception e) {
			System.out.println("JavaScript执行出错: " + e.getMessage());
		}
	}

	/**
	 * 子任务3.4：提取CSS样式信息
	 * 学习getCssValue()方法的使用
	 */
	@Test
	public void testExtractCssInfo() {
		System.out.println("=== CSS样式信息提取 ===");

		try {
			// 查找页面中的按钮元素
			List<WebElement> buttons = driver.findElements(By.tagName("button"));
			if (!buttons.isEmpty()) {
				System.out.println("按钮样式信息：");
				for (int i = 0; i < Math.min(3, buttons.size()); i++) {
					WebElement button = buttons.get(i);
					String text = button.getText().trim();

					System.out.println("  按钮" + (i + 1) + " (" + (text.isEmpty() ? "无文本" : text) + "):");
					System.out.println("    背景色: " + button.getCssValue("background-color"));
					System.out.println("    文字颜色: " + button.getCssValue("color"));
					System.out.println("    字体大小: " + button.getCssValue("font-size"));
					System.out.println("    边框: " + button.getCssValue("border"));
					System.out.println("    显示状态: " + button.getCssValue("display"));
					System.out.println("    是否可见: " + button.isDisplayed());
					System.out.println("    是否启用: " + button.isEnabled());
				}
			}

			// 查找页面主体的样式
			WebElement body = driver.findElement(By.tagName("body"));
			System.out.println("\n页面主体样式：");
			System.out.println("  背景色: " + body.getCssValue("background-color"));
			System.out.println("  字体: " + body.getCssValue("font-family"));
			System.out.println("  字体大小: " + body.getCssValue("font-size"));
			System.out.println("  行高: " + body.getCssValue("line-height"));

		} catch (Exception e) {
			System.out.println("CSS信息提取出错: " + e.getMessage());
		}
	}

	/**
	 * 子任务3.5：页面结构分析
	 * 分析页面的整体结构和元素分布
	 */
	@Test
	public void testPageStructureAnalysis() {
		System.out.println("=== 页面结构分析 ===");

		// 统计各种HTML标签的数量
		String[] commonTags = {"div", "span", "a", "button", "input", "img", "p", "h1", "h2", "h3", "ul", "li"};

		System.out.println("页面元素统计：");
		for (String tag : commonTags) {
			List<WebElement> elements = driver.findElements(By.tagName(tag));
			if (elements.size() > 0) {
				System.out.println("  <" + tag + ">: " + elements.size() + " 个");
			}
		}

		// 分析表单元素
		List<WebElement> forms = driver.findElements(By.tagName("form"));
		System.out.println("\n表单分析：");
		System.out.println("  表单数量: " + forms.size());

		for (int i = 0; i < forms.size(); i++) {
			WebElement form = forms.get(i);
			String action = form.getAttribute("action");
			String method = form.getAttribute("method");

			System.out.println("  表单" + (i + 1) + ":");
			System.out.println("    提交地址: " + (action != null ? action : "当前页面"));
			System.out.println("    提交方法: " + (method != null ? method.toUpperCase() : "GET"));

			List<WebElement> formInputs = form.findElements(By.tagName("input"));
			System.out.println("    输入框数量: " + formInputs.size());
		}

		// 分析页面加载的外部资源
		System.out.println("\n外部资源分析：");

		List<WebElement> scripts = driver.findElements(By.tagName("script"));
		int externalScripts = 0;
		for (WebElement script : scripts) {
			String src = script.getAttribute("src");
			if (src != null && !src.isEmpty()) {
				externalScripts++;
			}
		}
		System.out.println("  JavaScript文件: " + externalScripts + " 个");

		List<WebElement> stylesheets = driver.findElements(By.cssSelector("link[rel='stylesheet']"));
		System.out.println("  CSS样式文件: " + stylesheets.size() + " 个");

		List<WebElement> allImages = driver.findElements(By.tagName("img"));
		System.out.println("  图片文件: " + allImages.size() + " 个");
	}
}