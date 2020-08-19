package shop.chobitok.modnyi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.ShoePrice;
import shop.chobitok.modnyi.entity.request.AddOrRemovePatternRequest;
import shop.chobitok.modnyi.entity.request.CreateShoeRequest;
import shop.chobitok.modnyi.entity.request.UpdateShoeRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.mapper.ShoeMapper;
import shop.chobitok.modnyi.repository.CompanyRepository;
import shop.chobitok.modnyi.repository.ShoePriceRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.ShoeSpecification;
import shop.chobitok.modnyi.util.DateHelper;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formLocalDateTimeStartOfTheDay;

@Service
public class ShoeService {

    private ShoeRepository shoeRepository;
    private ShoePriceRepository shoePriceRepository;
    private ShoeMapper shoeMapper;


    public ShoeService(ShoeRepository shoeRepository, ShoePriceRepository shoePriceRepository, ShoeMapper shoeMapper) {
        this.shoeRepository = shoeRepository;
        this.shoePriceRepository = shoePriceRepository;
        this.shoeMapper = shoeMapper;
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

    public boolean removeShoe(Long id) {
        shoeRepository.deleteById(id);
        return true;
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

    private List<String> checkListOnEmptyStrings(List<String> strings) {
        for (int i = 0; i < strings.size(); ++i) {
            if (StringUtils.isEmpty(strings.get(i))) {
                strings.remove(i);
            }
        }
        return strings;
    }

    public ShoePrice setNewPrice(Shoe shoe, LocalDateTime from, Double price, Double cost) {
        ShoePrice shoePrice = shoePriceRepository.findOneByToIsNullAndShoeId(shoe.getId());
        from = formLocalDateTimeStartOfTheDay(from);
        if (shoePrice != null) {
            shoePrice.setTo(from);
            shoePriceRepository.save(shoePrice);
        }
        ShoePrice newShoePrice = new ShoePrice(shoe, from, cost, price);
        return shoePriceRepository.save(newShoePrice);
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
