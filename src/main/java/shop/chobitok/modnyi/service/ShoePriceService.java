package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.ShoePrice;
import shop.chobitok.modnyi.entity.request.CreateShoePriceRequest;
import shop.chobitok.modnyi.repository.ShoePriceRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formLocalDateTimeStartOfTheDay;

@Service
public class ShoePriceService {

    private ShoePriceRepository shoePriceRepository;
    private ShoeRepository shoeRepository;

    public ShoePriceService(ShoePriceRepository shoePriceRepository, ShoeRepository shoeRepository) {
        this.shoePriceRepository = shoePriceRepository;
        this.shoeRepository = shoeRepository;
    }

    public ShoePrice setNewPrice(Shoe shoe, LocalDateTime from, Double price, Double cost) {
        ShoePrice shoePrice = shoePriceRepository.findOneByToDateIsNullAndShoeId(shoe.getId());
        if (from == null) {
            from = formLocalDateTimeStartOfTheDay(LocalDateTime.now());
        } else {
            from = formLocalDateTimeStartOfTheDay(from);
        }
        if (shoePrice == null) {
            ShoePrice newShoePrice = new ShoePrice(shoe, from, cost, price);
            return shoePriceRepository.save(newShoePrice);
        } else if (shoePrice != null && (!shoePrice.getCost().equals(cost) || !shoePrice.getPrice().equals(price))) {
            shoePrice.setToDate(from);
            shoePriceRepository.save(shoePrice);

            ShoePrice newShoePrice = new ShoePrice(shoe, from, cost, price);
            return shoePriceRepository.save(newShoePrice);
        }
        return null;
    }

    public ShoePrice setNewPrice(Shoe shoe, Double price, Double cost) {
        return setNewPrice(shoe, null, price, cost);
    }

    public ShoePrice setNewPrice(CreateShoePriceRequest createShoePriceRequest) {
        return setNewPrice(shoeRepository.getOne(createShoePriceRequest.getShoeId()), createShoePriceRequest.getPrice(), createShoePriceRequest.getCost());
    }

    public ShoePrice getShoePrice(Shoe shoe, Ordered ordered) {
        List<ShoePrice> shoePrices = shoePriceRepository.findByShoeId(shoe.getId(), Sort.by(Sort.Direction.DESC, "fromDate").and(Sort.by(Sort.Direction.DESC, "createdDate")));
        for (ShoePrice shoePrice : shoePrices) {
            if (ordered.getCreatedDate() == null) {
                return getActualShoePrice(shoe);
            }
            if (shoePrice.getFromDate().isBefore(ordered.getCreatedDate()) || shoePrice.getFromDate().isEqual(ordered.getCreatedDate())) {
                return shoePrice;
            }
        }
        return getActualShoePrice(shoe);
    }

    public ShoePrice getActualShoePrice(Shoe shoe) {
        ShoePrice shoePrice = shoePriceRepository.findTopByShoeId(shoe.getId(), Sort.by(Sort.Direction.DESC, "fromDate").and(Sort.by(Sort.Direction.DESC, "createdDate")));
        if (shoePrice == null) {
            shoePrice = new ShoePrice(shoe, null, 0d, 0d);
        }
        return shoePrice;
    }

}
