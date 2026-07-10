package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import ec.edu.ups.icc.fundamentos01.categories.mappers.CategoryMapper;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

public class ProductMapper {

    public static ProductModel toModelFromDTO(CreateProductDto dto) {
        ProductModel model = new ProductModel();
        model.setName(dto.getName());
        model.setStock(dto.getStock());
        model.setPrice(dto.getPrice());
        model.setCreatedAt(LocalDateTime.now());

        return model;
    }

    public static ProductModel toModelFromEntity(ProductEntity entity) {
        ProductModel model = new ProductModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setStock(entity.getStock());
        model.setPrice(entity.getPrice());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());

        if (entity.getOwner() != null) {
            model.setOwner(UserMapper.toModelFromEntity(entity.getOwner()));
        }

        if (entity.getCategories() != null) {
            model.setCategories(entity.getCategories().stream().map(CategoryMapper::toModelFromEntity).collect(Collectors.toList()));
        }

        return model;
    }

    public static ProductEntity toEntityFromModel(ProductModel model) {
        ProductEntity entity = new ProductEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setStock(model.getStock());
        entity.setPrice(model.getPrice());

        return entity;
    }

    public static ProductResponseDto toResponse(ProductModel model) {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(model.getId());
        response.setName(model.getName());
        response.setStock(model.getStock());
        response.setPrice(model.getPrice());

        if (model.getOwner() != null) {
            response.setOwner(UserMapper.toResponse(model.getOwner()));
        }

        if (model.getCategories() != null) {
            response.setCategories(model.getCategories().stream().map(CategoryMapper::toResponse).collect(Collectors.toList()));
        }

        response.setCreatedAt(model.getCreatedAt());
        response.setUpdatedAt(model.getUpdatedAt());
        return response;
    }

    /*
     * Método de conveniencia: convierte directamente de ProductEntity
     * a ProductResponseDto, sin exponer el paso intermedio por ProductModel.
     *
     * Internamente reutiliza toModelFromEntity + toResponse,
     * así que no duplica lógica de mapeo, solo evita repetir
     * las dos líneas en cada método del service.
     */
    public static ProductResponseDto toResponseFromEntity(ProductEntity entity) {
        return toResponse(toModelFromEntity(entity));
    }
}