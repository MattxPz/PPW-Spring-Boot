package ec.edu.ups.icc.fundamentos01.products.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ec.edu.ups.icc.fundamentos01.categories.mappers.CategoryMapper;
import ec.edu.ups.icc.fundamentos01.categories.models.CategoryModel;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

public class ProductModel {

    private Long id;
    private String name;
    private Integer stock;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    private UserModel owner;
    
    private List<CategoryModel> categories;

    public ProductModel() {
    }

    public ProductModel(Long id, String name, Integer stock, Double price, LocalDateTime createdAt,
            LocalDateTime updatedAt, boolean deleted, UserModel owner, List<CategoryModel> categories) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.owner = owner;
        this.categories = categories;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
    }

    public static ProductModel fromDto(CreateProductDto dto) {
        ProductModel model = new ProductModel();
        model.setName(dto.getName());
        model.setPrice(dto.getPrice());
        model.setStock(dto.getStock());
        return model;
    }

    public static ProductModel fromEntity(ProductEntity entity) {
        return new ProductModel(
            entity.getId(),
            entity.getName(),
            entity.getStock(),
            entity.getPrice(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.isDeleted(),
            entity.getOwner() != null ? UserModel.fromEntity(entity.getOwner()) : null,
            entity.getCategories() != null ? entity.getCategories().stream().map(CategoryModel::fromEntity).collect(Collectors.toList()) : null
        );
    }

    public ProductEntity toEntity() {
        ProductEntity entity = new ProductEntity();
        if (this.id != null) {
            entity.setId(this.id);
        }
        entity.setName(this.name);
        entity.setPrice(this.price);
        entity.setStock(this.stock);
        
        if (this.createdAt != null) entity.setCreatedAt(this.createdAt);
        if (this.updatedAt != null) entity.setUpdatedAt(this.updatedAt);
        entity.setDeleted(this.deleted);
        
        return entity;
    }

    public ProductResponseDto toResponseDto() {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setPrice(this.price);
        dto.setStock(this.stock);

        if (this.owner != null) {
            dto.setOwner(UserMapper.toResponse(this.owner));
        }

        if (this.categories != null) {
            dto.setCategories(this.categories.stream().map(CategoryMapper::toResponse).collect(Collectors.toList()));
        }

        return dto;
    }

}