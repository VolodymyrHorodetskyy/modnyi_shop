package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderToPixel;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.facebook.RestResponseDTO;
import shop.chobitok.modnyi.repository.AppOrderToPixelRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static shop.chobitok.modnyi.util.DateHelper.formDateTime;

@Service
public class AppOrderToPixelService {

    private final AppOrderToPixelRepository appOrderToPixelRepository;
    private final FacebookApi2 facebookApi2;
    private final SendEventsHistoryService sendEventsHistoryService;

    public AppOrderToPixelService(AppOrderToPixelRepository appOrderToPixelRepository, FacebookApi2 facebookApi2, SendEventsHistoryService sendEventsHistoryService) {
        this.appOrderToPixelRepository = appOrderToPixelRepository;
        this.facebookApi2 = facebookApi2;
        this.sendEventsHistoryService = sendEventsHistoryService;
    }

    public AppOrderToPixel save(AppOrder appOrder) {
        if (appOrderToPixelRepository.findFirstByAppOrderId(appOrder.getId()) == null
                && appOrder.isDataValid()) {
            return appOrderToPixelRepository.save(new AppOrderToPixel(appOrder));
        }
        return null;
    }

    public void makeTry(AppOrderToPixel appOrderToPixel) {
        int trying = appOrderToPixel.getTrying() + 1;
        appOrderToPixel.setTrying(trying);
        appOrderToPixelRepository.save(appOrderToPixel);
    }

    public void sendAllTrying0() {
        sendAll(0);
    }

    public void sendAll(int trying) {
        List<AppOrderToPixel> appOrderToPixelList = appOrderToPixelRepository.findAllBySentFalseAndTrying(trying);
        for (AppOrderToPixel appOrderToPixel : appOrderToPixelList) {
            send(appOrderToPixel);
        }
    }

    public void sendAll(int tryingGreaterThan, String fromString) {
        LocalDateTime from = formDateTime(fromString);
        List<AppOrderToPixel> appOrderToPixelList = appOrderToPixelRepository.findAllBySentFalseAndTryingGreaterThanEqualAndCreatedDateGreaterThanEqual(tryingGreaterThan,
                from);
        for (AppOrderToPixel appOrderToPixel : appOrderToPixelList) {
            send(appOrderToPixel);
        }
    }

    public void send(AppOrderToPixel appOrderToPixel) {
        makeTry(appOrderToPixel);
        RestResponseDTO restResponseDTO = null;
        try {
            restResponseDTO = facebookApi2.send(appOrderToPixel.getAppOrder());

            if (OK == restResponseDTO.getHttpStatus()) {
                appOrderToPixel.setSent(true);
                appOrderToPixelRepository.save(appOrderToPixel);
            }
            sendEventsHistoryService.sendEventsHistory(restResponseDTO, appOrderToPixel);
        } catch (ResourceAccessException e) {
            sendEventsHistoryService.sendEventsHistory(e.getMessage(), appOrderToPixel);
        }
    }
}