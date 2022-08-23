package shop.chobitok.modnyi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Discount;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.request.*;
import shop.chobitok.modnyi.entity.response.ShoeWithPrice;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.mapper.ShoeMapper;
import shop.chobitok.modnyi.novaposta.mapper.NPOrderMapper;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.ShoeSpecification;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoeService {

    private final ShoeRepository shoeRepository;
    private final ShoeMapper shoeMapper;
    private final NPOrderMapper npOrderMapper;
    private final DiscountService discountService;
    private final OrderRepository orderRepository;


    public ShoeService(ShoeRepository shoeRepository, ShoeMapper shoeMapper, NPOrderMapper npOrderMapper, DiscountService discountService, OrderRepository orderRepository) {
        this.shoeRepository = shoeRepository;
        this.shoeMapper = shoeMapper;
        this.npOrderMapper = npOrderMapper;
        this.discountService = discountService;
        this.orderRepository = orderRepository;
    }

    public List<ShoeWithPrice> getAllShoeWithPrice(int page, int size, String modelAndColor) {
        return shoeMapper.convertToShoePrice(shoeRepository.findAll(new ShoeSpecification(modelAndColor), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))).getContent());
    }

    public List<Shoe> getAll(int page, int size, String model) {
        return shoeRepository.findAll(new ShoeSpecification(model), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))).getContent();
    }

    public Shoe createShoe(CreateShoeRequest createShoeRequest) {
        return shoeRepository.save(shoeMapper.convertFromCreateShoeRequest(createShoeRequest));
    }

    public Shoe updateShoe(UpdateShoeRequest updateShoeRequest) {
        Shoe shoe = shoeRepository.findById(updateShoeRequest.getShoeId()).orElse(null);
        if (shoe == null) {
            throw new ConflictException("Shoe not found");
        }
        return shoeRepository.save(shoeMapper.convertFromCreateShoeRequest(updateShoeRequest, shoe));
    }

    public Shoe addPattern(AddOrRemovePatternRequest request) {
        Shoe shoe = shoeRepository.findById(request.getShoeId()).orElse(null);
        if (shoe == null) {
            throw new ConflictException("Shoe not found");
        }
        shoe.getPatterns().add(request.getPattern());
        return shoeRepository.save(shoe);
    }

    public boolean removePattern(AddOrRemovePatternRequest request) {
        Shoe shoe = shoeRepository.findById(request.getShoeId()).orElse(null);
        if (shoe == null) {
            throw new ConflictException("Shoe not found");
        }
        shoe.getPatterns().remove(request.getPattern());
        shoeRepository.save(shoe);
        return true;
    }

    public Double getShoePrice(Long[] shoeIds, Long discountId) {
        if (shoeIds != null && shoeIds.length > 0) {
            Discount discount = null;
            if (discountId != 0) {
                discount = discountService.getById(discountId);
            }
            List<OrderedShoe> orderedShoeList = new ArrayList<>();
            for (Long shoeId : shoeIds) {
                orderedShoeList.add(new OrderedShoe(36, shoeRepository.getOne(shoeId), null));
            }
            return npOrderMapper.countDiscount(orderedShoeList, discount);
        }
        return null;
    }

    @Transactional
    public Ordered addShoeToOrder(AddShoeToOrderRequest request) {
        Ordered ordered = orderRepository.findById(request.getOrderId()).orElse(null);
        Shoe shoe = shoeRepository.findById(request.getShoeId()).orElse(null);
        if (ordered == null) {
            throw new ConflictException("Замовлення не знайдено");
        }
        if (shoe == null) {
            throw new ConflictException("Взуття не знайдено");
        }
        OrderedShoe orderedShoe = new OrderedShoe(request.getSize(), shoe, request.getComment(), ordered);
        if (ordered.getOrderedShoeList() == null) {
            ordered.setOrderedShoeList(new ArrayList<>());
        }
        ordered.getOrderedShoeList().add(orderedShoe);
        return orderRepository.save(ordered);
    }

    @Transactional
    public Ordered removeShoeFromOrder(RemoveShoeFromOrderRequest request) {
        Ordered ordered = orderRepository.findById(request.getOrderId()).orElse(null);
        if (ordered == null) {
            throw new ConflictException("Замовлення не знайдено");
        }
        OrderedShoe orderedShoe = ordered.getOrderedShoeList().stream().filter(o -> o.getId().equals(request.getShoeId())).findFirst().orElse(null);
        if (orderedShoe == null) {
            throw new ConflictException("Взуття не знайдено");
        } else {
            ordered.getOrderedShoeList().remove(orderedShoe);
            ordered = orderRepository.save(ordered);
        }
        return ordered;
    }


 /*   public List<Shoe> fromTildaCSV(String path) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {
            int i = 1;
            List<Shoe> shoes = new ArrayList<>();
            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                ++i;
                if (i != 8) {
                    Shoe shoe = new Shoe();
                    String[] str = line.split(cvsSplitBy);
                    String modelAndColor = str[2].replaceAll("^\"|\"$", "");
                    String model = modelAndColor.substring(0, modelAndColor.indexOf(' '));
                    String color = modelAndColor.substring(modelAndColor.indexOf(' ') + 1);
                    shoe.setModel(model);
                    shoe.setColor(color);
                    shoe.setPhotoPath(str[str.length - 7].replaceAll("^\"|\"$", ""));
                    shoe.setImported(true);
                    shoe.setPrice(Double.parseDouble(str[str.length - 6]));
                    shoes.add(shoe);
                }
            }
            return shoeRepository.saveAll(shoes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }*/

}
