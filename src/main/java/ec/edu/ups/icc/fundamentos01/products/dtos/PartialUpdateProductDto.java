package ec.edu.ups.icc.fundamentos01.products.dtos;

public class PartialUpdateProductDto {

    private String name;
    private String description;
    private Double price;

    public PartialUpdateProductDto() {
    }

    public PartialUpdateProductDto(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}