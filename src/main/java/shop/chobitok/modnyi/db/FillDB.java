package shop.chobitok.modnyi.db;

import org.springframework.stereotype.Component;
import shop.chobitok.modnyi.entity.*;
import shop.chobitok.modnyi.repository.CompanyRepository;
import shop.chobitok.modnyi.service.OrderService;
import shop.chobitok.modnyi.service.ShoeService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class FillDB {

    private ShoeService shoeService;

    private CompanyRepository companyRepository;

    private OrderService orderService;

    public FillDB(ShoeService shoeService, CompanyRepository companyRepository, OrderService orderService) {
        this.shoeService = shoeService;
        this.companyRepository = companyRepository;
        this.orderService = orderService;
    }

   // @PostConstruct
    public void init() {
        Company company = new Company();
        company.setName("LadyShoes");
        companyRepository.save(company);

        String model = "191 лаковані";
        Shoe shoe ;
        if (shoeService.getAll(0, 10, model).size() <= 0 || shoeService.getAll(0, 10, model).get(0) == null) {
            shoe = new Shoe();
            shoe.setName("191 лаковані");
            shoe.setModel("191 лаковані");
            shoe.setCost(720.0);
            shoe.setPrice(1500.0);
            shoe.setDescription("Лаковане взуття");
            shoe.setPhotoPath("192l/1.jpg");
            shoe = shoeService.addShoe(shoe);
        }else{
            shoe = shoeService.getAll(0, 10, model).get(0);
        }
        Client client = new Client();
        client.setName("Наталія");
        client.setLastName("Чобіток");
        client.setMiddleName("Андріївна");
        client.setPhone("+380637638967");

        Ordered ordered = new Ordered();
        ordered.setClient(client);
        List<Shoe> shoes = new ArrayList<>();
        shoes.add(shoe);
        ordered.setPrePayment(100.0);
        ordered.setStatus(Status.CREATED);
        ordered.setTtn("20450207223718");
        ordered.setNotes("Якісь записи, будь які");
        addOrder(ordered);
    }

    private void addOrder(Ordered ordered) {
        if (true) {
            orderService.createOrder(ordered);
        }
    }


}
