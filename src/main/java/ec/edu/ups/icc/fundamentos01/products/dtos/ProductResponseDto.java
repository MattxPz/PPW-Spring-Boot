package ec.edu.ups.icc.fundamentos01.products.dtos;

public class ProductResponseDto {

    private Long id;
    private String name;
    private Integer stock;
    private Double price;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, Integer stock, Double price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}