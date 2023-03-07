package shop.chobitok.modnyi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company extends Audit {

    private String name;
    private Boolean useStorage;
    private Boolean allShouldBePayed;
    private Boolean markOSShouldNotBePayedIfUsedInCoincidence;

    public Company(String name) {
        this.name = name;
    }

    public Company() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUseStorage() {
        return useStorage;
    }

    public void setUseStorage(Boolean useStorage) {
        this.useStorage = useStorage;
    }

    public Boolean getAllShouldBePayed() {
        return allShouldBePayed;
    }

    public void setAllShouldBePayed(Boolean allShouldBePayed) {
        this.allShouldBePayed = allShouldBePayed;
    }

    public Boolean getMarkOSShouldNotBePayedIfUsedInCoincidence() {
        return markOSShouldNotBePayedIfUsedInCoincidence;
    }

    public void setMarkOSShouldNotBePayedIfUsedInCoincidence(Boolean markOSShouldNotBePayedIfUsedInCoincidence) {
        this.markOSShouldNotBePayedIfUsedInCoincidence = markOSShouldNotBePayedIfUsedInCoincidence;
    }
}
