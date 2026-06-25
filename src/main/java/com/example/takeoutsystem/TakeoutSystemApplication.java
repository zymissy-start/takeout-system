package com.example.takeoutsystem;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 外卖点餐系统启动类。
 *
 * @SpringBootApplication：声明当前类为 Spring Boot 项目的入口类，
 * 会自动完成组件扫描、自动配置和 Bean 注册。
 *
 * @MapperScan：指定 MyBatis Mapper 接口所在包，
 * 使 SysUserMapper、MerchantOrderMapper、MerchantProductMapper 等接口
 * 能够被 Spring 容器扫描并注入到 Service 层。
 */

@MapperScan("com.example.takeoutsystem.mapper")
@SpringBootApplication
public class TakeoutSystemApplication {
	/**
	 * 程序主入口。
	 * 启动内嵌 Tomcat，并加载 Controller、Service、Mapper 等组件。
	 */

	public static void main(String[] args) {
		SpringApplication.run(TakeoutSystemApplication.class, args);
	}

}