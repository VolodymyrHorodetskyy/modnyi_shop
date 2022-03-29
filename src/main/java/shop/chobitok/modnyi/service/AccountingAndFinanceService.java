package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AccountingRecord;
import shop.chobitok.modnyi.entity.request.AccountingRecordRequest;
import shop.chobitok.modnyi.repository.AccountingRecordRepository;

@Service
public class AccountingAndFinanceService {

    private final AccountingRecordRepository arr;

    public AccountingAndFinanceService(AccountingRecordRepository arr) {
        this.arr = arr;
    }

    public AccountingRecord addRecord(AccountingRecordRequest request) {
        AccountingRecord lastAccountingRecord = arr.findFirstOrderByCreatedDateDesc();
        return arr.save(new AccountingRecord(request, lastAccountingRecord.getCurrentValue() + request.getOperationValue()));
    }


}
