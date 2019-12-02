package ca.tbssct.ep;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class EpRequestApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpRequestApplication.class, args);
	}
	

}
