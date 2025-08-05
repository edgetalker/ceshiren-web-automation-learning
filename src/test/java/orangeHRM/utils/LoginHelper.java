package orangeHRM.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginHelper {

	public static boolean login(WebDriver driver, String username, String password) {
		try {
			driver.get("https://opensource-demo.orangehrmlive.com");

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			// 登录
			WebElement usernameField = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.name("username"))
			);
			usernameField.clear();
			usernameField.sendKeys(username);

			driver.findElement(By.name("password")).clear();
			driver.findElement(By.name("password")).sendKeys(password);

			driver.findElement(By.xpath("//button[@type='submit']")).click();

			// 验证登录成功
			WebElement dashboard = wait.until(
					ExpectedConditions.visibilityOfElementLocated(
							By.className("oxd-topbar-header-breadcrumb-module")
					)
			);

			return dashboard.getText().equals("Dashboard");

		} catch (Exception e) {
			System.err.println("登录失败: " + e.getMessage());
			return false;
		}
	}

	public static boolean loginAsAdmin(WebDriver driver) {
		return login(driver, "Admin", "admin123");
	}
}