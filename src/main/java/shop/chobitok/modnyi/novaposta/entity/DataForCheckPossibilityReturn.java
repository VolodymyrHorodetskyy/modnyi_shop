package shop.chobitok.modnyi.novaposta.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataForCheckPossibilityReturn {

        private boolean NonCash;
        private String City;
        private String Counterparty;
        private String ContactPerson;
        private String Address;
        private String Phone;
        private String Ref;


        // Getter Methods

        public boolean getNonCash() {
            return NonCash;
        }

        public String getCity() {
            return City;
        }

        public String getCounterparty() {
            return Counterparty;
        }

        public String getContactPerson() {
            return ContactPerson;
        }

        public String getAddress() {
            return Address;
        }

        public String getPhone() {
            return Phone;
        }

        public String getRef() {
            return Ref;
        }

        // Setter Methods

        public void setNonCash(boolean NonCash) {
            this.NonCash = NonCash;
        }

        public void setCity(String City) {
            this.City = City;
        }

        public void setCounterparty(String Counterparty) {
            this.Counterparty = Counterparty;
        }

        public void setContactPerson(String ContactPerson) {
            this.ContactPerson = ContactPerson;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public void setPhone(String Phone) {
            this.Phone = Phone;
        }

        public void setRef(String Ref) {
            this.Ref = Ref;
        }


}
