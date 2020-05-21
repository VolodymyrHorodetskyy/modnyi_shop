package shop.chobitok.modnyi.mapper;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.request.CreateShoeRequest;

@Service
public class ShoeMapper {

    public Shoe convertFromCreateShoeRequest(CreateShoeRequest createShoeRequest) {
        Shoe shoe = null;
        if (createShoeRequest != null) {
            shoe = setToShoe(createShoeRequest, shoe);
        }
        return shoe;
    }

    public Shoe convertFromCreateShoeRequest(CreateShoeRequest createShoeRequest, Shoe shoe) {
        return setToShoe(createShoeRequest, shoe);
    }

    private Shoe setToShoe(CreateShoeRequest createShoeRequest, Shoe shoe) {
        shoe.setPrice(createShoeRequest.getPrice());
        shoe.setName(createShoeRequest.getName());
        shoe.setModel(createShoeRequest.getModel());
        shoe.setColor(createShoeRequest.getColor());
        shoe.setCost(createShoeRequest.getCost());
        shoe.setDescription(createShoeRequest.getDescription());
        return shoe;
    }

}
