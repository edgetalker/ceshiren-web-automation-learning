# 🚀 测试人网站自动化测试学习项目

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-4.15+-green.svg)](https://selenium.dev/)
[![JUnit](https://img.shields.io/badge/JUnit-5.9+-orange.svg)](https://junit.org/junit5/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> 📚 基于测试人社区网站(ceshiren.com)的Web自动化测试学习实践项目

## 📋 项目简介

这是一个完整的自动化测试学习项目，包含对测试人社区网站和OrangeHRM进行Web自动化测试以及Restful-Booker的接口测试，系统性地学习Selenium + JUnit 5 + RestAssured的使用。

**⚠️ 声明**: 本项目仅用于学习目的，所有测试均遵循网站的使用条款，不会对目标网站造成负担。

## 🎯 学习目标

- 掌握Web自动化测试的核心技能
- 学习Selenium WebDriver的各种API使用
- 掌握接口自动化测试的核心技能
- 学习RestAssured的使用
- 实践测试框架JUnit 5的高级特性
- 了解自动化测试工程化最佳实践
- 建立完整的测试项目架构思维

## 🛠️ 技术栈

| 技术 | 版本    | 用途         |
|------|-------|------------|
| Java | 11+   | 编程语言       |
| Maven | 3.6+  | 项目管理和构建    |
| Selenium WebDriver | 4.15+ | Web自动化测试框架 |
| JUnit 5 | 5.9+  | 单元测试框架     |
| RestAssured | 5.3+  | 接口自动化测试框架  |
| WebDriverManager | 5.6+  | 浏览器驱动管理    |
| Allure | 2.24+ | 测试报告生成     |

## 🚀 快速开始

### 1. 环境准备
```bash
# 检查Java版本 (需要11+)
java -version

# 检查Maven版本 (需要3.6+)
mvn -version

# 安装Chrome浏览器 (自动化测试必需)
```

### 2. 克隆项目
```bash
git clone https://github.com/你的用户名/ceshiren-web-automation-learning.git
cd ceshiren-web-automation-learning
```

### 3. 运行测试
```bash
# 运行所有基础测试
mvn test -Dtest=com.ceshiren.basic.*

# 运行指定测试类
mvn test -Dtest=Task01_BasicPageValidation

# 运行指定测试方法
mvn test -Dtest=Task01_BasicPageValidation#testPageTitle
```

### 4. 查看报告
```bash
# 生成Allure报告 (后期添加)
mvn allure:serve
```

## 📊 学习进度

- [x] 环境搭建和项目初始化
- [x] 基础页面验证测试
- [x] 元素定位策略学习
- [x] 页面信息提取技巧
- [x] 搜索功能完整测试流程
- [x] 登录功能自动化
- [x] 带有前置登录认证的接口自动化


## 📝 学习笔记

### 重要知识点记录

#### 元素定位最佳实践
```java
// 优先级: ID > CSS选择器 > XPath > ClassName > 其他
// 1. ID定位 (最优先，性能最好)
driver.findElement(By.id("search-button"))

// 2. CSS选择器 (功能强大，性能好)
driver.findElement(By.cssSelector("#search-button"))
driver.findElement(By.cssSelector(".login-btn"))
driver.findElement(By.cssSelector("input[name='username']"))

// 3. XPath (最灵活，但性能较差)
driver.findElement(By.xpath("//button[@id='search-button']"))
driver.findElement(By.xpath("//button[contains(text(), '搜索')]"))
```

#### 等待机制最佳实践
```java
// 显式等待 (推荐)
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("button")));

// 隐式等待 (全局设置)
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

### 遇到的问题和解决方案

#### 1. 元素点击被拦截
**问题**: `ElementClickInterceptedException`
**解决方案**:
```java
// 方法1: JavaScript点击
((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

// 方法2: 滚动到元素再点击
((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
Thread.sleep(500);
element.click();
```

#### 2. 元素定位失败
**问题**: `NoSuchElementException`
**解决方案**:
```java
// 增加等待时间或使用更稳定的定位策略
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
```

## 🤝 贡献指南

欢迎提出改进建议！如果你发现了更好的测试方法或遇到了有趣的问题，请：

1. Fork 这个项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📞 联系方式
- 邮箱: pankw0401@163.com
- 博客: https://blog.csdn.net/edgerunne

## 📄 许可证

这个项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- 感谢 [测试人社区](https://ceshiren.com/) 提供优秀的学习平台
- 感谢 [霍格沃兹测试开发学社](https://ceshiren.com/) 的技术指导
- 感谢所有为开源测试工具贡献的开发者们

---

⭐ 如果这个项目对你有帮助，请给个Star支持一下！

📚 持续学习，持续进步！