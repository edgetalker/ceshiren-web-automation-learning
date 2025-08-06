package Booker;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 改进的API自动化测试套件
 * 修复了原版本的设计问题：
 * 1. 移除重复健康检查
 * 2. 确保测试独立性
 * 3. 改进错误处理
 * 4. 动态测试数据生成
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("改进的API测试套件")
public class RestfulBookerApiTest {

	// API配置
	private static final String BASE_URL = "https://restful-booker.herokuapp.com";
	private static final String AUTH_USERNAME = "admin";
	private static final String AUTH_PASSWORD = "password123";

	// 测试上下文 - 使用线程安全的方式
	private String currentAuthToken;
	private Integer currentBookingId;
	private final String testId = UUID.randomUUID().toString().substring(0, 8);

	@BeforeAll
	public static void globalSetUp() {
		// 配置RestAssured
		RestAssured.baseURI = BASE_URL;
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		System.out.println("🚀 开始API测试套件");

		// 只在开始时检查一次API可用性
		try {
			given()
					.when()
					.get("/ping")
					.then()
					.statusCode(201);
			System.out.println("✅ API服务可用");
		} catch (Exception e) {
			System.err.println("❌ API服务不可用: " + e.getMessage());
			System.err.println("💡 建议：请检查网络连接或使用替代API");
			// 让测试套件快速失败
			Assumptions.assumeTrue(false, "API服务不可用，跳过测试套件");
		}
	}

	// ==================== 独立的健康检查测试 ====================

	@Test
	@Order(1)
	@DisplayName("API健康检查")
	public void testApiHealth() {
		System.out.println("🏥 执行API健康检查...");

		given()
				.when()
				.get("/ping")
				.then()
				.statusCode(201)
				.time(lessThan(5000L)); // 响应时间检查

		System.out.println("✅ API健康检查通过");
	}

	// ==================== 独立的认证测试 ====================

	@Test
	@Order(2)
	@DisplayName("用户认证测试")
	public void testAuthentication() {
		System.out.println("🔐 开始用户认证测试...");

		Map<String, String> credentials = createAuthCredentials();

		Response response = given()
				.contentType(ContentType.JSON)
				.body(credentials)
				.when()
				.post("/auth")
				.then()
				.statusCode(200)
				.body("token", notNullValue())
				.body("token", not(emptyString()))
				.extract().response();

		String token = response.jsonPath().getString("token");
		assertNotNull(token, "认证token不应为空");

		// 存储到实例变量，不使用静态变量
		this.currentAuthToken = token;

		System.out.println("✅ 用户认证测试通过，token: " + token.substring(0, 8) + "...");
	}

	// ==================== 独立的CRUD测试 ====================

	@Test
	@Order(3)
	@DisplayName("创建预订测试")
	public void testCreateBooking() {
		System.out.println("📝 开始创建预订测试...");

		Map<String, Object> bookingData = createTestBookingData();

		Response response = given()
				.contentType(ContentType.JSON)
				.body(bookingData)
				.when()
				.post("/booking")
				.then()
				.statusCode(200)
				.body("bookingid", notNullValue())
				.body("booking.firstname", equalTo(bookingData.get("firstname")))
				.extract().response();

		Integer bookingId = response.jsonPath().getInt("bookingid");
		assertTrue(bookingId > 0, "预订ID应该大于0");

		// 存储到实例变量
		this.currentBookingId = bookingId;

		System.out.println("✅ 创建预订测试通过，ID: " + bookingId);
	}

	@Test
	@Order(4)
	@DisplayName("查询预订测试")
	public void testReadBooking() {
		System.out.println("🔍 开始查询预订测试...");

		// 如果需要预订ID，就创建一个（保证测试独立性）
		Integer bookingId = ensureBookingExists();

		given()
				.when()
				.get("/booking/" + bookingId)
				.then()
				.statusCode(200)
				.body("firstname", notNullValue())
				.body("lastname", notNullValue());

		System.out.println("✅ 查询预订测试通过");
	}

	@Test
	@Order(5)
	@DisplayName("更新预订测试")
	public void testUpdateBooking() {
		System.out.println("✏️ 开始更新预订测试...");

		// 确保有认证token和预订ID（保证测试独立性）
		String authToken = ensureAuthTokenExists();
		Integer bookingId = ensureBookingExists();

		Map<String, Object> updateData = createTestBookingData();
		updateData.put("firstname", "Updated" + testId);

		given()
				.contentType(ContentType.JSON)
				.cookie("token", authToken)
				.body(updateData)
				.when()
				.put("/booking/" + bookingId)
				.then()
				.statusCode(200)
				.body("firstname", equalTo("Updated" + testId));

		System.out.println("✅ 更新预订测试通过");
	}

	@Test
	@Order(6)
	@DisplayName("删除预订测试")
	public void testDeleteBooking() {
		System.out.println("🗑️ 开始删除预订测试...");

		// 确保有认证token和预订ID
		String authToken = ensureAuthTokenExists();
		Integer bookingId = ensureBookingExists();

		given()
				.cookie("token", authToken)
				.when()
				.delete("/booking/" + bookingId)
				.then()
				.statusCode(201); // Restful-Booker的已知行为

		// 验证删除成功
		given()
				.when()
				.get("/booking/" + bookingId)
				.then()
				.statusCode(404);

		System.out.println("✅ 删除预订测试通过");
	}

	// ==================== 参数化测试 ====================

	@ParameterizedTest
	@Order(7)
	@DisplayName("认证失败场景测试")
	@CsvSource({
			"'', password123, 空用户名",
			"admin, '', 空密码",
			"wronguser, password123, 错误用户名",
			"admin, wrongpassword, 错误密码"
	})
	public void testAuthenticationFailure(String username, String password, String scenario) {
		System.out.println("🔒 测试认证失败场景: " + scenario);

		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", username);
		credentials.put("password", password);

		given()
				.contentType(ContentType.JSON)
				.body(credentials)
				.when()
				.post("/auth")
				.then()
				.statusCode(200)
				.body("reason", equalTo("Bad credentials"));

		System.out.println("✅ " + scenario + "测试通过");
	}

	// ==================== 错误处理测试 ====================

	@Test
	@Order(8)
	@DisplayName("不存在资源测试")
	public void testNonExistentResource() {
		System.out.println("❌ 测试访问不存在的资源...");

		int nonExistentId = 999999;

		given()
				.when()
				.get("/booking/" + nonExistentId)
				.then()
				.statusCode(404);

		System.out.println("✅ 不存在资源测试通过");
	}

	@Test
	@Order(9)
	@DisplayName("无权限操作测试")
	public void testUnauthorizedOperation() {
		System.out.println("🚫 测试无权限操作...");

		Integer bookingId = ensureBookingExists();

		// 不提供认证token尝试删除
		given()
				.when()
				.delete("/booking/" + bookingId)
				.then()
				.statusCode(403);

		System.out.println("✅ 无权限操作测试通过");
	}

	// ==================== 完整流程测试 ====================

	@Test
	@Order(10)
	@DisplayName("完整业务流程测试")
	public void testCompleteWorkflow() {
		System.out.println("🔄 开始完整业务流程测试...");

		String workflowTestId = UUID.randomUUID().toString().substring(0, 6);

		// 1. 获取认证
		String token = createAuthToken();
		System.out.println("✓ 1. 认证成功");

		// 2. 创建预订
		Map<String, Object> bookingData = createTestBookingData();
		bookingData.put("firstname", "Workflow" + workflowTestId);

		Response createResponse = given()
				.contentType(ContentType.JSON)
				.body(bookingData)
				.post("/booking");

		assertEquals(200, createResponse.getStatusCode());
		Integer workflowBookingId = createResponse.jsonPath().getInt("bookingid");
		System.out.println("✓ 2. 创建预订成功，ID: " + workflowBookingId);

		// 3. 查询预订
		given()
				.get("/booking/" + workflowBookingId)
				.then()
				.statusCode(200)
				.body("firstname", equalTo("Workflow" + workflowTestId));
		System.out.println("✓ 3. 查询预订成功");

		// 4. 更新预订
		bookingData.put("firstname", "UpdatedWorkflow" + workflowTestId);
		given()
				.contentType(ContentType.JSON)
				.cookie("token", token)
				.body(bookingData)
				.put("/booking/" + workflowBookingId)
				.then()
				.statusCode(200);
		System.out.println("✓ 4. 更新预订成功");

		// 5. 删除预订
		given()
				.cookie("token", token)
				.delete("/booking/" + workflowBookingId)
				.then()
				.statusCode(201);
		System.out.println("✓ 5. 删除预订成功");

		// 6. 验证删除
		given()
				.get("/booking/" + workflowBookingId)
				.then()
				.statusCode(404);
		System.out.println("✓ 6. 删除验证成功");

		System.out.println("🎉 完整业务流程测试通过！");
	}

	// ==================== 辅助方法 - 确保测试独立性 ====================

	/**
	 * 确保认证token存在，如果不存在就创建一个
	 */
	private String ensureAuthTokenExists() {
		if (currentAuthToken == null) {
			currentAuthToken = createAuthToken();
		}
		return currentAuthToken;
	}

	/**
	 * 确保预订存在，如果不存在就创建一个
	 */
	private Integer ensureBookingExists() {
		if (currentBookingId == null) {
			currentBookingId = createTestBooking();
		}
		return currentBookingId;
	}

	/**
	 * 创建认证token
	 */
	private String createAuthToken() {
		Map<String, String> credentials = createAuthCredentials();

		Response response = given()
				.contentType(ContentType.JSON)
				.body(credentials)
				.post("/auth");

		return response.jsonPath().getString("token");
	}

	/**
	 * 创建测试预订
	 */
	private Integer createTestBooking() {
		Map<String, Object> bookingData = createTestBookingData();

		Response response = given()
				.contentType(ContentType.JSON)
				.body(bookingData)
				.post("/booking");

		return response.jsonPath().getInt("bookingid");
	}

	/**
	 * 创建认证凭据
	 */
	private Map<String, String> createAuthCredentials() {
		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", AUTH_USERNAME);
		credentials.put("password", AUTH_PASSWORD);
		return credentials;
	}

	/**
	 * 创建测试预订数据 - 动态生成
	 */
	private Map<String, Object> createTestBookingData() {
		Map<String, Object> bookingData = new HashMap<>();
		bookingData.put("firstname", "Test" + testId);
		bookingData.put("lastname", "User" + testId);
		bookingData.put("totalprice", 100 + (int)(Math.random() * 200));
		bookingData.put("depositpaid", true);

		// 动态生成未来的日期
		LocalDate checkinDate = LocalDate.now().plusDays(30);
		LocalDate checkoutDate = checkinDate.plusDays(5);

		Map<String, String> dates = new HashMap<>();
		dates.put("checkin", checkinDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		dates.put("checkout", checkoutDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		bookingData.put("bookingdates", dates);

		bookingData.put("additionalneeds", "Test needs for " + testId);

		return bookingData;
	}

	@AfterAll
	public static void globalTearDown() {
		System.out.println("🏁 API测试套件执行完成");
	}
}