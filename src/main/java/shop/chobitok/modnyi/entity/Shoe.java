    package shop.chobitok.modnyi.entity;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

    import javax.persistence.*;
    import java.util.List;

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
        private String photoPath;

        @Column
        private boolean available = true;

        @Column
        private boolean deleted;

        @Column
        private boolean imported;
        @OneToOne
        private Variants shoeType;

        @ElementCollection(fetch = FetchType.EAGER)
        private List<String> patterns;

        public Shoe() {
        }

        public Shoe(Shoe shoe) {
            setId(shoe.getId());
            setCreatedDate(shoe.getCreatedDate());
            setLastModifiedDate(shoe.getLastModifiedDate());
            this.name = shoe.getName();
            this.model = shoe.getModel();
            this.color = shoe.getColor();
            this.description = shoe.getDescription();
            this.company = shoe.getCompany();
            this.photoPath = shoe.getPhotoPath();
            this.available = shoe.isAvailable();
            this.deleted = shoe.isDeleted();
            this.imported = shoe.isImported();
            this.patterns = shoe.getPatterns();
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

        public Variants getShoeType() {
            return shoeType;
        }

        public void setShoeType(Variants shoeType) {
            this.shoeType = shoeType;
        }

        @Override
        public boolean equals(Object o) {


            Shoe shoe = (Shoe) o;

            if (!model.equals(shoe.model)) return false;
            return color.equals(shoe.color);
        }

        @Override
        public int hashCode() {
            int result = model.hashCode();
            result = 31 * result + color.hashCode();
            return result;
        }

        public String getModelAndColor() {
            return this.model + " " + this.color;
        }
    }
