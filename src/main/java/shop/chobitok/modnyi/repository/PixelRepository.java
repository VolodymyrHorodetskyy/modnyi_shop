package shop.chobitok.modnyi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chobitok.modnyi.entity.Pixel;

public interface PixelRepository extends JpaRepository<Pixel, Long> {

    Pixel findOneByPixelId(String pixelId);

}
