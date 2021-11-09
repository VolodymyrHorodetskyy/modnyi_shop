package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderToPixel;
import shop.chobitok.modnyi.facebook.FacebookApi2;
import shop.chobitok.modnyi.facebook.RestResponseDTO;
import shop.chobitok.modnyi.repository.AppOrderToPixelRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

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
        if (appOrderToPixelRepository.findOneByAppOrderId(appOrder.getId()) == null
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

    public void sendAll() {
        List<AppOrderToPixel> appOrderToPixelList = appOrderToPixelRepository.findAllBySentFalse();
        for (AppOrderToPixel appOrderToPixel : appOrderToPixelList) {
            makeTry(appOrderToPixel);
            RestResponseDTO restResponseDTO = facebookApi2.send(appOrderToPixel.getAppOrder());
            if (OK == restResponseDTO.getHttpStatus()) {
                appOrderToPixel.setSent(true);
                appOrderToPixelRepository.save(appOrderToPixel);
            }
            sendEventsHistoryService.sendEventsHistory(restResponseDTO, appOrderToPixel);
        }
    }
}