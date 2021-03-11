package shop.chobitok.modnyi.novaposta.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Marking;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.repository.MarkingRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkingService {

    private OrderRepository orderRepository;
    private NovaPostaRepository postaRepository;
    private MarkingRepository markingRepository;

    public MarkingService(OrderRepository orderRepository, NovaPostaRepository postaRepository, MarkingRepository markingRepository) {
        this.orderRepository = orderRepository;
        this.postaRepository = postaRepository;
        this.markingRepository = markingRepository;
    }

    public List<Marking> getMarking(String ttn, String modelAndColor, Integer size, Boolean showPrinted) {
        OrderedSpecification orderedSpecification = new OrderedSpecification();
        String model = null;
        String color = null;
        if (!StringUtils.isEmpty(modelAndColor)) {
            if (modelAndColor.indexOf(' ') != -1) {
                model = StringUtils.trimAllWhitespace(modelAndColor.substring(0, modelAndColor.indexOf(' ')));
                color = StringUtils.trimLeadingWhitespace(modelAndColor.substring(modelAndColor.indexOf(' ') + 1));
            } else {
                model = modelAndColor;
            }
        }
        orderedSpecification.setModel(model);
        orderedSpecification.setColor(color);
        orderedSpecification.setTtn(ttn);
        orderedSpecification.setSize(size);
        orderedSpecification.setStatuses(Arrays.asList(Status.СТВОРЕНО));
        Sort sort = Sort.by("urgent").descending().and(Sort.by("createdDate").ascending());
        List<Ordered> orderedList = orderRepository.findAll(orderedSpecification, sort);
        List<Marking> markings = getAndSaveMarking(orderedList);
        if (!showPrinted) {
            markings = markings.stream().filter(marking -> !marking.isPrinted()).collect(Collectors.toList());
        }
        return markings;
    }

    private Marking getAndSaveMarking(Ordered ordered) {
        Marking marking = markingRepository.findByOrderedId(ordered.getId());
        if (marking == null) {
            marking = markingRepository.save(new Marking(ordered, postaRepository.getMarking(ordered)));
        }
        return marking;
    }

    private List<Marking> getAndSaveMarking(List<Ordered> orderedList) {
        return orderedList.stream().map(this::getAndSaveMarking).collect(Collectors.toList());
    }

    public Marking setPrinted(Long orderedId) {
        Marking marking = markingRepository.findByOrderedId(orderedId);
        marking.setPrinted(true);
        return markingRepository.save(marking);
    }

}
