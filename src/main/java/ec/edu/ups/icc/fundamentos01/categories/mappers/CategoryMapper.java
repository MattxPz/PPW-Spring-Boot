package ec.edu.ups.icc.fundamentos01.categories.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.models.CategoryModel;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;

/*
 * Clase encargada de convertir objetos entre DTOs, modelos y entidades
 * para el recurso categories.
 */
public class CategoryMapper {

    /*
     * Convierte un CreateCategoryDto en CategoryModel.
     *
     * El DTO contiene los datos recibidos desde la API.
     * El modelo representa la categoría dentro de la lógica de la aplicación.
     */
    public static CategoryModel toModelFromDTO(CreateCategoryDto dto) {
        CategoryModel model = new CategoryModel();

        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setCreatedAt(LocalDateTime.now());

        return model;
    }

    /*
     * Convierte una entidad JPA en CategoryModel.
     *
     * Se usa cuando el repositorio devuelve datos desde PostgreSQL.
     */
    public static CategoryModel toModelFromEntity(CategoryEntity entity) {
        CategoryModel model = new CategoryModel();

        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());

        return model;
    }

    /*
     * Convierte un CategoryModel en CategoryEntity.
     *
     * Se usa antes de guardar datos en la base de datos.
     */
    public static CategoryEntity toEntityFromModel(CategoryModel model) {
        CategoryEntity entity = new CategoryEntity();

        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());

        return entity;
    }

    /*
     * Convierte un CategoryModel en CategoryResponseDto.
     *
     * Prepara los datos para ser expuestos en la respuesta de la API.
     */
    public static CategoryResponseDto toResponse(CategoryModel model) {
        CategoryResponseDto response = new CategoryResponseDto();

        response.setId(model.getId());
        response.setName(model.getName());
        response.setDescription(model.getDescription());

        return response;
    }
}