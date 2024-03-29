package shop.chobitok.modnyi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shop.chobitok.modnyi.entity.CancelReason;
import shop.chobitok.modnyi.entity.OurTTN;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.request.AddOurTtnRequest;
import shop.chobitok.modnyi.entity.request.EditOurTtnRequest;
import shop.chobitok.modnyi.entity.request.ImportOrdersFromStringRequest;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;
import shop.chobitok.modnyi.mapper.OurTtnMapper;
import shop.chobitok.modnyi.novaposta.entity.Data;
import shop.chobitok.modnyi.novaposta.entity.TrackingEntity;
import shop.chobitok.modnyi.novaposta.repository.NovaPostaRepository;
import shop.chobitok.modnyi.novaposta.util.ShoeUtil;
import shop.chobitok.modnyi.repository.CanceledOrderReasonRepository;
import shop.chobitok.modnyi.repository.OrderRepository;
import shop.chobitok.modnyi.repository.OurTtnRepository;
import shop.chobitok.modnyi.service.entity.OurTtnResp;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.isEmpty;
import static shop.chobitok.modnyi.util.StringHelper.splitTTNString;

@Service
public class OurTtnService {

    private final OurTtnMapper ourTtnMapper;
    private final NovaPostaRepository postaRepository;
    private final OrderRepository orderRepository;
    private final CanceledOrderReasonRepository canceledOrderReasonRepository;
    private final OurTtnRepository ourTtnRepository;

    public OurTtnService(OurTtnMapper ourTtnMapper, NovaPostaRepository postaRepository, OrderRepository orderRepository, CanceledOrderReasonRepository canceledOrderReasonRepository, OurTtnRepository ourTtnRepository) {
        this.ourTtnMapper = ourTtnMapper;
        this.postaRepository = postaRepository;
        this.orderRepository = orderRepository;
        this.canceledOrderReasonRepository = canceledOrderReasonRepository;
        this.ourTtnRepository = ourTtnRepository;
    }

    public List<OurTTN> getAll(List<Status> statusesNotIn) {
        return ourTtnRepository.findAllByStatusNotIn(statusesNotIn);
    }

    public StringResponse addNewTtn(AddOurTtnRequest request) {
        String result = checkIfExistOnImportWithReturnString(request.getTtn());
        StringResponse stringResponse = new StringResponse();
        if (isEmpty(result)) {
            OurTTN ourTTN = ourTtnMapper.toOurTtn(postaRepository.getTracking(request.getNpAccountId(), request.getTtn()).getData().get(0), request.getNpAccountId());
            if (ourTTN == null || ourTTN.getStatus() == Status.НЕ_ЗНАЙДЕНО) {
                stringResponse.setOk(false);
                result = request.getTtn() + " " + "Не Знайдено";
            } else {
                ourTTN.setCargoDescription(request.getCargoDescription());
                ourTTN.setCancelReason(request.getCancelReason());
                ourTTN.setComment(request.getComment());
                result = request.getTtn() + " " + "Добавлено";
                ourTtnRepository.save(ourTTN);
            }
        } else {
            stringResponse.setOk(false);
        }
        stringResponse.setResult(result);
        return stringResponse;
    }

    public StringResponse editOurTtn(EditOurTtnRequest request) {
        OurTTN ourTTN = ourTtnRepository.findById(request.getOurTtnId()).orElse(null);
        if (ourTTN != null) {
            ourTTN.setComment(request.getComment());
            ourTTN.setNpAccountId(request.getNpAccountId());
            ourTTN.setCargoDescription(request.getCargoDescription());
            ourTTN.setCancelReason(request.getCancelReason());
            ourTtnRepository.save(ourTTN);
        }
        return new StringResponse("Редаговано");
    }

    public StringResponse receive(ImportOrdersFromStringRequest request) {
        if (request.getNpAccountId() == null) {
            throw new ConflictException("Акаунт не вказано");
        }
        StringBuilder result = new StringBuilder();
        List<String> splitted = splitTTNString(request.getTtns());
        List<String> filtered = new ArrayList<>();
        for (String ttn : splitted) {
            String r = checkIfExistOnImportWithReturnString(ttn);
            if (r == null) {
                filtered.add(ttn);
            } else {
                result.append(r);
            }
        }
        List<OurTTN> ourTTNS = ourTtnMapper.toOurTtn(postaRepository.getTrackingByTtns(request.getNpAccountId(), filtered), request.getNpAccountId());
        for (OurTTN ourTTN : ourTTNS) {
            if (ourTTN.getStatus() == Status.НЕ_ЗНАЙДЕНО) {
                result.append(" не знайдено");
            } else {
                ourTtnRepository.save(ourTTN);
                result.append(ourTTN.getTtn()).append(" збережено");
            }
        }
        return new StringResponse(result.toString());
    }

    public void updateStatusesOurTtns() {
        List<OurTTN> ourTTNS = ourTtnRepository.findAllByStatusNot(Status.ОТРИМАНО);
        Set<OurTTN> toUpdate = new HashSet<>();
        for (OurTTN ourTTN : ourTTNS) {
            if (checkIfExist(ourTTN.getTtn())) {
                ourTTN.setDeleted(true);
                toUpdate.add(ourTTN);
            } else {
                ourTTN.setDeleted(false);
                toUpdate.add(ourTTN);
            }
            TrackingEntity trackingEntity = postaRepository.getTracking(ourTTN.getNpAccountId(), ourTTN.getTtn());
            Data data = trackingEntity.getData().get(0);
            Status newStatus = ShoeUtil.convertToStatus(data.getStatusCode());
            if (newStatus != null && newStatus != ourTTN.getStatus()) {
                ourTTN.setStatus(newStatus);
                if (newStatus == Status.ДОСТАВЛЕНО) {
                    ourTTN.setDatePayedKeeping(ShoeUtil.toLocalDateTime(data.getDatePayedKeeping()));
                }
                toUpdate.add(ourTTN);
            }
        }
        ourTtnRepository.saveAll(toUpdate);
    }

    public Page getTtns(int page, int size, boolean showDeletedAndReceived) {
        Page pageObject;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        if (showDeletedAndReceived) {
            pageObject = ourTtnRepository.findAll(pageable);
        } else {
            pageObject = ourTtnRepository.findAllByDeletedFalseAndStatusNotIn(
                    asList(Status.СТВОРЕНО, Status.ОТРИМАНО, Status.ВІДМОВА, Status.ВИДАЛЕНО, Status.НЕ_ЗНАЙДЕНО), pageable);
        }
        return pageObject;
    }


    public String checkIfExistOnImportWithReturnString(String ttn) {
        StringBuilder stringBuilder = new StringBuilder();
        String result = null;
        if (canceledOrderReasonRepository.findFirstByReturnTtn(ttn) != null) {
            result = "існує в поверненнях";
        } else if (orderRepository.findOneByAvailableTrueAndTtn(ttn) != null) {
            result = "Існує в замовленнях";
        } else if (ourTtnRepository.findFirstByTtn(ttn) != null) {
            result = "існує в наших ттн";
        }
        if (result != null) {
            stringBuilder.append(ttn).append(" ").append(result).append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean checkIfExist(String ttn) {
        boolean result = false;
        if (canceledOrderReasonRepository.findFirstByReturnTtn(ttn) != null) {
            result = true;
        } else if (orderRepository.findOneByAvailableTrueAndTtn(ttn) != null) {
            result = true;
        }
        return result;
    }

    public OurTtnResp formOurTtnResp() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("d.MM");
        List<OurTTN> ourTTNS = ourTtnRepository.findAllByStatusIn(asList(Status.ВІДПРАВЛЕНО, Status.ДОСТАВЛЕНО));
        StringBuilder allOurTtnsStringBuilder = new StringBuilder();
        StringBuilder payedKeepingStringBuilder = new StringBuilder();
        StringBuilder needAttentionStringBuilder = new StringBuilder();
        for (OurTTN ourTTN : ourTTNS) {
            allOurTtnsStringBuilder.append(ourTTN.getTtn()).append(" ").append(ourTTN.getStatus()).append("\n")
                    .append(ourTTN.getCancelReason()).append(" ").append(ourTTN.getComment()).append("\n")
                    .append(ourTTN.getCargoDescription()).append("\n\n");
            if (ourTTN.getDatePayedKeeping() != null &&
                    now().plusDays(2).isAfter(ourTTN.getDatePayedKeeping())) {
                payedKeepingStringBuilder.append(ourTTN.getComment()).append("\n").
                        append(ourTTN.getTtn()).append("\n")
                        .append(ourTTN.getStatus()).append(" ").append(ourTTN.getCancelReason())
                        .append(" ").append(isEmpty(ourTTN.getComment()) ? "" : ourTTN.getComment())
                        .append("\n")
                        .append(ourTTN.getDatePayedKeeping().format(timeFormatter))
                        .append("\n\n");
            }
            if (ourTTN.getCancelReason() == CancelReason.БРАК
                    || ourTTN.getCancelReason() == CancelReason.ПОМИЛКА) {
                needAttentionStringBuilder
                        .append(ourTTN.getTtn()).append(" ").append(ourTTN.getStatus()).append("\n")
                        .append(ourTTN.getCancelReason()).append(" ")
                        .append(isEmpty(ourTTN.getComment()) ? "" : ourTTN.getComment())
                        .append("\n\n");
            }
        }
        return new OurTtnResp(allOurTtnsStringBuilder.toString(),
                payedKeepingStringBuilder.toString(),
                needAttentionStringBuilder.toString());
    }
}