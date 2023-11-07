package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.novaposta.service.NovaPostaService;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.UserRepository;
import shop.chobitok.modnyi.service.entity.ImportResp;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class ImportService {

    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private NovaPostaService novaPostaService;
    private NotificationService notificationService;
    private StorageCoincidenceService storageCoincidenceService;

    public ImportService(UserRepository userRepository, OrderRepository orderRepository, NovaPostaService novaPostaService, NotificationService notificationService, StorageCoincidenceService storageCoincidenceService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.novaPostaService = novaPostaService;
        this.notificationService = notificationService;
        this.storageCoincidenceService = storageCoincidenceService;
    }

    public ImportResp importOrderFromTTNString(String ttn, Long userId, Discount discount,
                                               Variants sourceOfOrder) {
        int notResolvedStorageCoincidencesSize = storageCoincidenceService.findNotResolvedStorageCoincidences().size();
        if (userId == null) {
            throw new ConflictException("UserId must not be null");
        } else {
            User user = userRepository.findById(userId).orElse(null);
            String errors = null;
            if (user == null) {
                throw new ConflictException("User not found");
            }
            StringBuilder result = new StringBuilder();
            if (orderRepository.findOneByAvailableTrueAndTtn(ttn) == null) {
                try {
                    Ordered ordered = novaPostaService.createOrUpdateOrderFromNP(ttn, null, discount, sourceOfOrder);
                    ordered.setUser(user);
                    errors = checkOrder(ordered);
                    if (isEmpty(errors)) {
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
                    }
                } catch (ConflictException e) {
                    result.append(ttn + "  ... неможливо знайти ттн \n");
                }
            } else {
                result.append(ttn + "  ... вже існує в базі \n");
            }
            boolean coincidenceFound = false;
            if (notResolvedStorageCoincidencesSize != storageCoincidenceService.findNotResolvedStorageCoincidences().size()) {
                coincidenceFound = true;
            }
            return new ImportResp(result.toString(), coincidenceFound, errors);
        }
    }

    private String checkOrder(Ordered ordered) {
        String result = null;
        if (ordered.getClient() == null) {
            result = "Не вдалось витягнути дані клієнта з НП апі";
        } else if (isEmpty(ordered.getPostComment())) {
            result = "Не вдалось витягнути комент з НП апі";
        }
        if (!isEmpty(result)) {
            result += ", статус накландої = " + ordered.getStatus();
        }
        return result;
    }
}
