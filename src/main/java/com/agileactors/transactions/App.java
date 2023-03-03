package com.agileactors.transactions;

import com.agileactors.transactions.configuration.InMemoryDatasourceConfiguration;
import com.agileactors.transactions.controller.TransactionController;
import com.agileactors.transactions.repository.TransactionRepository;
import com.agileactors.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@ComponentScan(
	basePackageClasses = {
		InMemoryDatasourceConfiguration.class,
		TransactionController.class,
		TransactionService.class,
		TransactionRepository.class
	})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
