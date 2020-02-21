package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    private String Number;
    private String DateCreated;
    private float DocumentWeight;
    private float CheckWeight;
    private float SumBeforeCheckWeight;
    private String PayerType;
    private String RecipientFullName;
    private String RecipientDateTime;
    private String ScheduledDeliveryDate;
    private String PaymentMethod;
    private String CargoDescriptionString;
    private String CargoType;
    private String CitySender;
    private String CityRecipient;
    private String WarehouseRecipient;
    private String CounterpartyType;
    private float Redelivery;
    private float RedeliverySum;
    private String RedeliveryNum;
    private String RedeliveryPayer;
    private float AfterpaymentOnGoodsCost;
    private String ServiceType;
    private String UndeliveryReasonsSubtypeDescription;
    private float WarehouseRecipientNumber;
    private String LastCreatedOnTheBasisNumber;
    private String LastCreatedOnTheBasisDocumentType;
    private String LastCreatedOnTheBasisPayerType;
    private String LastCreatedOnTheBasisDateTime;
    private String LastTransactionStatusGM;
    private String LastTransactionDateTimeGM;
    private String PhoneRecipient;
    private String RecipientFullNameEW;
    private String WarehouseRecipientInternetAddressRef;
    private String MarketplacePartnerToken;
    private String DateScan;
    private String ClientBarcode;
    private String RecipientAddress;
    private String CounterpartyRecipientDescription;
    private String CounterpartySenderType;
    private String PaymentStatus;
    private String PaymentStatusDate;
    private String AmountToPay;
    private String AmountPaid;
    private String WarehouseRecipientRef;
    private float DocumentCost;
    private float AnnouncedPrice;
    private String OwnerDocumentNumber;
    private String DateFirstDayStorage;
    private String InternationalDeliveryType;
    private String DaysStorageCargo;
    private String RecipientWarehouseTypeRef;
    private String StorageAmount;
    private String StoragePrice;
    private String VolumeWeight;
    private String SeatsAmount;
    private String OwnerDocumentType;
    private String ActualDeliveryDate;
    private String DateReturnCargo;
    private String RefCitySender;
    private String RefCityRecipient;
    private String CardMaskedNumber;
    private String BarcodeRedBox;
    private float AviaDelivery;
    private String OnlineCreditStatus;
    private float FreeShipping;
    private String AdjustedDate;
    private String Status;
    private String StatusCode;
    private String RefEW;
    private String DatePayedKeeping;
    private String OnlineCreditStatusCode;


    // Getter Methods

    public String getNumber() {
        return Number;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public float getDocumentWeight() {
        return DocumentWeight;
    }

    public float getCheckWeight() {
        return CheckWeight;
    }

    public float getSumBeforeCheckWeight() {
        return SumBeforeCheckWeight;
    }

    public String getPayerType() {
        return PayerType;
    }

    public String getRecipientFullName() {
        return RecipientFullName;
    }

    public String getRecipientDateTime() {
        return RecipientDateTime;
    }

    public String getScheduledDeliveryDate() {
        return ScheduledDeliveryDate;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public String getCargoDescriptionString() {
        return CargoDescriptionString;
    }

    public String getCargoType() {
        return CargoType;
    }

    public String getCitySender() {
        return CitySender;
    }

    public String getCityRecipient() {
        return CityRecipient;
    }

    public String getWarehouseRecipient() {
        return WarehouseRecipient;
    }

    public String getCounterpartyType() {
        return CounterpartyType;
    }

    public float getRedelivery() {
        return Redelivery;
    }

    public float getRedeliverySum() {
        return RedeliverySum;
    }

    public String getRedeliveryNum() {
        return RedeliveryNum;
    }

    public String getRedeliveryPayer() {
        return RedeliveryPayer;
    }

    public float getAfterpaymentOnGoodsCost() {
        return AfterpaymentOnGoodsCost;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public String getUndeliveryReasonsSubtypeDescription() {
        return UndeliveryReasonsSubtypeDescription;
    }

    public float getWarehouseRecipientNumber() {
        return WarehouseRecipientNumber;
    }

    public String getLastCreatedOnTheBasisNumber() {
        return LastCreatedOnTheBasisNumber;
    }

    public String getLastCreatedOnTheBasisDocumentType() {
        return LastCreatedOnTheBasisDocumentType;
    }

    public String getLastCreatedOnTheBasisPayerType() {
        return LastCreatedOnTheBasisPayerType;
    }

    public String getLastCreatedOnTheBasisDateTime() {
        return LastCreatedOnTheBasisDateTime;
    }

    public String getLastTransactionStatusGM() {
        return LastTransactionStatusGM;
    }

    public String getLastTransactionDateTimeGM() {
        return LastTransactionDateTimeGM;
    }

    public String getPhoneRecipient() {
        return PhoneRecipient;
    }

    public String getRecipientFullNameEW() {
        return RecipientFullNameEW;
    }

    public String getWarehouseRecipientInternetAddressRef() {
        return WarehouseRecipientInternetAddressRef;
    }

    public String getMarketplacePartnerToken() {
        return MarketplacePartnerToken;
    }

    public String getDateScan() {
        return DateScan;
    }

    public String getClientBarcode() {
        return ClientBarcode;
    }

    public String getRecipientAddress() {
        return RecipientAddress;
    }

    public String getCounterpartyRecipientDescription() {
        return CounterpartyRecipientDescription;
    }

    public String getCounterpartySenderType() {
        return CounterpartySenderType;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public String getPaymentStatusDate() {
        return PaymentStatusDate;
    }

    public String getAmountToPay() {
        return AmountToPay;
    }

    public String getAmountPaid() {
        return AmountPaid;
    }

    public String getWarehouseRecipientRef() {
        return WarehouseRecipientRef;
    }

    public float getDocumentCost() {
        return DocumentCost;
    }

    public float getAnnouncedPrice() {
        return AnnouncedPrice;
    }

    public String getOwnerDocumentNumber() {
        return OwnerDocumentNumber;
    }

    public String getDateFirstDayStorage() {
        return DateFirstDayStorage;
    }

    public String getInternationalDeliveryType() {
        return InternationalDeliveryType;
    }

    public String getDaysStorageCargo() {
        return DaysStorageCargo;
    }

    public String getRecipientWarehouseTypeRef() {
        return RecipientWarehouseTypeRef;
    }

    public String getStorageAmount() {
        return StorageAmount;
    }

    public String getStoragePrice() {
        return StoragePrice;
    }

    public String getVolumeWeight() {
        return VolumeWeight;
    }

    public String getSeatsAmount() {
        return SeatsAmount;
    }

    public String getOwnerDocumentType() {
        return OwnerDocumentType;
    }

    public String getActualDeliveryDate() {
        return ActualDeliveryDate;
    }

    public String getDateReturnCargo() {
        return DateReturnCargo;
    }

    public String getRefCitySender() {
        return RefCitySender;
    }

    public String getRefCityRecipient() {
        return RefCityRecipient;
    }

    public String getCardMaskedNumber() {
        return CardMaskedNumber;
    }

    public String getBarcodeRedBox() {
        return BarcodeRedBox;
    }

    public float getAviaDelivery() {
        return AviaDelivery;
    }

    public String getOnlineCreditStatus() {
        return OnlineCreditStatus;
    }

    public float getFreeShipping() {
        return FreeShipping;
    }

    public String getAdjustedDate() {
        return AdjustedDate;
    }

    public String getStatus() {
        return Status;
    }

    public String getStatusCode() {
        return StatusCode;
    }

    public String getRefEW() {
        return RefEW;
    }

    public String getDatePayedKeeping() {
        return DatePayedKeeping;
    }

    public String getOnlineCreditStatusCode() {
        return OnlineCreditStatusCode;
    }

    // Setter Methods

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public void setDateCreated(String DateCreated) {
        this.DateCreated = DateCreated;
    }

    public void setDocumentWeight(float DocumentWeight) {
        this.DocumentWeight = DocumentWeight;
    }

    public void setCheckWeight(float CheckWeight) {
        this.CheckWeight = CheckWeight;
    }

    public void setSumBeforeCheckWeight(float SumBeforeCheckWeight) {
        this.SumBeforeCheckWeight = SumBeforeCheckWeight;
    }

    public void setPayerType(String PayerType) {
        this.PayerType = PayerType;
    }

    public void setRecipientFullName(String RecipientFullName) {
        this.RecipientFullName = RecipientFullName;
    }

    public void setRecipientDateTime(String RecipientDateTime) {
        this.RecipientDateTime = RecipientDateTime;
    }

    public void setScheduledDeliveryDate(String ScheduledDeliveryDate) {
        this.ScheduledDeliveryDate = ScheduledDeliveryDate;
    }

    public void setPaymentMethod(String PaymentMethod) {
        this.PaymentMethod = PaymentMethod;
    }

    public void setCargoDescriptionString(String CargoDescriptionString) {
        this.CargoDescriptionString = CargoDescriptionString;
    }

    public void setCargoType(String CargoType) {
        this.CargoType = CargoType;
    }

    public void setCitySender(String CitySender) {
        this.CitySender = CitySender;
    }

    public void setCityRecipient(String CityRecipient) {
        this.CityRecipient = CityRecipient;
    }

    public void setWarehouseRecipient(String WarehouseRecipient) {
        this.WarehouseRecipient = WarehouseRecipient;
    }

    public void setCounterpartyType(String CounterpartyType) {
        this.CounterpartyType = CounterpartyType;
    }

    public void setRedelivery(float Redelivery) {
        this.Redelivery = Redelivery;
    }

    public void setRedeliverySum(float RedeliverySum) {
        this.RedeliverySum = RedeliverySum;
    }

    public void setRedeliveryNum(String RedeliveryNum) {
        this.RedeliveryNum = RedeliveryNum;
    }

    public void setRedeliveryPayer(String RedeliveryPayer) {
        this.RedeliveryPayer = RedeliveryPayer;
    }

    public void setAfterpaymentOnGoodsCost(float AfterpaymentOnGoodsCost) {
        this.AfterpaymentOnGoodsCost = AfterpaymentOnGoodsCost;
    }

    public void setServiceType(String ServiceType) {
        this.ServiceType = ServiceType;
    }

    public void setUndeliveryReasonsSubtypeDescription(String UndeliveryReasonsSubtypeDescription) {
        this.UndeliveryReasonsSubtypeDescription = UndeliveryReasonsSubtypeDescription;
    }

    public void setWarehouseRecipientNumber(float WarehouseRecipientNumber) {
        this.WarehouseRecipientNumber = WarehouseRecipientNumber;
    }

    public void setLastCreatedOnTheBasisNumber(String LastCreatedOnTheBasisNumber) {
        this.LastCreatedOnTheBasisNumber = LastCreatedOnTheBasisNumber;
    }

    public void setLastCreatedOnTheBasisDocumentType(String LastCreatedOnTheBasisDocumentType) {
        this.LastCreatedOnTheBasisDocumentType = LastCreatedOnTheBasisDocumentType;
    }

    public void setLastCreatedOnTheBasisPayerType(String LastCreatedOnTheBasisPayerType) {
        this.LastCreatedOnTheBasisPayerType = LastCreatedOnTheBasisPayerType;
    }

    public void setLastCreatedOnTheBasisDateTime(String LastCreatedOnTheBasisDateTime) {
        this.LastCreatedOnTheBasisDateTime = LastCreatedOnTheBasisDateTime;
    }

    public void setLastTransactionStatusGM(String LastTransactionStatusGM) {
        this.LastTransactionStatusGM = LastTransactionStatusGM;
    }

    public void setLastTransactionDateTimeGM(String LastTransactionDateTimeGM) {
        this.LastTransactionDateTimeGM = LastTransactionDateTimeGM;
    }

    public void setPhoneRecipient(String PhoneRecipient) {
        this.PhoneRecipient = PhoneRecipient;
    }

    public void setRecipientFullNameEW(String RecipientFullNameEW) {
        this.RecipientFullNameEW = RecipientFullNameEW;
    }

    public void setWarehouseRecipientInternetAddressRef(String WarehouseRecipientInternetAddressRef) {
        this.WarehouseRecipientInternetAddressRef = WarehouseRecipientInternetAddressRef;
    }

    public void setMarketplacePartnerToken(String MarketplacePartnerToken) {
        this.MarketplacePartnerToken = MarketplacePartnerToken;
    }

    public void setDateScan(String DateScan) {
        this.DateScan = DateScan;
    }

    public void setClientBarcode(String ClientBarcode) {
        this.ClientBarcode = ClientBarcode;
    }

    public void setRecipientAddress(String RecipientAddress) {
        this.RecipientAddress = RecipientAddress;
    }

    public void setCounterpartyRecipientDescription(String CounterpartyRecipientDescription) {
        this.CounterpartyRecipientDescription = CounterpartyRecipientDescription;
    }

    public void setCounterpartySenderType(String CounterpartySenderType) {
        this.CounterpartySenderType = CounterpartySenderType;
    }

    public void setPaymentStatus(String PaymentStatus) {
        this.PaymentStatus = PaymentStatus;
    }

    public void setPaymentStatusDate(String PaymentStatusDate) {
        this.PaymentStatusDate = PaymentStatusDate;
    }

    public void setAmountToPay(String AmountToPay) {
        this.AmountToPay = AmountToPay;
    }

    public void setAmountPaid(String AmountPaid) {
        this.AmountPaid = AmountPaid;
    }

    public void setWarehouseRecipientRef(String WarehouseRecipientRef) {
        this.WarehouseRecipientRef = WarehouseRecipientRef;
    }

    public void setDocumentCost(float DocumentCost) {
        this.DocumentCost = DocumentCost;
    }

    public void setAnnouncedPrice(float AnnouncedPrice) {
        this.AnnouncedPrice = AnnouncedPrice;
    }

    public void setOwnerDocumentNumber(String OwnerDocumentNumber) {
        this.OwnerDocumentNumber = OwnerDocumentNumber;
    }

    public void setDateFirstDayStorage(String DateFirstDayStorage) {
        this.DateFirstDayStorage = DateFirstDayStorage;
    }

    public void setInternationalDeliveryType(String InternationalDeliveryType) {
        this.InternationalDeliveryType = InternationalDeliveryType;
    }

    public void setDaysStorageCargo(String DaysStorageCargo) {
        this.DaysStorageCargo = DaysStorageCargo;
    }

    public void setRecipientWarehouseTypeRef(String RecipientWarehouseTypeRef) {
        this.RecipientWarehouseTypeRef = RecipientWarehouseTypeRef;
    }

    public void setStorageAmount(String StorageAmount) {
        this.StorageAmount = StorageAmount;
    }

    public void setStoragePrice(String StoragePrice) {
        this.StoragePrice = StoragePrice;
    }

    public void setVolumeWeight(String VolumeWeight) {
        this.VolumeWeight = VolumeWeight;
    }

    public void setSeatsAmount(String SeatsAmount) {
        this.SeatsAmount = SeatsAmount;
    }

    public void setOwnerDocumentType(String OwnerDocumentType) {
        this.OwnerDocumentType = OwnerDocumentType;
    }

    public void setActualDeliveryDate(String ActualDeliveryDate) {
        this.ActualDeliveryDate = ActualDeliveryDate;
    }

    public void setDateReturnCargo(String DateReturnCargo) {
        this.DateReturnCargo = DateReturnCargo;
    }

    public void setRefCitySender(String RefCitySender) {
        this.RefCitySender = RefCitySender;
    }

    public void setRefCityRecipient(String RefCityRecipient) {
        this.RefCityRecipient = RefCityRecipient;
    }

    public void setCardMaskedNumber(String CardMaskedNumber) {
        this.CardMaskedNumber = CardMaskedNumber;
    }

    public void setBarcodeRedBox(String BarcodeRedBox) {
        this.BarcodeRedBox = BarcodeRedBox;
    }

    public void setAviaDelivery(float AviaDelivery) {
        this.AviaDelivery = AviaDelivery;
    }

    public void setOnlineCreditStatus(String OnlineCreditStatus) {
        this.OnlineCreditStatus = OnlineCreditStatus;
    }

    public void setFreeShipping(float FreeShipping) {
        this.FreeShipping = FreeShipping;
    }

    public void setAdjustedDate(String AdjustedDate) {
        this.AdjustedDate = AdjustedDate;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public void setStatusCode(String StatusCode) {
        this.StatusCode = StatusCode;
    }

    public void setRefEW(String RefEW) {
        this.RefEW = RefEW;
    }

    public void setDatePayedKeeping(String DatePayedKeeping) {
        this.DatePayedKeeping = DatePayedKeeping;
    }

    public void setOnlineCreditStatusCode(String OnlineCreditStatusCode) {
        this.OnlineCreditStatusCode = OnlineCreditStatusCode;
    }
}


   /* @JsonProperty("RecipientAddress")
    private String RecipientAddress;
    @JsonProperty("StatusCode")
    private int StatusCode;
    @JsonProperty("Status")
    private String Status;
    @JsonProperty("Redelivery")
    private Integer Redelivery;
    @JsonProperty("RedeliverySum")
    private Integer RedeliverySum;
    @JsonProperty("Number")
    private String Number;




    public String getRecipientAddress() {
        return RecipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        RecipientAddress = recipientAddress;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Integer getRedelivery() {
        return Redelivery;
    }

    public void setRedelivery(Integer redelivery) {
        Redelivery = redelivery;
    }

    public Integer getRedeliverySum() {
        return RedeliverySum;
    }

    public void setRedeliverySum(Integer redeliverySum) {
        RedeliverySum = redeliverySum;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }*/

