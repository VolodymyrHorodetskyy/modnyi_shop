package shop.chobitok.modnyi.google.docs.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.google.docs.repository.GoogleDocsRepository;

@Service
public class GoogleDocsService {

    private GoogleDocsRepository docsRepository;

    public GoogleDocsService(GoogleDocsRepository docsRepository) {
        this.docsRepository = docsRepository;
    }

    private String deliveryFileId = "14PZYEr3ny4xu5zd3A9n14t5gk8UH9J6eXCA40jplr7o";
    private String returningsFileId = "1K2mkRH9BMu9SEn-xXGn1e70MwWP1CQUVLwrH5z_ID6Q";

    public void updateDeliveryFile(String text) {
  //      docsRepository.updateDocumentByText(deliveryFileId, text);
    }

    public void updateReturningsFile(String text) {
  //      docsRepository.updateDocumentByText(returningsFileId, text);
    }

}
