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
                ordered.setStatus(convertToStatus(data.getStatusCode()));
            }
        }
        return ordered;
    }

    private Status convertToStatus(Integer statusCode) {
        if (statusCode != null) {
            if (statusCode == 4 || statusCode == 41 || statusCode == 5 || statusCode == 6) {
                return Status.SENT;
            }
        }
        return null;
    }


}
