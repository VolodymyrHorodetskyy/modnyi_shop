package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataForList {

    private String Ref;
    private String DeletionMark;
    private String DateTime;
    private String PreferredDeliveryDate;
    private String Weight;
    private String SeatsAmount;
    private String IntDocNumber;
    private Double Cost;
    private String ServiceType;
    private String Description;
    private String CitySender;
    private String CityRecipient;
    private String State;
    private String SenderAddress;
    private String RecipientAddress;
    private String Sender;
    private String ContactSender;
    private String Recipient;
    private String ContactRecipient;
    private String CostOnSite;
    private String PayerType;
    private String PaymentMethod;
    private String AfterpaymentOnGoodsCost;
    private String CargoType;
    private String PackingNumber;
    private String AdditionalInformation;
    private String SendersPhone;
    private String RecipientsPhone;
    private String LoyaltyCard;
    private String Posted = null;
    private String Route = null;
    private String EWNumber = null;
    private String SaturdayDelivery = null;
    private String ExpressWaybill = null;
    private String CarCall = null;
    private String DeliveryDateFrom = null;
    private String Vip = null;
    private String LastModificationDate = null;
    private String ReceiptDate = null;
    private String Redelivery = null;
    private String SaturdayDeliveryString = null;
    private String Note = null;
    private String ThirdPerson = null;
    private String Forwarding = null;
    private String NumberOfFloorsLifting = null;
    private String StatementOfAcceptanceTransferCargoID = null;
    private String StateId;
    private String StateName;
    private String RecipientFullName = null;
    private String RecipientPost = null;
    private String RecipientDateTime = null;
    private String RejectionReason = null;
    private String OnlineCreditStatus = null;
    private String CitySenderDescription;
    private String CityRecipientDescription;
    private String SenderDescription;
    private String RecipientDescription;
    private String RecipientContactPhone;
    private String RecipientContactPerson;
    private String SenderAddressDescription;
    private String RecipientAddressDescription;
    private String Printed;
    private String ChangedDataEW;
    private String EWDateCreated = null;
    private String ScheduledDeliveryDate = null;
    private String EstimatedDeliveryDate;
    private String DateLastUpdatedStatus;
    private String DateLastPrint = null;
    private String CreateTime;
    private String ScanSheetNumber;
    private String ScanSheetPrinted;
    private String InfoRegClientBarcodes;
    private String StatePayId;
    private String StatePayName;
    private String BackwardDeliveryCargoType;
    private String BackwardDeliverySum;
    private float BackwardDeliveryMoney;
    private String MarketplacePartnerDescription;
    private String SenderCounterpartyType;
    private String ElevatorRecipient;
    private String RecipientCounterpartyType;
    private String DeliveryByHand;
    private float ForwardingCount;
    private String OwnershipForm;
    private String EDRPOU;
    private String RedBoxBarcode;
    private String RecipientCityRef;
    private String RecipientStreetRef;
    private String RecipientWarehouseRef;
    private String IsTakeAttorney;
    private String SameDayDelivery;
    private String TimeInterval;
    private String TimeIntervalRef;
    private String TimeIntervalString;
    private String ExpressPallet;
    private float TermExtension;
    private String TermExtensionDays;
    private float AviaDelivery;

    // Getter Methods

    public String getRef() {
        return Ref;
    }

    public String getDeletionMark() {
        return DeletionMark;
    }

    public String getDateTime() {
        return DateTime;
    }

    public String getPreferredDeliveryDate() {
        return PreferredDeliveryDate;
    }

    public String getWeight() {
        return Weight;
    }

    public String getSeatsAmount() {
        return SeatsAmount;
    }

    public String getIntDocNumber() {
        return IntDocNumber;
    }


    public String getServiceType() {
        return ServiceType;
    }

    public String getDescription() {
        return Description;
    }

    public String getCitySender() {
        return CitySender;
    }

    public String getCityRecipient() {
        return CityRecipient;
    }

    public String getState() {
        return State;
    }

    public String getSenderAddress() {
        return SenderAddress;
    }

    public String getRecipientAddress() {
        return RecipientAddress;
    }

    public String getSender() {
        return Sender;
    }

    public String getContactSender() {
        return ContactSender;
    }

    public String getRecipient() {
        return Recipient;
    }

    public String getContactRecipient() {
        return ContactRecipient;
    }

    public String getCostOnSite() {
        return CostOnSite;
    }

    public String getPayerType() {
        return PayerType;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public String getAfterpaymentOnGoodsCost() {
        return AfterpaymentOnGoodsCost;
    }

    public String getCargoType() {
        return CargoType;
    }

    public String getPackingNumber() {
        return PackingNumber;
    }

    public String getAdditionalInformation() {
        return AdditionalInformation;
    }

    public String getSendersPhone() {
        return SendersPhone;
    }

    public String getRecipientsPhone() {
        return RecipientsPhone;
    }

    public String getLoyaltyCard() {
        return LoyaltyCard;
    }

    public String getPosted() {
        return Posted;
    }

    public String getRoute() {
        return Route;
    }

    public String getEWNumber() {
        return EWNumber;
    }

    public String getSaturdayDelivery() {
        return SaturdayDelivery;
    }

    public String getExpressWaybill() {
        return ExpressWaybill;
    }

    public String getCarCall() {
        return CarCall;
    }

    public String getDeliveryDateFrom() {
        return DeliveryDateFrom;
    }

    public String getVip() {
        return Vip;
    }

    public String getLastModificationDate() {
        return LastModificationDate;
    }

    public String getReceiptDate() {
        return ReceiptDate;
    }

    public String getRedelivery() {
        return Redelivery;
    }

    public String getSaturdayDeliveryString() {
        return SaturdayDeliveryString;
    }

    public String getNote() {
        return Note;
    }

    public String getThirdPerson() {
        return ThirdPerson;
    }

    public String getForwarding() {
        return Forwarding;
    }

    public String getNumberOfFloorsLifting() {
        return NumberOfFloorsLifting;
    }

    public String getStatementOfAcceptanceTransferCargoID() {
        return StatementOfAcceptanceTransferCargoID;
    }

    public String getStateId() {
        return StateId;
    }

    public String getStateName() {
        return StateName;
    }

    public String getRecipientFullName() {
        return RecipientFullName;
    }

    public String getRecipientPost() {
        return RecipientPost;
    }

    public String getRecipientDateTime() {
        return RecipientDateTime;
    }

    public String getRejectionReason() {
        return RejectionReason;
    }

    public String getOnlineCreditStatus() {
        return OnlineCreditStatus;
    }

    public String getCitySenderDescription() {
        return CitySenderDescription;
    }

    public String getCityRecipientDescription() {
        return CityRecipientDescription;
    }

    public String getSenderDescription() {
        return SenderDescription;
    }

    public String getRecipientDescription() {
        return RecipientDescription;
    }

    public String getRecipientContactPhone() {
        return RecipientContactPhone;
    }

    public String getRecipientContactPerson() {
        return RecipientContactPerson;
    }

    public String getSenderAddressDescription() {
        return SenderAddressDescription;
    }

    public String getRecipientAddressDescription() {
        return RecipientAddressDescription;
    }

    public String getPrinted() {
        return Printed;
    }

    public String getChangedDataEW() {
        return ChangedDataEW;
    }

    public String getEWDateCreated() {
        return EWDateCreated;
    }

    public String getScheduledDeliveryDate() {
        return ScheduledDeliveryDate;
    }

    public String getEstimatedDeliveryDate() {
        return EstimatedDeliveryDate;
    }

    public String getDateLastUpdatedStatus() {
        return DateLastUpdatedStatus;
    }

    public String getDateLastPrint() {
        return DateLastPrint;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public String getScanSheetNumber() {
        return ScanSheetNumber;
    }

    public String getScanSheetPrinted() {
        return ScanSheetPrinted;
    }

    public String getInfoRegClientBarcodes() {
        return InfoRegClientBarcodes;
    }

    public String getStatePayId() {
        return StatePayId;
    }

    public String getStatePayName() {
        return StatePayName;
    }

    public String getBackwardDeliveryCargoType() {
        return BackwardDeliveryCargoType;
    }

    public String getBackwardDeliverySum() {
        return BackwardDeliverySum;
    }

    public float getBackwardDeliveryMoney() {
        return BackwardDeliveryMoney;
    }

    public String getMarketplacePartnerDescription() {
        return MarketplacePartnerDescription;
    }

    public String getSenderCounterpartyType() {
        return SenderCounterpartyType;
    }

    public String getElevatorRecipient() {
        return ElevatorRecipient;
    }

    public String getRecipientCounterpartyType() {
        return RecipientCounterpartyType;
    }

    public String getDeliveryByHand() {
        return DeliveryByHand;
    }

    public float getForwardingCount() {
        return ForwardingCount;
    }


    public String getOwnershipForm() {
        return OwnershipForm;
    }

    public String getEDRPOU() {
        return EDRPOU;
    }

    public String getRedBoxBarcode() {
        return RedBoxBarcode;
    }

    public String getRecipientCityRef() {
        return RecipientCityRef;
    }

    public String getRecipientStreetRef() {
        return RecipientStreetRef;
    }

    public String getRecipientWarehouseRef() {
        return RecipientWarehouseRef;
    }

    public String getIsTakeAttorney() {
        return IsTakeAttorney;
    }

    public String getSameDayDelivery() {
        return SameDayDelivery;
    }

    public String getTimeInterval() {
        return TimeInterval;
    }

    public String getTimeIntervalRef() {
        return TimeIntervalRef;
    }

    public String getTimeIntervalString() {
        return TimeIntervalString;
    }

    public String getExpressPallet() {
        return ExpressPallet;
    }

    public float getTermExtension() {
        return TermExtension;
    }

    public String getTermExtensionDays() {
        return TermExtensionDays;
    }

    public float getAviaDelivery() {
        return AviaDelivery;
    }


    // Setter Methods

    public void setRef(String Ref) {
        this.Ref = Ref;
    }

    public void setDeletionMark(String DeletionMark) {
        this.DeletionMark = DeletionMark;
    }

    public void setDateTime(String DateTime) {
        this.DateTime = DateTime;
    }

    public void setPreferredDeliveryDate(String PreferredDeliveryDate) {
        this.PreferredDeliveryDate = PreferredDeliveryDate;
    }

    public void setWeight(String Weight) {
        this.Weight = Weight;
    }

    public void setSeatsAmount(String SeatsAmount) {
        this.SeatsAmount = SeatsAmount;
    }

    public void setIntDocNumber(String IntDocNumber) {
        this.IntDocNumber = IntDocNumber;
    }

    public void setServiceType(String ServiceType) {
        this.ServiceType = ServiceType;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setCitySender(String CitySender) {
        this.CitySender = CitySender;
    }

    public void setCityRecipient(String CityRecipient) {
        this.CityRecipient = CityRecipient;
    }

    public void setState(String State) {
        this.State = State;
    }

    public void setSenderAddress(String SenderAddress) {
        this.SenderAddress = SenderAddress;
    }

    public void setRecipientAddress(String RecipientAddress) {
        this.RecipientAddress = RecipientAddress;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public void setContactSender(String ContactSender) {
        this.ContactSender = ContactSender;
    }

    public void setRecipient(String Recipient) {
        this.Recipient = Recipient;
    }

    public void setContactRecipient(String ContactRecipient) {
        this.ContactRecipient = ContactRecipient;
    }

    public void setCostOnSite(String CostOnSite) {
        this.CostOnSite = CostOnSite;
    }

    public void setPayerType(String PayerType) {
        this.PayerType = PayerType;
    }

    public void setPaymentMethod(String PaymentMethod) {
        this.PaymentMethod = PaymentMethod;
    }

    public void setAfterpaymentOnGoodsCost(String AfterpaymentOnGoodsCost) {
        this.AfterpaymentOnGoodsCost = AfterpaymentOnGoodsCost;
    }

    public void setCargoType(String CargoType) {
        this.CargoType = CargoType;
    }

    public void setPackingNumber(String PackingNumber) {
        this.PackingNumber = PackingNumber;
    }

    public void setAdditionalInformation(String AdditionalInformation) {
        this.AdditionalInformation = AdditionalInformation;
    }

    public void setSendersPhone(String SendersPhone) {
        this.SendersPhone = SendersPhone;
    }

    public void setRecipientsPhone(String RecipientsPhone) {
        this.RecipientsPhone = RecipientsPhone;
    }

    public void setLoyaltyCard(String LoyaltyCard) {
        this.LoyaltyCard = LoyaltyCard;
    }

    public void setPosted(String Posted) {
        this.Posted = Posted;
    }

    public void setRoute(String Route) {
        this.Route = Route;
    }

    public void setEWNumber(String EWNumber) {
        this.EWNumber = EWNumber;
    }

    public void setSaturdayDelivery(String SaturdayDelivery) {
        this.SaturdayDelivery = SaturdayDelivery;
    }

    public void setExpressWaybill(String ExpressWaybill) {
        this.ExpressWaybill = ExpressWaybill;
    }

    public void setCarCall(String CarCall) {
        this.CarCall = CarCall;
    }

    public void setDeliveryDateFrom(String DeliveryDateFrom) {
        this.DeliveryDateFrom = DeliveryDateFrom;
    }

    public void setVip(String Vip) {
        this.Vip = Vip;
    }

    public void setLastModificationDate(String LastModificationDate) {
        this.LastModificationDate = LastModificationDate;
    }

    public void setReceiptDate(String ReceiptDate) {
        this.ReceiptDate = ReceiptDate;
    }

    public void setRedelivery(String Redelivery) {
        this.Redelivery = Redelivery;
    }

    public void setSaturdayDeliveryString(String SaturdayDeliveryString) {
        this.SaturdayDeliveryString = SaturdayDeliveryString;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }

    public void setThirdPerson(String ThirdPerson) {
        this.ThirdPerson = ThirdPerson;
    }

    public void setForwarding(String Forwarding) {
        this.Forwarding = Forwarding;
    }

    public void setNumberOfFloorsLifting(String NumberOfFloorsLifting) {
        this.NumberOfFloorsLifting = NumberOfFloorsLifting;
    }

    public void setStatementOfAcceptanceTransferCargoID(String StatementOfAcceptanceTransferCargoID) {
        this.StatementOfAcceptanceTransferCargoID = StatementOfAcceptanceTransferCargoID;
    }

    public void setStateId(String StateId) {
        this.StateId = StateId;
    }

    public void setStateName(String StateName) {
        this.StateName = StateName;
    }

    public void setRecipientFullName(String RecipientFullName) {
        this.RecipientFullName = RecipientFullName;
    }

    public void setRecipientPost(String RecipientPost) {
        this.RecipientPost = RecipientPost;
    }

    public void setRecipientDateTime(String RecipientDateTime) {
        this.RecipientDateTime = RecipientDateTime;
    }

    public void setRejectionReason(String RejectionReason) {
        this.RejectionReason = RejectionReason;
    }

    public void setOnlineCreditStatus(String OnlineCreditStatus) {
        this.OnlineCreditStatus = OnlineCreditStatus;
    }

    public void setCitySenderDescription(String CitySenderDescription) {
        this.CitySenderDescription = CitySenderDescription;
    }

    public void setCityRecipientDescription(String CityRecipientDescription) {
        this.CityRecipientDescription = CityRecipientDescription;
    }

    public void setSenderDescription(String SenderDescription) {
        this.SenderDescription = SenderDescription;
    }

    public void setRecipientDescription(String RecipientDescription) {
        this.RecipientDescription = RecipientDescription;
    }

    public void setRecipientContactPhone(String RecipientContactPhone) {
        this.RecipientContactPhone = RecipientContactPhone;
    }

    public void setRecipientContactPerson(String RecipientContactPerson) {
        this.RecipientContactPerson = RecipientContactPerson;
    }

    public void setSenderAddressDescription(String SenderAddressDescription) {
        this.SenderAddressDescription = SenderAddressDescription;
    }

    public void setRecipientAddressDescription(String RecipientAddressDescription) {
        this.RecipientAddressDescription = RecipientAddressDescription;
    }

    public void setPrinted(String Printed) {
        this.Printed = Printed;
    }

    public void setChangedDataEW(String ChangedDataEW) {
        this.ChangedDataEW = ChangedDataEW;
    }

    public void setEWDateCreated(String EWDateCreated) {
        this.EWDateCreated = EWDateCreated;
    }

    public void setScheduledDeliveryDate(String ScheduledDeliveryDate) {
        this.ScheduledDeliveryDate = ScheduledDeliveryDate;
    }

    public void setEstimatedDeliveryDate(String EstimatedDeliveryDate) {
        this.EstimatedDeliveryDate = EstimatedDeliveryDate;
    }

    public void setDateLastUpdatedStatus(String DateLastUpdatedStatus) {
        this.DateLastUpdatedStatus = DateLastUpdatedStatus;
    }

    public void setDateLastPrint(String DateLastPrint) {
        this.DateLastPrint = DateLastPrint;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public void setScanSheetNumber(String ScanSheetNumber) {
        this.ScanSheetNumber = ScanSheetNumber;
    }

    public void setScanSheetPrinted(String ScanSheetPrinted) {
        this.ScanSheetPrinted = ScanSheetPrinted;
    }

    public void setInfoRegClientBarcodes(String InfoRegClientBarcodes) {
        this.InfoRegClientBarcodes = InfoRegClientBarcodes;
    }

    public void setStatePayId(String StatePayId) {
        this.StatePayId = StatePayId;
    }

    public void setStatePayName(String StatePayName) {
        this.StatePayName = StatePayName;
    }

    public void setBackwardDeliveryCargoType(String BackwardDeliveryCargoType) {
        this.BackwardDeliveryCargoType = BackwardDeliveryCargoType;
    }

    public void setBackwardDeliverySum(String BackwardDeliverySum) {
        this.BackwardDeliverySum = BackwardDeliverySum;
    }

    public void setBackwardDeliveryMoney(float BackwardDeliveryMoney) {
        this.BackwardDeliveryMoney = BackwardDeliveryMoney;
    }

    public void setMarketplacePartnerDescription(String MarketplacePartnerDescription) {
        this.MarketplacePartnerDescription = MarketplacePartnerDescription;
    }

    public void setSenderCounterpartyType(String SenderCounterpartyType) {
        this.SenderCounterpartyType = SenderCounterpartyType;
    }

    public void setElevatorRecipient(String ElevatorRecipient) {
        this.ElevatorRecipient = ElevatorRecipient;
    }

    public void setRecipientCounterpartyType(String RecipientCounterpartyType) {
        this.RecipientCounterpartyType = RecipientCounterpartyType;
    }

    public void setDeliveryByHand(String DeliveryByHand) {
        this.DeliveryByHand = DeliveryByHand;
    }

    public void setForwardingCount(float ForwardingCount) {
        this.ForwardingCount = ForwardingCount;
    }


    public void setOwnershipForm(String OwnershipForm) {
        this.OwnershipForm = OwnershipForm;
    }

    public void setEDRPOU(String EDRPOU) {
        this.EDRPOU = EDRPOU;
    }

    public void setRedBoxBarcode(String RedBoxBarcode) {
        this.RedBoxBarcode = RedBoxBarcode;
    }

    public void setRecipientCityRef(String RecipientCityRef) {
        this.RecipientCityRef = RecipientCityRef;
    }

    public void setRecipientStreetRef(String RecipientStreetRef) {
        this.RecipientStreetRef = RecipientStreetRef;
    }

    public void setRecipientWarehouseRef(String RecipientWarehouseRef) {
        this.RecipientWarehouseRef = RecipientWarehouseRef;
    }

    public void setIsTakeAttorney(String IsTakeAttorney) {
        this.IsTakeAttorney = IsTakeAttorney;
    }

    public void setSameDayDelivery(String SameDayDelivery) {
        this.SameDayDelivery = SameDayDelivery;
    }

    public void setTimeInterval(String TimeInterval) {
        this.TimeInterval = TimeInterval;
    }

    public void setTimeIntervalRef(String TimeIntervalRef) {
        this.TimeIntervalRef = TimeIntervalRef;
    }

    public void setTimeIntervalString(String TimeIntervalString) {
        this.TimeIntervalString = TimeIntervalString;
    }

    public void setExpressPallet(String ExpressPallet) {
        this.ExpressPallet = ExpressPallet;
    }

    public void setTermExtension(float TermExtension) {
        this.TermExtension = TermExtension;
    }

    public void setTermExtensionDays(String TermExtensionDays) {
        this.TermExtensionDays = TermExtensionDays;
    }

    public void setAviaDelivery(float AviaDelivery) {
        this.AviaDelivery = AviaDelivery;
    }

    public Double getCost() {
        return Cost;
    }

    public void setCost(Double cost) {
        Cost = cost;
    }
}
