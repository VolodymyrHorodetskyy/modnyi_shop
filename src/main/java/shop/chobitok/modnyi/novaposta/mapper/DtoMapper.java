package shop.chobitok.modnyi.novaposta.mapper;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.novaposta.dto.ListTrackingEntityDto;
import shop.chobitok.modnyi.novaposta.entity.DataForList;
import shop.chobitok.modnyi.novaposta.entity.ListTrackingEntity;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DtoMapper {

    private ListTrackingEntityDto toListTrackingDto(DataForList data) {
        ListTrackingEntityDto listTrackingEntityDto = null;
        if (data != null) {
            listTrackingEntityDto = new ListTrackingEntityDto();
            listTrackingEntityDto.setRecipient(data.getRecipientContactPerson());
            listTrackingEntityDto.setStatus(ShoeUtil.convertToStatus(Integer.parseInt(data.getStateId())));
            listTrackingEntityDto.setTTN(data.getIntDocNumber());
        }
        return listTrackingEntityDto;
    }

    public List<ListTrackingEntityDto> dtoList(ListTrackingEntity entity) {
        return entity.getData().stream().map(this::toListTrackingDto).collect(Collectors.toList());
    }

}
