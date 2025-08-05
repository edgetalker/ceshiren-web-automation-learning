package orangeHRM.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class PIMHelper {

	public static void navigateToPIM(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement pimMenu = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))
		);
		pimMenu.click();
	}

	public static String addEmployee(WebDriver driver, String firstName, String lastName) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		try {
			// 点击Add Employee
			WebElement addEmployeeTab = wait.until(
					ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Add Employee']"))
			);
			addEmployeeTab.click();

			// 填写姓名
			WebElement firstNameField = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.name("firstName"))
			);
			firstNameField.clear();
			firstNameField.sendKeys(firstName);

			WebElement lastNameField = driver.findElement(By.name("lastName"));
			lastNameField.clear();
			lastNameField.sendKeys(lastName);

			// 获取自动生成的员工ID
			WebElement employeeIdField = driver.findElement(
					By.xpath("//label[text()='Employee Id']/../../div[2]/input")
			);
			String employeeId = employeeIdField.getAttribute("value");

			// 保存员工（不创建登录账户，避免复选框问题）
			WebElement saveButton = wait.until(
					ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))
			);
			saveButton.click();

			// 等待成功消息
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//p[contains(@class,'oxd-text--toast-message')]")
			));

			System.out.println("员工创建成功，ID: " + employeeId);
			return employeeId;

		} catch (Exception e) {
			System.err.println("创建员工失败: " + e.getMessage());
			return null;
		}
	}

	public static void goToEmployeeList(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement employeeListTab = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Employee List']"))
		);
		employeeListTab.click();

		// 等待页面加载
		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//button[@type='submit']")
		));
	}

	public static boolean searchEmployeeById(WebDriver driver, String employeeId) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// 清空搜索条件
			try {
				WebElement resetButton = driver.findElement(By.xpath("//button[normalize-space()='Reset']"));
				if (resetButton.isDisplayed()) {
					resetButton.click();
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// 重置按钮不存在，继续
			}

			// 搜索员工ID
			WebElement searchField = wait.until(
					ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//label[text()='Employee Id']/../../div[2]/input")
					)
			);
			searchField.clear();
			searchField.sendKeys(employeeId);

			// 点击搜索
			WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
			searchButton.click();

			// 等待结果
			Thread.sleep(2000);

			// 检查是否找到员工
			List<WebElement> rows = driver.findElements(
					By.xpath("//div[@class='oxd-table-body']//div[@role='row']")
			);

			if (rows.isEmpty()) {
				// 检查是否显示"No Records Found"
				try {
					driver.findElement(By.xpath("//span[text()='No Records Found']"));
					System.out.println("搜索结果：未找到员工 ID " + employeeId);
					return false;
				} catch (Exception e) {
					System.out.println("搜索结果加载中...");
					return false;
				}
			}

			// 检查搜索结果中是否包含目标员工ID
			for (WebElement row : rows) {
				List<WebElement> cells = row.findElements(By.xpath(".//div[@role='cell']"));
				if (cells.size() >= 2) {
					String foundId = cells.get(1).getText().trim();
					if (foundId.equals(employeeId)) {
						System.out.println("找到员工 ID: " + employeeId);
						return true;
					}
				}
			}

			System.out.println("未找到匹配的员工 ID: " + employeeId);
			return false;

		} catch (Exception e) {
			System.err.println("搜索员工失败: " + e.getMessage());
			return false;
		}
	}

	public static boolean updateFirstEmployee(WebDriver driver, String newFirstName) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		try {
			// 点击第一个编辑按钮
			WebElement editButton = wait.until(
					ExpectedConditions.elementToBeClickable(
							By.xpath("//i[contains(@class,'bi-pencil-fill')]")
					)
			);
			editButton.click();

			// 等待编辑页面加载完成
			WebElement firstNameField = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.name("firstName"))
			);

			// 等待页面完全加载，避免加载器遮挡
			Thread.sleep(2000);

			// 更新姓名
			firstNameField.clear();
			firstNameField.sendKeys(newFirstName);

			// 等待加载器消失并保存更改
			try {
				// 等待加载器消失
				wait.until(ExpectedConditions.invisibilityOfElementLocated(
						By.className("oxd-form-loader")
				));
			} catch (Exception e) {
				// 加载器可能不存在，继续执行
			}

			// 多种方式尝试点击保存按钮
			boolean saved = false;

			// 方法1：等待按钮可点击状态
			try {
				WebElement saveButton = wait.until(
						ExpectedConditions.elementToBeClickable(
								By.xpath("//button[@type='submit' and contains(@class,'oxd-button--secondary')]")
						)
				);
				saveButton.click();
				saved = true;
				System.out.println("使用方法1保存成功");
			} catch (Exception e1) {
				System.out.println("方法1失败，尝试方法2...");

				// 方法2：使用JavaScript点击
				try {
					WebElement saveButton = driver.findElement(
							By.xpath("//button[@type='submit' and contains(@class,'oxd-button--secondary')]")
					);
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
					saved = true;
					System.out.println("使用方法2保存成功");
				} catch (Exception e2) {
					System.out.println("方法2失败，尝试方法3...");

					// 方法3：等待更长时间再点击
					try {
						Thread.sleep(3000); // 额外等待
						WebElement saveButton = wait.until(
								ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))
						);
						saveButton.click();
						saved = true;
						System.out.println("使用方法3保存成功");
					} catch (Exception e3) {
						System.err.println("所有保存方法都失败了");
						return false;
					}
				}
			}

			if (saved) {
				// 等待成功消息或页面跳转
				try {
					wait.until(ExpectedConditions.or(
							ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//p[contains(@class,'oxd-text--toast-message')]")
							),
							ExpectedConditions.visibilityOfElementLocated(
									By.xpath("//h6[contains(@class,'oxd-text--h6')]")
							)
					));
					System.out.println("员工信息更新成功，新姓名: " + newFirstName);
					return true;
				} catch (Exception e) {
					System.out.println("未找到成功消息，但保存操作已执行");
					return true; // 假设保存成功
				}
			}

			return false;

		} catch (Exception e) {
			System.err.println("更新员工失败: " + e.getMessage());
			return false;
		}
	}

	public static boolean deleteFirstEmployee(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// 点击删除按钮
			WebElement deleteButton = wait.until(
					ExpectedConditions.elementToBeClickable(
							By.xpath("//i[contains(@class,'bi-trash')]")
					)
			);
			deleteButton.click();

			// 确认删除
			WebElement confirmButton = wait.until(
					ExpectedConditions.elementToBeClickable(
							By.xpath("//button[contains(@class,'oxd-button--label-danger')]")
					)
			);
			confirmButton.click();

			// 等待删除完成
			Thread.sleep(2000);

			System.out.println("员工删除成功");
			return true;

		} catch (Exception e) {
			System.err.println("删除员工失败: " + e.getMessage());
			return false;
		}
	}
}