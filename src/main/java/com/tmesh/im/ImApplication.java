package com.tmesh.im;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@MapperScan("com.tmesh.im.**.dao")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@SpringBootApplication()
public class ImApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
