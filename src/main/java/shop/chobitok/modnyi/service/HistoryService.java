package shop.chobitok.modnyi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.History;
import shop.chobitok.modnyi.entity.HistoryType;
import shop.chobitok.modnyi.repository.HistoryRepository;

import java.util.List;

@Service
public class HistoryService {

    private HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public History addHistoryRecord(HistoryType type, String record) {
        return historyRepository.save(new History(type, record));
    }

    public History addHistoryRecord(HistoryType type, String ttn, String record) {
        return historyRepository.save(new History(type, record, ttn));
    }

    public List<History> getLast20(HistoryType type) {

        return historyRepository.findAllByType(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdDate")), type)
                .getContent();
    }

}
