package team7.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class HrbankApplication {

    public static void main(String[] args) { SpringApplication.run(HrbankApplication.class, args);}

}
