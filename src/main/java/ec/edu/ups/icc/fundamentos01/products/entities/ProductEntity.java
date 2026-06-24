package ec.edu.ups.icc.fundamentos01.products.entities;

import jakarta.persistence.Table;
import ec.edu.ups.icc.fundamentos01.core.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Double price;

    public ProductEntity() {
    }

    public ProductEntity(String name, Integer stock, Double price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
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