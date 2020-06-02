package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Shoe extends Audit {

    @Column(unique = true)
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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> patterns;


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

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shoe)) return false;
        Shoe shoe = (Shoe) o;
        return Objects.equals(name, shoe.name) &&
                model.equals(shoe.model) &&
                color.equals(shoe.color) &&
                Objects.equals(description, shoe.description) &&
                Objects.equals(cost, shoe.cost) &&
                Objects.equals(price, shoe.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, model, color, description, cost, price);
    }
}
