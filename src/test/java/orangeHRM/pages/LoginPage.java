package orangeHRM.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage{

	private final By usernameField = By.name("username");
	private final By passwordField = By.name("password");
	private final By loginButton = By.xpath("//button[@type='submit']");
	private final By dashboardHeader = By.className("oxd-topbar-header-breadcrumb-module");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void login(String username, String password) {
		WebElement usernameElement = wait.until(
				ExpectedConditions.visibilityOfElementLocated(usernameField)
		);
		usernameElement.clear();
		usernameElement.sendKeys(username);

		driver.findElement(passwordField).clear();
		driver.findElement(passwordField).sendKeys(password);

		WebElement loginBtn = wait.until(
				ExpectedConditions.elementToBeClickable(loginButton)
		);
		loginBtn.click();
	}

	public boolean isLoginSuccessful() {
		try {
			WebElement dashboard = wait.until(
					ExpectedConditions.visibilityOfElementLocated(dashboardHeader)
			);
			return dashboard.getText().equals("Dashboard");
		} catch (Exception e) {
			return false;
		}
	}

	public String getErrorMessage() {
		try {
			WebElement errorElement = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.className("oxd-alert-content-text"))
			);
			return errorElement.getText();
		} catch (Exception e) {
			return "";
		}
	}
}
