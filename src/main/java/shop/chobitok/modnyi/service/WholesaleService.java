package shop.chobitok.modnyi.service;

import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.WholesaleOrder;
import shop.chobitok.modnyi.entity.request.AddWholesaleOrderRequest;
import shop.chobitok.modnyi.entity.request.DoCompanyFinanceControlOperationRequest;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.repository.WholesaleRepository;

@Service
public class WholesaleService {

    private final WholesaleRepository wholesaleRepository;
    private final CompanyFinanceControlService companyFinanceControlService;

    public WholesaleService(WholesaleRepository wholesaleRepository, CompanyFinanceControlService companyFinanceControlService) {
        this.wholesaleRepository = wholesaleRepository;
        this.companyFinanceControlService = companyFinanceControlService;
    }

    public WholesaleOrder saveWholesaleOrder(AddWholesaleOrderRequest request) {
        return wholesaleRepository.save(mapToWholesaleOrder(request));
    }

    public void addToCompanyFinanceControl(Long wholesaleOrderId) {
        WholesaleOrder wholesaleOrder = wholesaleRepository.findById(wholesaleOrderId)
                .orElseThrow(() -> new ConflictException("Wholesale order not found"));
        if (wholesaleOrder.isCompleted()) {
            throw new ConflictException("Wholesale order already completed");
        } else {
            companyFinanceControlService.doOperation(new DoCompanyFinanceControlOperationRequest(
                    wholesaleOrder.getCompany().getId(), wholesaleOrder.getCost(), wholesaleOrder.getOrderDescription()
            ));
            wholesaleOrder.setCompleted(true);
            wholesaleRepository.save(wholesaleOrder);
        }
    }

    private WholesaleOrder mapToWholesaleOrder(AddWholesaleOrderRequest request) {
        WholesaleOrder wholesaleOrder = null;
        if (request != null) {
            wholesaleOrder = new WholesaleOrder();
            wholesaleOrder.setOrderDescription(request.getOrderDescription());
            wholesaleOrder.setCompleted(request.isCompleted());
            wholesaleOrder.setCost(request.getCost());
            wholesaleOrder.setPayed(request.getPayed());
            wholesaleOrder.setShouldBePayed(request.getShouldBePayed());
        }
        return wholesaleOrder;
    }
}
