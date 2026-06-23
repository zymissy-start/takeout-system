package com.example.takeoutsystem;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.takeoutsystem.mapper")
@SpringBootApplication
public class TakeoutSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeoutSystemApplication.class, args);
	}

}