package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Company;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.repository.CompanyRepository;
import shop.chobitok.modnyi.repository.ShoeRepository;
import shop.chobitok.modnyi.specification.ShoeSpecification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoeService {

    private ShoeRepository shoeRepository;

    private CompanyRepository companyRepository;


    public ShoeService(ShoeRepository shoeRepository, CompanyRepository companyRepository) {
        this.shoeRepository = shoeRepository;
        this.companyRepository = companyRepository;
    }

    public List<Shoe> getAll(int page, int size, String model) {
        return shoeRepository.findAll(new ShoeSpecification(model), PageRequest.of(page, size)).getContent();
    }

    public Shoe addShoe(Shoe shoe) {
        Company company = companyRepository.findAll().get(0);
        shoe.setCompany(company);
        return shoeRepository.save(shoe);
    }

    public List<Shoe> fromTildaCSV(String path) {
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
                    shoe.setPhotoPath( str[str.length - 7].replaceAll("^\"|\"$", ""));
                    shoe.setImported(true);
                    shoe.setPrice(Double.parseDouble(str[str.length - 6]));
                    System.out.println(str[2]);
                    shoes.add(shoe);

                    System.out.println(str[str.length - 6]);
                    System.out.println(str[str.length - 7]);
                    //   System.out.println(str[9]);
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
    }

}
