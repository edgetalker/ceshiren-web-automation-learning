package orangeHRM;

import orangeHRM.utils.LoginHelper;
import orangeHRM.utils.PIMHelper;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class PIMTest {
	private WebDriver driver;
	private String testFirstName;
	private String testLastName;
	private String employeeId;

	@BeforeEach
	public void setUp() {
		// 配置Chrome选项
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--remote-allow-origins=*");

		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

		// 登录
		assertTrue(LoginHelper.loginAsAdmin(driver), "管理员登录失败");
		System.out.println("✓ 登录成功");

		// 准备测试数据
		String suffix = String.valueOf(new Random().nextInt(1000));
		testFirstName = "TestUser" + suffix;
		testLastName = "Employee" + suffix;
	}

	@AfterEach
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	@Order(1)
	@DisplayName("创建员工测试")
	public void testCreateEmployee() {
		// 导航到PIM模块
		PIMHelper.navigateToPIM(driver);

		// 创建员工
		employeeId = PIMHelper.addEmployee(driver, testFirstName, testLastName);

		// 验证创建成功
		assertNotNull(employeeId, "员工ID不应为空");
		assertFalse(employeeId.trim().isEmpty(), "员工ID不应为空字符串");

		System.out.println("✓ 员工创建测试通过");
	}

	@Test
	@Order(2)
	@DisplayName("查询员工测试")
	public void testReadEmployee() {
		// 先创建员工
		testCreateEmployee();

		// 导航到员工列表
		PIMHelper.goToEmployeeList(driver);

		// 搜索员工
		boolean found = PIMHelper.searchEmployeeById(driver, employeeId);

		// 验证找到员工
		assertTrue(found, "应该能够找到刚创建的员工");

		System.out.println("✓ 员工查询测试通过");
	}

	@Test
	@Order(3)
	@DisplayName("更新员工测试")
	public void testUpdateEmployee() {
		// 先创建员工
		testCreateEmployee();

		// 导航到员工列表并搜索
		PIMHelper.goToEmployeeList(driver);
		assertTrue(PIMHelper.searchEmployeeById(driver, employeeId), "搜索员工失败");

		// 更新员工信息
		String newFirstName = "Updated" + testFirstName;
		boolean updated = PIMHelper.updateFirstEmployee(driver, newFirstName);

		// 验证更新成功
		assertTrue(updated, "员工信息更新失败");

		System.out.println("✓ 员工更新测试通过");
	}

	@Test
	@Order(4)
	@DisplayName("删除员工测试")
	public void testDeleteEmployee() {
		// 先创建员工
		testCreateEmployee();

		// 导航到员工列表并搜索
		PIMHelper.goToEmployeeList(driver);
		assertTrue(PIMHelper.searchEmployeeById(driver, employeeId), "搜索员工失败");

		// 删除员工
		boolean deleted = PIMHelper.deleteFirstEmployee(driver);
		assertTrue(deleted, "删除员工失败");

		// 验证删除成功 - 重新搜索应该找不到
		boolean stillExists = PIMHelper.searchEmployeeById(driver, employeeId);
		assertFalse(stillExists, "员工删除后仍然存在");

		System.out.println("✓ 员工删除测试通过");
	}

	@Test
	@Order(5)
	@DisplayName("完整CRUD流程测试")
	public void testCompleteCRUDFlow() {
		System.out.println("开始完整CRUD流程测试...");

		// 1. Create - 创建
		PIMHelper.navigateToPIM(driver);
		employeeId = PIMHelper.addEmployee(driver, testFirstName, testLastName);
		assertNotNull(employeeId, "创建员工失败");
		System.out.println("✓ Create - 员工创建成功");

		// 2. Read - 查询
		PIMHelper.goToEmployeeList(driver);
		assertTrue(PIMHelper.searchEmployeeById(driver, employeeId), "查询员工失败");
		System.out.println("✓ Read - 员工查询成功");

		// 3. Update - 更新
		String updatedName = "Updated" + testFirstName;
		assertTrue(PIMHelper.updateFirstEmployee(driver, updatedName), "更新员工失败");
		System.out.println("✓ Update - 员工更新成功");

		// 4. 验证更新（简化验证 - 重新搜索员工ID确认员工仍存在）
		PIMHelper.goToEmployeeList(driver);
		assertTrue(PIMHelper.searchEmployeeById(driver, employeeId), "更新后员工消失了");
		System.out.println("✓ Update Verification - 员工更新后仍然存在");

		// 5. Delete - 删除
		assertTrue(PIMHelper.deleteFirstEmployee(driver), "删除员工失败");
		System.out.println("✓ Delete - 员工删除成功");

		// 6. 验证删除
		assertFalse(PIMHelper.searchEmployeeById(driver, employeeId), "员工删除后仍存在");
		System.out.println("✓ Delete Verification - 删除验证成功");

		System.out.println("✅ 完整CRUD流程测试通过！");
	}
}
