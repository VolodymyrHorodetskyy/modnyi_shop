package shop.chobitok.modnyi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.telegram.ChobitokBot;
import shop.chobitok.modnyi.telegram.ChobitokLeadsBot;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@Configuration
@EnableJpaAuditing
@EnableScheduling
public class ModnyiApplication {

    @Autowired
    private OrderService orderService;


    public static void main(String[] args) throws TelegramApiException {
        ConfigurableApplicationContext context = SpringApplication.run(ModnyiApplication.class, args);
        ChobitokBot chobitokBot = context.getBean(ChobitokBot.class);
        ChobitokLeadsBot chobitokLeadsBot = context.getBean(ChobitokLeadsBot.class);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(chobitokBot);
        telegramBotsApi.registerBot(chobitokLeadsBot);
        System.out.println("Bot started successfully!");
    }

}
