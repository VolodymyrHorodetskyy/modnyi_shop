package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.StatusChangeRecord;
import shop.chobitok.modnyi.repository.StatusChangeRepository;

import java.util.List;

@Service
public class StatusChangeService {

    private StatusChangeRepository statusChangeRepository;


    public StatusChangeService(StatusChangeRepository statusChangeRepository) {
        this.statusChangeRepository = statusChangeRepository;
    }

    public StatusChangeRecord createRecord(Ordered ordered, Status previous, Status current) {
        return statusChangeRepository.save(new StatusChangeRecord(ordered, previous, current));
    }

    public List<StatusChangeRecord> getAll(){
        return statusChangeRepository.findAll();
    }


}
