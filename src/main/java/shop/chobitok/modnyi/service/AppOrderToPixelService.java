package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.AppOrder;
import shop.chobitok.modnyi.entity.AppOrderToPixel;
import shop.chobitok.modnyi.repository.AppOrderToPixelRepository;

@Service
public class AppOrderToPixelService {

    private AppOrderToPixelRepository appOrderToPixelRepository;

    public AppOrderToPixelService(AppOrderToPixelRepository appOrderToPixelRepository) {
        this.appOrderToPixelRepository = appOrderToPixelRepository;
    }

    public AppOrderToPixel save(AppOrder appOrder) {
        return appOrderToPixelRepository.save(new AppOrderToPixel(appOrder));
    }

}
