package shop.chobitok.modnyi.mapper;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.OurTTN;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;

import java.util.List;
import java.util.stream.Collectors;

import static shop.chobitok.modnyi.novaposta.util.ShoeUtil.convertToStatus;

@Service
public class OurTtnMapper {

    public List<OurTTN> toOurTtn(TrackingEntity trackingEntity, Long npAccountId) {
        return trackingEntity.getData().stream().map(data -> toOurTtn(data, npAccountId)).collect(Collectors.toList());
    }

    private OurTTN toOurTtn(Data data, Long npAccountId) {
        OurTTN ourTTN = null;
        if (data != null) {
            ourTTN = new OurTTN();
            ourTTN.setReceiverPhone(data.getPhoneRecipient());
            ourTTN.setSenderPhone("+" + data.getPhoneSender());
            ourTTN.setTtn(data.getNumber());
            ourTTN.setDatePayedKeeping(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
            ourTTN.setStatus(convertToStatus(data.getStatusCode()));
            ourTTN.setNpAccountId(npAccountId);
        }
        return ourTTN;
    }

}