package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para crear un producto")
public class CreateProductDto {

    @Schema(
            description = "Nombre del producto",
            example = "Laptop Dell"
    )
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @Schema(
            description = "Stock del producto",
            example = "10"
    )
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser un número positivo o cero")
    private Integer stock;

    @Schema(
            description = "Precio del producto",
            example = "1000.00"
    )
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser un número positivo o cero")
    private Double price;

    @Schema(
            description = "Categorías del producto",
            example = "[1, 2, 3]"
    )
    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private Set<Long> categoryIds;

    public CreateProductDto() {
    }

    

    public CreateProductDto(String name, Integer stock, Double price, Set<Long> categoryIds) {
        this.name = name;
        this.stock = stock;
        this.price = price;
        //this.userId = userId;
        this.categoryIds = categoryIds;
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

    // public Long getUserId() {
    //     return userId;
    // }

    // public void setUserId(Long userId) {
    //     this.userId = userId;
    // }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}