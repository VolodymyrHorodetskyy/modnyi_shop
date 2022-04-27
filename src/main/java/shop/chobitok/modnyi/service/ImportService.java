package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.UserRepository;

@Service
public class ImportService {

    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private NovaPostaService novaPostaService;
    private NotificationService notificationService;

    public ImportService(UserRepository userRepository, OrderRepository orderRepository, NovaPostaService novaPostaService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.novaPostaService = novaPostaService;
        this.notificationService = notificationService;
    }

    public String importOrderFromTTNString(String ttn, Long userId, Discount discount,
                                           Variants sourceOfOrder) {
        if (userId == null) {
            throw new ConflictException("UserId must not be null");
        } else {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new ConflictException("User not found");
            }
            StringBuilder result = new StringBuilder();
            if (orderRepository.findOneByAvailableTrueAndTtn(ttn) == null) {
                try {
                    Ordered ordered = novaPostaService.createOrUpdateOrderFromNP(ttn, null, discount, sourceOfOrder);
                    ordered.setUser(user);
                    if (ordered.getStatus() != Status.НЕ_ЗНАЙДЕНО) {
                        orderRepository.save(ordered);
                        if (ordered.getOrderedShoeList().size() < 1) {
                            result.append(ttn + "  ... взуття не визначено \n");
                        } else {
                            result.append(ttn + "  ... імпортовано \n");
                        }
                    } else {
                        result.append("  ...  НЕ ІМПОРТОВАНО ... Статус Не знайдено");
                        notificationService.createNotification("Накладну не імпортовано, " + user.getName(), ttn, null);
                    }
                } catch (ConflictException e) {
                    result.append(ttn + "  ... неможливо знайти ттн \n");
                }
            } else {
                result.append(ttn + "  ... вже існує в базі \n");
            }
            return result.toString();
        }
    }

}
