package shop.chobitok.modnyi.novaposta.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.entity.MarkingResponse;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MarkingService {

    private OrderRepository orderRepository;
    private NovaPostaRepository postaRepository;

    public MarkingService(OrderRepository orderRepository, NovaPostaRepository postaRepository) {
        this.orderRepository = orderRepository;
        this.postaRepository = postaRepository;
    }

    public List<MarkingResponse> getMarking(String ttn, String modelAndColor, Integer size) {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        String model;
        String color = null;
        if (modelAndColor.indexOf(' ') != -1) {
            model = modelAndColor.substring(0, modelAndColor.indexOf(' '));
            color = modelAndColor.substring(modelAndColor.indexOf(' ') + 1);
        } else {
            model = modelAndColor;
        }
        orderedSpecification.setModel(model);
        orderedSpecification.setColor(color);
        orderedSpecification.setTtn(ttn);
        orderedSpecification.setSize(size);
        orderedSpecification.setStatuses(Arrays.asList(Status.СТВОРЕНО));
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification);
        List<MarkingResponse> markings = new ArrayList<>();
        for (Ordered ordered : orderedList) {
            markings.add(new MarkingResponse(ordered, postaRepository.getMarking(ordered)));
        }
        return markings;
    }

}
