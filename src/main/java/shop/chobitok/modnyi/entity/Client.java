package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class Client extends Audit {

    @Column
    private String name;
    @Column
    private String lastName;
    @Column
    private String middleName;
    @Column
    private String phone;
    @Column
    private String comment;
    @Column
    private String mail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;

        Client client = (Client) o;

        if (!name.equals(client.name)) return false;
        if (!lastName.equals(client.lastName)) return false;
        return phone.equals(client.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastName, middleName, phone);
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", phone='" + phone + '\'' +
                ", comment='" + comment + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
