package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserEfficiency;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserEfficiencyService {

    public UserEfficiency determineEfficiency(AppOrder appOrder, User user) {
        long diff = ChronoUnit.MINUTES.between(LocalDateTime.now(), appOrder.getDateAppOrderShouldBeProcessed());
        return null;
    }

}
