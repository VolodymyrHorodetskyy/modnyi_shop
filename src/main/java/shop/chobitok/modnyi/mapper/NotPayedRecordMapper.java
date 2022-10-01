package shop.chobitok.modnyi.mapper;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OrderedShoe;
import shop.chobitok.modnyi.entity.PayedOrdered;
import shop.chobitok.modnyi.entity.dto.NotPayedRecord;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class NotPayedRecordMapper {

    public NotPayedRecord mapTo(String ttn, Double cost, OrderedShoe orderedShoe) {
        NotPayedRecord notPayedRecord = null;
        if (orderedShoe != null) {
            notPayedRecord = new NotPayedRecord();
            notPayedRecord.setSum(cost);
            notPayedRecord.setNote(orderedShoe.getComment());
            notPayedRecord.setTtn(ttn);
            notPayedRecord.setOrderedShoeId(orderedShoe.getId());
            notPayedRecord.setModelAndColor(orderedShoe.getShoe().getModelAndColor());
        }
        return notPayedRecord;
    }

    public NotPayedRecord mapTo(PayedOrdered payedOrdered) {
        NotPayedRecord notPayedRecord = null;
        if (payedOrdered != null) {
            notPayedRecord = new NotPayedRecord();
            notPayedRecord.setPayedRecordId(payedOrdered.getId());
            notPayedRecord.setSum(-payedOrdered.getSum());
            notPayedRecord.setTtn(payedOrdered.getOrdered().getTtn());
            notPayedRecord.setModelAndColor(payedOrdered.getOrderedShoe().getShoe().getModelAndColor());
        }
        return notPayedRecord;
    }

    public List<NotPayedRecord> mapTo(List<PayedOrdered> payedOrderedList) {
        return payedOrderedList.stream().map(this::mapTo).collect(toList());
    }
}
