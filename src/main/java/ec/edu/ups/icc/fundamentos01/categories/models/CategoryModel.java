package ec.edu.ups.icc.fundamentos01.categories.models;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;

/*
 * Modelo de dominio del recurso categories.
 *
 * Representa la categoría dentro de la lógica de negocio.
 * No es una entidad de base de datos y no debe tener anotaciones JPA.
 */
public class CategoryModel {

    private Long id;
    
    private String name;
    
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean deleted;

    public CategoryModel() {
    }

    public CategoryModel(Long id, String name, String description, LocalDateTime createdAt,
            LocalDateTime updatedAt, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    // Métodos de mapeo internos de la clase

    public static CategoryModel fromDto(CreateCategoryDto dto) {
        CategoryModel model = new CategoryModel();
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        return model;
    }

    public static CategoryModel fromEntity(CategoryEntity entity) {
        return new CategoryModel(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.isDeleted()
        );
    }

    public CategoryEntity toEntity() {
        CategoryEntity entity = new CategoryEntity();
        
        if (this.id != null) {
            entity.setId(this.id);
        }
        
        entity.setName(this.name);
        entity.setDescription(this.description);
        
        // Mapeo de campos de auditoría heredados
        if (this.createdAt != null) entity.setCreatedAt(this.createdAt);
        if (this.updatedAt != null) entity.setUpdatedAt(this.updatedAt);
        entity.setDeleted(this.deleted);
        
        return entity;
    }
}