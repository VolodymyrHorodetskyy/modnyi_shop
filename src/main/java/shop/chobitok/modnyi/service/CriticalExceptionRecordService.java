package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.CriticalExceptionRecord;
import shop.chobitok.modnyi.repository.CriticalExceptionRecordRepository;

@Service
public class CriticalExceptionRecordService {

    private CriticalExceptionRecordRepository criticalExceptionRecordRepository;

    public CriticalExceptionRecord save(String description) {
        return new CriticalExceptionRecord(description);
    }
}
