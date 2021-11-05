package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.Pixel;
import shop.chobitok.modnyi.repository.PixelRepository;

@Service
public class PixelService {

    private PixelRepository pixelRepository;

    public PixelService(PixelRepository pixelRepository) {
        this.pixelRepository = pixelRepository;
    }

    public Pixel getPixel(String pixelId) {
        return pixelRepository.findOneByPixelId(pixelId);
    }
}
