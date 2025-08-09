package Booker;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCeshirenSearchAPI {

	// =============== 测试环境配置 ===============
	private static String baseUrl;
	private static String searchUrl;

	/**
	 * 测试类初始化方法 - 对应Python的setup_class
	 * 配置测试环境的基础信息和API端点
	 */
	@BeforeAll
	public static void setupClass() {
		// =============== 配置基础测试环境 ===============
		baseUrl = "https://ceshiren.com";              // 论坛主域名
		searchUrl = baseUrl + "/search";               // 搜索接口的完整URL

		// =============== 配置RestAssured全局设置 ===============
		RestAssured.baseURI = baseUrl;                 // 设置基础URI

		System.out.println("初始化测试环境，搜索API地址：" + searchUrl);
	}

	// =============== 正向测试用例 - 参数化测试 ===============
	/**
	 * 正向测试用例：对应Python的test_search方法
	 * 验证搜索API在正常搜索关键词下的功能
	 *
	 * @param searchKey 搜索关键词，通过参数化注入不同的测试数据
	 */
	@ParameterizedTest
	@ValueSource(strings = {
			"pytest",
			"面试题",
			"a",
			"appium desktop连接真机，start session，出现报错，手机上appium setting打开闪退，但是进程显示是进行中。报错内容：An unknown server-side error occurred while processing the command. Original error: Could not find a connected Android device in 20364ms."
	})
	public void testSearch(String searchKey) {
		// =============== 步骤1：构建请求参数 ===============
		// =============== 步骤2：设置请求头 ===============
		// =============== 步骤3：发送HTTP GET请求到搜索API ===============
		Response response = given()
				.header("Accept", "application/json")  // 对应Python的headers
				.queryParam("q", searchKey)            // 对应Python的params["q"]
				.queryParam("page", 1)                 // 对应Python的params["page"]
				.when()
				.get("/search")
				.then()
				.extract().response();

		// =============== 步骤4：输出原始响应用于调试 ===============
		System.out.println(response.asString());      // 对应Python的print(r.text)

		// =============== 步骤5：解析响应并验证搜索结果 ===============
		List<Map<String, Object>> posts = response.jsonPath().getList("posts");
		int results = posts != null ? posts.size() : 0;

		System.out.println("响应结果中 posts 结果数量为 " + results);

		// =============== 步骤6：断言验证 ===============
		assertNotEquals(0, results);
	}

	// =============== 边界测试用例 ===============
	/**
	 * 边界测试：对应Python的test_search_none方法
	 * 验证搜索关键词为空字符串时的API行为
	 */
	@Test
	public void testSearchNone() {
		// =============== 步骤1：构建空搜索关键词的请求 ===============
		Response response = given()
				.header("Accept", "application/json")
				.queryParam("q", "")                   // 对应Python的params["q"] = ""
				.queryParam("page", 1)
				.when()
				.get("/search")
				.then()
				.extract().response();

		// =============== 步骤2：输出响应用于分析 ===============
		System.out.println(response.asString());      // 对应Python的print(r.text)

		// =============== 步骤3：验证空搜索的处理结果 ===============
		Object groupedSearchResult = response.jsonPath().get("grouped_search_result");

		// 对应Python的assert r.json().get("grouped_search_result") == None
		assertNull(groupedSearchResult);
	}

	// =============== 异常测试用例 ===============
	/**
	 * 异常测试：对应Python的test_search_no_result方法
	 * 验证搜索不到任何结果时的API行为
	 */
	@Test
	public void testSearchNoResult() {
		// =============== 步骤1：构建无结果搜索请求 ===============
		Response response = given()
				.header("Accept", "application/json")
				.queryParam("q", "ooooooooooo")        // 对应Python的params["q"] = "ooooooooooo"
				.queryParam("page", 1)
				.when()
				.get("/search")
				.then()
				.extract().response();

		// =============== 步骤2：输出响应用于分析 ===============
		System.out.println(response.asString());      // 对应Python的print(r.text)

		// =============== 步骤3：验证无搜索结果的处理 ===============
		List<Map<String, Object>> posts = response.jsonPath().getList("posts");

		// 对应Python的assert r.json().get("posts") == []
		assertNotNull(posts);
		assertTrue(posts.isEmpty());
	}

	// =============== 参数验证测试用例1 ===============
	/**
	 * 参数验证测试：对应Python的test_search_noq方法
	 * 验证缺少必需参数'q'时的API行为
	 */
	@Test
	public void testSearchNoq() {
		// =============== 步骤1：构建缺少q参数的请求 ===============
		Response response = given()
				.header("Accept", "application/json")
				.queryParam("page", 1)                 // 只有page参数，没有q参数
				.when()
				.get("/search")
				.then()
				.extract().response();

		// =============== 步骤2：输出响应用于分析 ===============
		System.out.println(response.asString());      // 对应Python的print(r.text)

		// =============== 步骤3：验证缺少关键参数的处理 ===============
		Object groupedSearchResult = response.jsonPath().get("grouped_search_result");

		// 对应Python的assert r.json().get("grouped_search_result") == None
		assertNull(groupedSearchResult);
	}

	// =============== 参数验证测试用例2 ===============
	/**
	 * 参数验证测试：对应Python的test_search_nopage方法
	 * 验证缺少'page'参数时的API行为
	 */
	@Test
	public void testSearchNopage() {
		// =============== 步骤1：构建缺少page参数的请求 ===============
		Response response = given()
				.header("Accept", "application/json")
				.queryParam("q", "测试用例")            // 只有q参数，没有page参数
				.when()
				.get("/search")
				.then()
				.extract().response();

		// =============== 步骤2：输出响应用于分析 ===============
		System.out.println(response.asString());      // 对应Python的print(r.text)

		// =============== 步骤3：验证缺少page参数的处理 ===============
		List<Map<String, Object>> posts = response.jsonPath().getList("posts");
		int results = posts != null ? posts.size() : 0;

		System.out.println("响应结果中 posts 结果数量为 " + results);

		// 对应Python的assert results != 0
		assertNotEquals(0, results);
	}

	// =============== 参数验证测试用例3 ===============
	/**
	 * 参数验证测试：对应Python的test_search_noparams方法
	 * 验证完全不传任何参数时的API行为
	 */
	@Test
	public void testSearchNoparams() {
		// =============== 步骤1：构建无参数请求 ===============
		Response response = given()
				.header("Accept", "application/json") // 不传递任何查询参数
				.when()
				.get("/search")
				.then()
				.extract().response();

		// =============== 步骤2：输出响应用于分析 ===============
		System.out.println(response.asString());      // 对应Python的print(r.text)

		// =============== 步骤3：验证无参数请求的处理 ===============
		Object groupedSearchResult = response.jsonPath().get("grouped_search_result");

		// 对应Python的assert r.json().get("grouped_search_result") == None
		assertNull(groupedSearchResult);
	}
}