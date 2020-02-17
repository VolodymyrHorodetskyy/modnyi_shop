package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.repository.ShoeRepository;

import java.util.List;

@Service
public class ShoeService {

    private ShoeRepository shoeRepository;

    public ShoeService(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;
    }

    public List<Shoe> getAll() {
        return shoeRepository.findAll();
    }

}
