package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.dto.StatusDto;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.specification.OrderedSpecification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        if (status == Status.СТВОРЕНО) {
            return "Створено";
        } else if (status == Status.ВІДПРАВЛЕНО) {
            return "Відправлено";
        } else if (status == Status.ДОСТАВЛЕНО) {
            return "Прибуло";
        } else if (status == Status.ОТРИМАНО) {
            return "Отримано";
        } else if (status == Status.ВІДМОВА) {
            return "Відмова";
        } else if (status == Status.ВИДАЛЕНО) {
            return "Видалено";
        }
        return null;
    }

}
