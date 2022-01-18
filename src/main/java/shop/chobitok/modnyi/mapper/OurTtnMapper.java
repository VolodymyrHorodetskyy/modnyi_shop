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

    public List<OurTTN> toOurTtn(List<Data> data, Long npAccountId) {
        return data.stream().map(data1 -> toOurTtn(data1, npAccountId)).collect(Collectors.toList());
    }

    public OurTTN toOurTtn(Data data, Long npAccountId) {
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
