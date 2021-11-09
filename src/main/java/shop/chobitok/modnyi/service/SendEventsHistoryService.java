package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AppOrderToPixel;
import shop.chobitok.modnyi.entity.SendEventsHistory;
import shop.chobitok.modnyi.facebook.RestResponseDTO;
import shop.chobitok.modnyi.repository.SendEventsHistoryRepository;

@Service
public class SendEventsHistoryService {

    private final SendEventsHistoryRepository sendEventsHistoryRepository;

    public SendEventsHistoryService(SendEventsHistoryRepository sendEventsHistoryRepository) {
        this.sendEventsHistoryRepository = sendEventsHistoryRepository;
    }

    public SendEventsHistory sendEventsHistory(RestResponseDTO restResponseDTO, AppOrderToPixel appOrderToPixel) {
        SendEventsHistory sendEventsHistory = new SendEventsHistory();
        sendEventsHistory.setBody(restResponseDTO.getBody());
        if (restResponseDTO.getHttpStatus() != null) {
            sendEventsHistory.setHttpStatus(
                    restResponseDTO.getHttpStatus().value());
        }
        sendEventsHistory.setUrl(restResponseDTO.getUrl());
        sendEventsHistory.setMessage(restResponseDTO.getMessage());
        sendEventsHistory.setAppOrderToPixel(appOrderToPixel);
        return sendEventsHistoryRepository.save(sendEventsHistory);
    }
}
