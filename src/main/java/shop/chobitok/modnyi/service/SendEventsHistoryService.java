package shop.chobitok.modnyi.service;

import org.springframework.http.HttpStatus;
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
        return sendEventsHistory(restResponseDTO.getBody(), restResponseDTO.getHttpStatus(),
                restResponseDTO.getUrl(), restResponseDTO.getMessage(), appOrderToPixel);
    }

    public SendEventsHistory sendEventsHistory(String message, AppOrderToPixel appOrderToPixel) {
        return sendEventsHistory(null, null, null, message, appOrderToPixel);
    }

    public SendEventsHistory sendEventsHistory(String body, HttpStatus httpStatus, String url,
                                               String message, AppOrderToPixel appOrderToPixel) {
        SendEventsHistory sendEventsHistory = new SendEventsHistory();
        sendEventsHistory.setBody(body);
        if (httpStatus != null) {
            sendEventsHistory.setHttpStatus(
                    httpStatus.value());
        }
        sendEventsHistory.setUrl(url);
        sendEventsHistory.setMessage(message);
        sendEventsHistory.setAppOrderToPixel(appOrderToPixel);
        return sendEventsHistoryRepository.save(sendEventsHistory);
    }
}
