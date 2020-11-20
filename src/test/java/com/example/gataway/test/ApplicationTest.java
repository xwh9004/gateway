package com.example.gataway.test;

import com.example.gataway.test.service.TestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 13:12 on 2020/11/20
 * @version V0.1
 * @classNmae ApplicationTest
 */
@EnableAspectJAutoProxy(proxyTargetClass=true)
@SpringBootApplication(scanBasePackageClasses =ApplicationTest.class ,exclude = DataSourceAutoConfiguration.class )
public class ApplicationTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(ApplicationTest.class);
        TestService service = application.getBean(TestService.class);

        service.test2();

    }
}
