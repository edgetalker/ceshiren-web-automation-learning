package orangeHRM.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginHelper {

	public static boolean login(WebDriver driver,String username,String password){
		try{
			//进入网站
			driver.get("https://opensource-demo.orangehrmlive.com/");

			//设置显式等待
			WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(10));

			//登录
			WebElement ele1=wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='username']"))
			);
			ele1.clear();
			ele1.sendKeys(username);

			WebElement ele2=driver.findElement(By.xpath("//input[@name='password']"));
			ele2.clear();
			ele2.sendKeys(password);

			driver.findElement(By.xpath("//button[@type='submit']")).click();

			WebElement dashboard=wait.until(
					ExpectedConditions.visibilityOfElementLocated(
							By.className("oxd-topbar-header-breadcrumb-module")
					)
			);
			return dashboard.getText().equals("Dashboard");

		}catch(Exception e){
			System.err.println("登录失败："+e.getMessage());
			return false;
		}
	}
	public static boolean loginAsAdmin(WebDriver driver){
		return login(driver,"Admin","admin123");
	}
}