package shop.chobitok.modnyi.novaposta.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MethodPropertiesForReturn {

        private String IntDocNumber;
        private String PaymentMethod = "Cash";
        private String Reason = "49754eb2-a9e1-11e3-9fa0-0050568002cf";
        private String SubtypeReason = "49754ec8-a9e1-11e3-9fa0-0050568002cf";
        private String Note = "";
        private String OrderType = "orderCargoReturn";
        private String ReturnAddressRef = "dbf47cd9-638e-11ea-8513-b88303659df5";

        public String getIntDocNumber() {
            return IntDocNumber;
        }

        public String getPaymentMethod() {
            return PaymentMethod;
        }

        public String getReason() {
            return Reason;
        }

        public String getSubtypeReason() {
            return SubtypeReason;
        }

        public String getNote() {
            return Note;
        }

        public String getOrderType() {
            return OrderType;
        }

        public String getReturnAddressRef() {
            return ReturnAddressRef;
        }

        public void setIntDocNumber(String IntDocNumber) {
            this.IntDocNumber = IntDocNumber;
        }

        public void setPaymentMethod(String PaymentMethod) {
            this.PaymentMethod = PaymentMethod;
        }

        public void setReason(String Reason) {
            this.Reason = Reason;
        }

        public void setSubtypeReason(String SubtypeReason) {
            this.SubtypeReason = SubtypeReason;
        }

        public void setNote(String Note) {
            this.Note = Note;
        }

        public void setOrderType(String OrderType) {
            this.OrderType = OrderType;
        }

        public void setReturnAddressRef(String ReturnAddressRef) {
            this.ReturnAddressRef = ReturnAddressRef;
        }

}
