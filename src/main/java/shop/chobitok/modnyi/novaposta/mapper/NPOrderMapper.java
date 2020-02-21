package shop.chobitok.modnyi.novaposta.mapper;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;

import java.util.List;


@Service
public class NPOrderMapper {

    public Ordered toOrdered(TrackingEntity trackingEntity) {
        Ordered ordered = null;
        if (trackingEntity != null) {
            List<Data> dataList = trackingEntity.getData();
            if (dataList != null && dataList.size() > 0) {
                ordered = new Ordered();
                Data data = dataList.get(0);
                ordered.setTtn(data.getNumber());
                ordered.setAddress(data.getRecipientAddress());
                ordered.setStatus(convertToStatus(Integer.parseInt(data.getStatusCode())));
            }
        }
        return ordered;
    }

    private Status convertToStatus(Integer statusCode) {
        if (statusCode != null) {
            if (statusCode == 4 || statusCode == 41 || statusCode == 5 || statusCode == 6 || statusCode == 101) {
                return Status.SENT;
            } else if (statusCode == 2) {
                return Status.DELETED;
            } else if (statusCode == 1) {
                return Status.CREATED;
            } else if (statusCode == 7 || statusCode == 8) {
                return Status.DELIVERED;
            } else if (statusCode == 102 || statusCode == 103 || statusCode == 108) {
                return Status.DENIED;
            } else if (statusCode == 9 || statusCode == 10 || statusCode == 11) {
                return Status.RECEIVED;
            }
        }
        return null;
    }


}
