package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.User;
import shop.chobitok.modnyi.entity.UserEfficiency;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.repository.UserEfficiencyRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formDateFromOrGetDefault;

@Service
public class UserEfficiencyService {

    private UserEfficiencyRepository userEfficiencyRepository;
    private ParamsService paramsService;

    public UserEfficiencyService(UserEfficiencyRepository userEfficiencyRepository, ParamsService paramsService) {
        this.userEfficiencyRepository = userEfficiencyRepository;
        this.paramsService = paramsService;
    }

    public UserEfficiency determineEfficiency(AppOrder appOrder, User user) {
        int diff = (int) ChronoUnit.MINUTES.between(appOrder.getDateAppOrderShouldBeProcessed(), LocalDateTime.now());
        UserEfficiency userEfficiency = new UserEfficiency(user, diff, getEfficiencyPoints(diff), appOrder);
        return userEfficiencyRepository.save(userEfficiency);
    }

    public double getEfficiencyPoints(int diff) {
        double efficiencyPoints = 0;
        int minutesAppOrderShouldBeProcessed =
                Integer.parseInt(paramsService.getParam("minutesAppOrderShouldBeProcessed").getGetting());
        if (diff > minutesAppOrderShouldBeProcessed) {
            efficiencyPoints = -100;
        } else {
            if (diff < (int) (minutesAppOrderShouldBeProcessed * (40 / 100.0f))) {
                efficiencyPoints = 100;
            } else {
                efficiencyPoints = calculatePercentage(minutesAppOrderShouldBeProcessed - diff, minutesAppOrderShouldBeProcessed);
            }
        }
        return efficiencyPoints;
    }

    public double calculatePercentage(int obtained, int total) {
        return obtained * 100 / total;
    }

    public StringResponse showUserEfficiency(String from, Long userId) {
        LocalDateTime fromLocalDateTime = formDateFromOrGetDefault(from);
        List<UserEfficiency> userEfficiencyList = userEfficiencyRepository.findByCreatedDateGreaterThanEqualAndUserId(
                fromLocalDateTime, userId);
        StringBuilder result = new StringBuilder();
        double allEfficiency = 0;
        int allMinutes = 0;
        for (UserEfficiency userEfficiency : userEfficiencyList) {
            allEfficiency += userEfficiency.getProcessingEfficiency();
            allMinutes += userEfficiency.getMinutes();
        }
        result.append("Ефективність = ").append(allEfficiency / userEfficiencyList.size()).append("\n");
        result.append("Середній час на обробку = ").append(allMinutes / userEfficiencyList.size()).append("\n");
        return new StringResponse(result.toString());
    }

}
