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
import shop.chobitok.modnyi.entity.response.ShoeWithPrice;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.mapper.ShoeMapper;
import shop.chobitok.modnyi.repository.ShoePriceRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.ShoeSpecification;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chobitok.modnyi.util.DateHelper.formLocalDateTimeStartOfTheDay;

@Service
public class ShoeService {

    private ShoeRepository shoeRepository;
    private ShoeMapper shoeMapper;
    private ShoePriceService shoePriceService;

    public ShoeService(ShoeRepository shoeRepository, ShoeMapper shoeMapper, ShoePriceService shoePriceService) {
        this.shoeRepository = shoeRepository;
        this.shoeMapper = shoeMapper;
        this.shoePriceService = shoePriceService;
    }

    public List<ShoeWithPrice> getAllShoeWithPrice(int page, int size, String model) {
        return shoeMapper.convertToShoePrice(shoeRepository.findAll(new ShoeSpecification(model), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))).getContent());
    }

    public List<Shoe> getAll(int page, int size, String model) {
        return shoeRepository.findAll(new ShoeSpecification(model), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"))).getContent();
    }

    public Shoe createShoe(CreateShoeRequest createShoeRequest) {
        Shoe shoe = shoeRepository.save(shoeMapper.convertFromCreateShoeRequest(createShoeRequest));
        return shoe;
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
