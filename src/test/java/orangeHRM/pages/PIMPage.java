package orangeHRM.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class PIMPage extends BasePage{

	// 原有的定位器保持不变...
	private final By pimMenuItem = By.xpath("//span[text()='PIM']");
	private final By addEmployeeTab = By.xpath("//a[text()='Add Employee']");
	private final By employeeListTab = By.xpath("//a[text()='Employee List']");

	// 表单元素
	private final By firstNameField = By.name("firstName");
	private final By middleNameField = By.name("middleName");
	private final By lastNameField = By.name("lastName");
	private final By employeeIdField = By.xpath("//label[text()='Employee Id']/../../div[2]/input");

	// 修复后的复选框定位器 - 点击外层容器而不是input
	private final By createLoginToggle = By.xpath("//span[contains(@class,'oxd-switch-input')]");
	private final By createLoginContainer = By.xpath("//div[@class='oxd-switch-wrapper']");

	private final By usernameField = By.xpath("//label[text()='Username']/../../div[2]/input");
	private final By passwordField = By.xpath("//label[text()='Password']/../../div[2]/input");
	private final By confirmPasswordField = By.xpath("//label[text()='Confirm Password']/../../div[2]/input");
	private final By saveButton = By.xpath("//button[@type='submit']");

	// 搜索相关元素
	private final By searchEmployeeIdField = By.xpath("//label[text()='Employee Id']/../../div[2]/input");
	private final By searchEmployeeNameField = By.xpath("//input[@placeholder='Type for hints...']");
	private final By searchButton = By.xpath("//button[@type='submit']");
	private final By employeeListTable = By.className("oxd-table-body");
	private final By noRecordsFound = By.xpath("//span[text()='No Records Found']");
	private final By resetButton = By.xpath("//button[normalize-space()='Reset']");
	private final By deleteButton = By.xpath("//i[contains(@class,'bi-trash')]");
	private final By confirmDeleteButton = By.xpath("//button[contains(@class,'oxd-button--label-danger')]");
	private final By editButton = By.xpath("//i[contains(@class,'bi-pencil-fill')]");

	// 成功消息
	private final By successMessage = By.xpath("//p[contains(@class,'oxd-text--toast-message')]");

	public PIMPage(WebDriver driver) {
		super(driver);
	}

	public void navigateToPIM() {
		WebElement pimMenu = wait.until(
				ExpectedConditions.elementToBeClickable(pimMenuItem)
		);
		pimMenu.click();

		// 等待PIM页面加载完成
		wait.until(ExpectedConditions.visibilityOfElementLocated(addEmployeeTab));
	}

	public void clickAddEmployee() {
		WebElement addEmpTab = wait.until(
				ExpectedConditions.elementToBeClickable(addEmployeeTab)
		);
		addEmpTab.click();

		// 等待添加员工表单加载
		wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
	}

	public void clickEmployeeList() {
		WebElement empListTab = wait.until(
				ExpectedConditions.elementToBeClickable(employeeListTab)
		);
		empListTab.click();

		// 等待员工列表加载
		wait.until(ExpectedConditions.visibilityOfElementLocated(searchButton));
	}

	public String addEmployee(String firstName, String middleName, String lastName,
							  String employeeId, boolean createLogin, String username,
							  String password) {

		// 填写基本信息
		WebElement firstNameElement = wait.until(
				ExpectedConditions.visibilityOfElementLocated(firstNameField)
		);
		firstNameElement.clear();
		firstNameElement.sendKeys(firstName);

		if (middleName != null && !middleName.isEmpty()) {
			driver.findElement(middleNameField).clear();
			driver.findElement(middleNameField).sendKeys(middleName);
		}

		driver.findElement(lastNameField).clear();
		driver.findElement(lastNameField).sendKeys(lastName);

		// 获取或设置员工ID
		WebElement empIdElement = driver.findElement(employeeIdField);
		String actualEmployeeId = empIdElement.getAttribute("value");
		if (employeeId != null && !employeeId.isEmpty()) {
			empIdElement.clear();
			empIdElement.sendKeys(employeeId);
			actualEmployeeId = employeeId;
		}

		// 修复后的复选框处理逻辑
		if (createLogin) {
			try {
				// 方法1：尝试点击切换容器
				WebElement toggleContainer = wait.until(
						ExpectedConditions.elementToBeClickable(createLoginContainer)
				);
				toggleContainer.click();

				// 等待用户名字段出现，确认切换成功
				wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));

			} catch (Exception e1) {
				try {
					// 方法2：如果方法1失败，尝试点击span元素
					WebElement toggleSpan = driver.findElement(createLoginToggle);
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggleSpan);

					wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));

				} catch (Exception e2) {
					// 方法3：使用Actions类进行点击
					try {
						WebElement toggleElement = driver.findElement(createLoginToggle);
						Actions actions = new Actions(driver);
						actions.moveToElement(toggleElement).click().perform();

						wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));

					} catch (Exception e3) {
						System.err.println("无法点击创建登录账户切换按钮，跳过登录账户创建");
						return actualEmployeeId;
					}
				}
			}

			// 填写登录信息
			try {
				driver.findElement(usernameField).sendKeys(username);
				driver.findElement(passwordField).sendKeys(password);
				driver.findElement(confirmPasswordField).sendKeys(password);
			} catch (Exception e) {
				System.err.println("填写登录信息时出错，但继续保存员工基本信息");
			}
		}

		// 提交表单
		WebElement saveBtn = wait.until(
				ExpectedConditions.elementToBeClickable(saveButton)
		);
		saveBtn.click();

		return actualEmployeeId;
	}

	public boolean isEmployeeAddedSuccessfully() {
		try {
			WebElement success = wait.until(
					ExpectedConditions.visibilityOfElementLocated(successMessage)
			);
			return success.getText().contains("Successfully Saved");
		} catch (Exception e) {
			return false;
		}
	}

	public void searchEmployeeById(String employeeId) {
		// 首先重置搜索条件
		try {
			WebElement resetBtn = driver.findElement(resetButton);
			if (resetBtn.isDisplayed()) {
				resetBtn.click();
				Thread.sleep(1000); // 等待重置完成
			}
		} catch (Exception e) {
			// 重置按钮不存在或不可见，继续执行
		}

		WebElement searchField = wait.until(
				ExpectedConditions.visibilityOfElementLocated(searchEmployeeIdField)
		);
		searchField.clear();
		searchField.sendKeys(employeeId);

		// 点击搜索按钮
		WebElement searchBtn = wait.until(
				ExpectedConditions.elementToBeClickable(searchButton)
		);
		searchBtn.click();

		// 等待搜索结果加载
		try {
			wait.until(ExpectedConditions.or(
					ExpectedConditions.visibilityOfElementLocated(employeeListTable),
					ExpectedConditions.visibilityOfElementLocated(noRecordsFound)
			));
			Thread.sleep(2000); // 额外等待确保结果完全加载
		} catch (Exception e) {
			System.err.println("搜索结果加载超时");
		}
	}

	public void searchEmployeeByName(String firstName) {
		// 首先重置搜索条件
		try {
			WebElement resetBtn = driver.findElement(resetButton);
			if (resetBtn.isDisplayed()) {
				resetBtn.click();
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			// 继续执行
		}

		WebElement searchField = wait.until(
				ExpectedConditions.visibilityOfElementLocated(searchEmployeeNameField)
		);
		searchField.clear();
		searchField.sendKeys(firstName);

		// 等待下拉提示出现并选择
		try {
			Thread.sleep(1000); // 等待自动完成提示
			// 如果有下拉选项，点击第一个
			List<WebElement> suggestions = driver.findElements(
					By.xpath("//div[@role='listbox']//span")
			);
			if (!suggestions.isEmpty()) {
				suggestions.get(0).click();
			}
		} catch (Exception e) {
			// 没有下拉提示，直接搜索
		}

		WebElement searchBtn = wait.until(
				ExpectedConditions.elementToBeClickable(searchButton)
		);
		searchBtn.click();

		// 等待搜索结果
		try {
			wait.until(ExpectedConditions.or(
					ExpectedConditions.visibilityOfElementLocated(employeeListTable),
					ExpectedConditions.visibilityOfElementLocated(noRecordsFound)
			));
			Thread.sleep(2000);
		} catch (Exception e) {
			System.err.println("按姓名搜索结果加载超时");
		}
	}

	public boolean isEmployeeFoundInList(String employeeId, String firstName, String lastName) {
		try {
			// 先检查是否显示"No Records Found"
			if (isNoRecordsFound()) {
				System.out.println("搜索结果显示：No Records Found");
				return false;
			}

			// 等待表格加载完成
			Thread.sleep(1000);

			List<WebElement> rows = driver.findElements(
					By.xpath("//div[@class='oxd-table-body']//div[@role='row']")
			);

			System.out.println("找到 " + rows.size() + " 行数据");

			for (int i = 0; i < rows.size(); i++) {
				WebElement row = rows.get(i);
				List<WebElement> cells = row.findElements(By.xpath(".//div[@role='cell']"));

				if (cells.size() >= 4) {
					String foundId = cells.get(1).getText().trim();
					String foundFirstName = cells.get(2).getText().trim();
					String foundLastName = cells.get(3).getText().trim();

					System.out.println("第" + (i+1) + "行数据: ID=" + foundId +
							", FirstName=" + foundFirstName +
							", LastName=" + foundLastName);

					if (foundId.equals(employeeId) &&
							foundFirstName.equals(firstName) &&
							foundLastName.equals(lastName)) {
						System.out.println("找到匹配的员工记录");
						return true;
					}
				}
			}

			System.out.println("在 " + rows.size() + " 条记录中未找到匹配的员工");
			return false;

		} catch (Exception e) {
			System.err.println("验证员工列表时出错: " + e.getMessage());
			return false;
		}
	}

	public boolean isNoRecordsFound() {
		try {
			driver.findElement(noRecordsFound);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public void editFirstEmployeeInList() {
		try {
			WebElement editBtn = wait.until(
					ExpectedConditions.elementToBeClickable(editButton)
			);
			editBtn.click();

			// 等待编辑页面加载
			wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
		} catch (Exception e) {
			throw new RuntimeException("无法点击编辑按钮或编辑页面加载失败", e);
		}
	}

	public void updateEmployeeFirstName(String newFirstName) {
		WebElement firstNameElement = wait.until(
				ExpectedConditions.visibilityOfElementLocated(firstNameField)
		);
		firstNameElement.clear();
		firstNameElement.sendKeys(newFirstName);

		WebElement saveBtn = wait.until(
				ExpectedConditions.elementToBeClickable(saveButton)
		);
		saveBtn.click();
	}

	public boolean isEmployeeUpdatedSuccessfully() {
		try {
			WebElement success = wait.until(
					ExpectedConditions.visibilityOfElementLocated(successMessage)
			);
			return success.getText().contains("Successfully Updated");
		} catch (Exception e) {
			return false;
		}
	}

	public void deleteFirstEmployeeInList() {
		try {
			WebElement deleteBtn = wait.until(
					ExpectedConditions.elementToBeClickable(deleteButton)
			);
			deleteBtn.click();

			// 等待确认删除对话框并点击确认
			WebElement confirmBtn = wait.until(
					ExpectedConditions.elementToBeClickable(confirmDeleteButton)
			);
			confirmBtn.click();

			// 等待删除操作完成
			Thread.sleep(2000);

		} catch (Exception e) {
			throw new RuntimeException("删除员工操作失败", e);
		}
	}

	public boolean isEmployeeDeletedSuccessfully() {
		try {
			WebElement success = wait.until(
					ExpectedConditions.visibilityOfElementLocated(successMessage)
			);
			return success.getText().contains("Successfully Deleted");
		} catch (Exception e) {
			return false;
		}
	}
}
