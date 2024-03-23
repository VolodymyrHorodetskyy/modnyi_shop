package shop.chobitok.modnyi.service.horoshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HoroshopConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    @Bean(name = "vilna")
    public HoroshopService vilnaHoroshopService() {
        return new HoroshopService(restTemplate, new HoroshopConfig("https://vilna.top/api/auth",
                "https://vilna.top/api/orders/get", "volodymyr", "Vova2024", "vilna"));
    }

    @Bean(name = "mchobitok")
    public HoroshopService mchobitokHoroshopService() {
        return new HoroshopService(restTemplate, new HoroshopConfig("https://mchobitok.com/api/auth",
                "https://mchobitok.com/api/orders/get", "volodymyr", "Vova2023", "mchobitok"));
    }
}
