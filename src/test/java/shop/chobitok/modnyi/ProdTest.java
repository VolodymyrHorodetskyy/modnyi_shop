package shop.chobitok.modnyi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.NotificationRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.service.CheckerService;
import shop.chobitok.modnyi.specification.CanceledOrderReasonSpecification;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.util.Arrays.asList;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("prod")
public class ProdTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CanceledOrderReasonRepository canceledOrderReasonRepository;
    @Autowired
    private NovaPostaRepository novaPostaRepository;

    @Test
    public void getSentAndDelivered() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setStatuses(asList(Status.ДОСТАВЛЕНО, Status.ВІДПРАВЛЕНО));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            out.println(ordered.getTtn() + " " + ordered.getStatus() + "\n" + ordered.getPostComment());
        }
    }

    @Test
    public void getReturnsDepartment() {
        CanceledOrderReasonSpecification cors = new CanceledOrderReasonSpecification();
        cors.setStatus(Status.ДОСТАВЛЕНО);
        List<CanceledOrderReason> canceledOrderReasons = canceledOrderReasonRepository.findAll(cors);
        for (CanceledOrderReason c : canceledOrderReasons) {
            out.println(novaPostaRepository.getTracking(4l, c.getReturnTtn()).getData().get(0).getRecipientAddress());
        }
    }

    @Autowired
    private ShoeRepository shoeRepository;

    @Test
    public void renameShoe() {
        Shoe shoe = shoeRepository.findById(17913l).orElse(null);
        shoe.setModel("174");
        shoeRepository.save(shoe);
    }

    @Test
    public void setUnAvailableForOrders() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(LocalDateTime.now().minusMonths(2));
        orderedSpecification.setTo(LocalDateTime.now().minusMonths(1));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        for (Ordered ordered : orderedList) {
            ordered.setAvailable(false);
        }
        orderRepository.saveAll(orderedList);
    }

    @Autowired
    private CheckerService checkerService;

    @Test
    public void checkPayedKeepingOrders() {
        checkerService.checkPayedKeepingOrders();
    }

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void removeNotifications() {
        List<Notification> notifications = notificationRepository.findByCreatedDateIsGreaterThan(LocalDateTime.now().minusDays(1));
        notificationRepository.deleteAll(notifications);
    }

    @Test
    public void payedKeepingCheck() {
        checkerService.checkPayedKeepingOrders();
    }

    @Test
    public void showAllDm() {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        orderedSpecification.setFrom(LocalDateTime.now().minusDays(20));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        orderedList = orderedList.stream().filter(ordered -> (ordered.getPostComment().contains("дм") || ordered.getPostComment().contains("Дм")
                || ordered.getPostComment().contains("ДМ")) && ordered.getPostComment().contains("40")).collect(Collectors.toList());
        for (Ordered ordered : orderedList) {
            out.println(ordered.getTtn() + " " + ordered.getPostComment() + " " + ordered.getStatus() + "\n");
        }
    }
}
