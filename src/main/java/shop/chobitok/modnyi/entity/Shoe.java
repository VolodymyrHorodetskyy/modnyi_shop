package shop.chobitok.modnyi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Shoe extends Audit {

    @Column
    private String name;

    @Column
    private String model;

    @Column
    private String color;

    @Column
    private String description;

    @ManyToOne
    private Company company;

    @Column
    private Double cost;

    @Column
    private Double price;

    @Column
    private String photoPath;

    @Column
    private boolean available = true;

    @Column
    private boolean deleted;

    @Column
    private boolean imported;

    @ManyToMany
    private List<Ordered> ordereds;


    public Shoe() {
    }

    public Shoe(String name, String model, String description, Company company, Double cost, Double price, String photoPath) {
        this.name = name;
        this.model = model;
        this.description = description;
        this.company = company;
        this.cost = cost;
        this.price = price;
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Ordered> getOrdereds() {
        return ordereds;
    }

    public void setOrdereds(List<Ordered> ordereds) {
        this.ordereds = ordereds;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }
}
