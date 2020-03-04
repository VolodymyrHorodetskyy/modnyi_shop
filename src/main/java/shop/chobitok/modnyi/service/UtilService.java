package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.dto.StatusDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UtilService {

    public List<StatusDto> getStatuses() {
        List<StatusDto> statusDtoList = new ArrayList<>();
        for (Status s : Status.values()) {
            StatusDto statusDto = new StatusDto();
            statusDto.setStatus(s.toString());
            statusDto.setUkrStatus(getUkrStatus(s));
            statusDtoList.add(statusDto);
        }
        return statusDtoList;
    }

    private String getUkrStatus(Status status) {
        if (status == Status.CREATED) {
            return "Створено";
        } else if (status == Status.SENT) {
            return "Відправлено";
        } else if (status == Status.DELIVERED) {
            return "Прибуло";
        } else if (status == Status.RECEIVED) {
            return "Отримано";
        } else if (status == Status.DENIED) {
            return "Відмова";
        } else if (status == Status.DELETED) {
            return "Видалено";
        }
        return null;
    }


}
