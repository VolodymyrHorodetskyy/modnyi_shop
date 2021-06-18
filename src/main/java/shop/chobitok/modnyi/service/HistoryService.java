package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.History;
import shop.chobitok.modnyi.entity.HistoryType;
import shop.chobitok.modnyi.repository.HistoryRepository;

@Service
public class HistoryService {

    private HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public History addHistoryRecord(HistoryType type, String record) {
        return historyRepository.save(new History(type, record));
    }

}
