package ec.edu.ups.icc.fundamentos01.products.services;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Sort;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByUserDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;



    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /*
    * Retorna los productos activos creados por un usuario.
    *
    * Primero valida que el usuario exista y no esté eliminado.
    */
    @Override
    public List<ProductResponseDto> findByUserId(Long userId) {
            if (!userRepository.existsByIdAndDeletedFalse(userId)) {
                throw new NotFoundException("User not found");
            }

                List<ProductEntity> list = productRepository.findByOwner_IdAndDeletedFalse(userId);

            return list
                    .stream()
                    .map(ProductMapper::toModelFromEntity)
                    .map(ProductMapper::toResponse)
                    .toList();
    }

        /*
    * Retorna los productos activos asociados a una categoría.
    *
    * Primero valida que la categoría exista y no esté eliminada.
    */
    @Override
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {

        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        return productRepository.findByCategories_IdAndDeletedFalse(categoryId)
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }


    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted()) 
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDto findOne(Long id) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        ProductModel model = ProductMapper.toModelFromEntity(entity);

        return ProductMapper.toResponse(model);
    }

    /*
    * Crea un producto asociado a un usuario y a una categoría.
    *
    * Valida:
    * - que el usuario exista
    * - que la categoría exista
    * - que no exista un producto activo con el mismo nombre
    */
    @Override
    public ProductResponseDto create(CreateProductDto dto) {

            // 1 Encontramos el user
        UserEntity owner = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (owner.isDeleted()) {
            throw new NotFoundException("User not found");
        }

            // 2 Encontramos las categorias
        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

    // validadacion de negocio, por ejemplo que no exista un producto  con el mismo nombre
        if (productRepository.findByNameIgnoreCaseAndDeletedFalse(dto.getName()).isPresent()) {
            throw new ConflictException("Product name already registered");
        }


            // Genereamos la entidad a partir del DTO

        ProductEntity entity = new ProductEntity();

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setOwner(owner);
        entity.setCategories(categories);

    ProductEntity savedEntity = productRepository.save(entity);

            ProductModel savedModel = ProductMapper.toModelFromEntity(savedEntity);

            return ProductMapper.toResponse(savedModel);
    }

    /*
    * Actualiza completamente un producto activo.
    *
    * No permite cambiar el usuario propietario.
    * Sí permite cambiar la categoría.
    */
    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setCategories(categories);

            ProductEntity savedEntity = productRepository.save(entity);

            ProductModel model = ProductMapper.toModelFromEntity(savedEntity);

            return ProductMapper.toResponse(model);
    }

    /*
    * Actualiza parcialmente un producto activo.
    *
    * Solo modifica los campos enviados en el DTO.
    */
    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }

        if (dto.getCategoryIds() != null) {
            entity.setCategories(validateAndGetCategories(dto.getCategoryIds()));
        }

        ProductEntity savedEntity = productRepository.save(entity);

            ProductModel model = ProductMapper.toModelFromEntity(savedEntity);

            return ProductMapper.toResponse(model);
    }

    @Override
    public void delete(Long id) {
        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
                
        entity.setDeleted(true);
        productRepository.save(entity);
    }

    /*
    * Retorna productos activos de un usuario aplicando filtros opcionales.
    *
    * Primero valida que el usuario exista y no esté eliminado.
    * Luego valida el rango de precios.
    * Finalmente consulta los productos desde ProductRepository.
    */
    @Override
    public List<ProductResponseDto> findByUserIdWithFilters(
            Long userId,
            ProductFilterByUserDto filters
    ) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        validateUserFilters(filters);

        String name = normalizeName(filters.getName());

        return productRepository.findByOwnerIdWithFilters(
                        userId,
                        name,
                        filters.getMinPrice(),
                        filters.getMaxPrice(),
                        filters.getCategoryId()
                )
                .stream()
            .map(ProductMapper::toModelFromEntity)
                    .map(ProductMapper::toResponse)
                .toList();
    }


    /*
    * Retorna productos activos de una categoría aplicando filtros opcionales.
    *
    * Primero valida que la categoría exista y no esté eliminada.
    * Luego valida el rango de precios.
    * Si viene userId como filtro, valida que el usuario exista.
    * Finalmente consulta los productos desde ProductRepository.
    */
    @Override
    public List<ProductResponseDto> findByCategoryIdWithFilters(
            Long categoryId,
            ProductFilterByCategoryDto filters
    ) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);

        String name = normalizeName(filters.getName());

        return productRepository.findByCategoryIdWithFilters(
                        categoryId,
                        name,
                        filters.getMinPrice(),
                        filters.getMaxPrice(),
                        filters.getUserId()
                )
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    private void validateCategoryFilters(ProductFilterByCategoryDto filters) {
        if (filters == null) {
            return;
        }

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }

        if (filters.getUserId() != null &&
                !userRepository.existsByIdAndDeletedFalse(filters.getUserId())) {
            throw new NotFoundException("User not found");
        }
    }


    /*
    * Valida que todas las categorías existan y estén activas.
    *
    * Retorna el conjunto de entidades CategoryEntity
    * que se asociarán al producto.
    */
    private Set<CategoryEntity> validateAndGetCategories(Set<Long> categoryIds) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos una categoría");
        }

        Set<CategoryEntity> categories = new HashSet<>();

        for (Long categoryId : categoryIds) {
            CategoryEntity category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.isDeleted()) {
                throw new NotFoundException("Category not found");
            }

            categories.add(category);
        }

        return categories;
    }


    /*
    * Valida reglas de negocio relacionadas con filtros.
    */
    private void validateUserFilters(ProductFilterByUserDto filters) {

        if (filters == null) {
            return;
        }

        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }

        if (filters.getCategoryId() != null &&
                !categoryRepository.existsByIdAndDeletedFalse(filters.getCategoryId())) {
            throw new NotFoundException("Category not found");
        }


    }

    /*
    * Convierte un texto vacío en null.
    *
    * Esto permite que el repositorio ignore el filtro por nombre
    * cuando el query param llega vacío.
    */
    private String normalizeName(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return name.trim();
    }

    /*
    * Retorna productos activos usando Page.
    *
    * Incluye metadatos completos:
    * totalElements, totalPages, number, size, first, last.
    */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination) {

        Pageable pageable = createPageable(pagination);

        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }

    /*
    * Retorna productos activos usando Slice.
    *
    * No incluye totalElements ni totalPages.
    * Es más liviano para navegación secuencial.
    */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(PaginationDto pagination) {

        Pageable pageable = createPageable(pagination);

        return productRepository.findActiveSlice(pageable)
            .map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }

    /*
    * Construye el objeto Pageable validando:
    * página, tamaño, campo de ordenamiento y dirección.
    */
    private Pageable createPageable(PaginationDto pagination) {

        String sortBy = normalizeSortBy(pagination.getSortBy());

        Sort.Direction direction = normalizeDirection(pagination.getDirection());

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );
    }

    /*
    * Valida que el campo de ordenamiento exista y esté permitido.
    *
    * Se usa lista blanca para evitar ordenar por campos inexistentes
    * o por relaciones complejas no preparadas para esta práctica.
    */
    private String normalizeSortBy(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }

        Set<String> allowedFields = Set.of(
                "id",
                "name",
                "price",
                "stock",
                "createdAt",
                "updatedAt"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException("Campo de ordenamiento no permitido: " + sortBy);
        }

        return sortBy;
    }

    /*
    * Convierte la dirección recibida por query param
    * en Sort.Direction.
    */
    private Sort.Direction normalizeDirection(String direction) {

        if (direction == null || direction.isBlank()) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }

        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        throw new BadRequestException("Dirección de ordenamiento no válida: " + direction);
    }

    /*
     * Retorna productos activos de una categoría usando Page.
     * Mantiene los filtros de la práctica anterior y agrega paginación.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByCategoryIdWithFiltersPage(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination
    ) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);

        String name = normalizeName(filters.getName());
        Pageable pageable = createPageable(pagination);

        BigDecimal minPrice = filters.getMinPrice() != null ? BigDecimal.valueOf(filters.getMinPrice()) : null;
        BigDecimal maxPrice = filters.getMaxPrice() != null ? BigDecimal.valueOf(filters.getMaxPrice()) : null;

        return productRepository.findByCategoryIdWithFiltersPage(
                categoryId,
                name,
                minPrice,
                maxPrice,
                pageable
        ).map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }

    /*
     * Retorna productos activos de una categoría usando Slice.
     * No calcula totalElements ni totalPages.
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findByCategoryIdWithFiltersSlice(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination
    ) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);

        String name = normalizeName(filters.getName());
        Pageable pageable = createPageable(pagination);

        BigDecimal minPrice = filters.getMinPrice() != null ? BigDecimal.valueOf(filters.getMinPrice()) : null;
        BigDecimal maxPrice = filters.getMaxPrice() != null ? BigDecimal.valueOf(filters.getMaxPrice()) : null;

        return productRepository.findByCategoryIdWithFiltersSlice(
                categoryId,
                name,
                minPrice,
                maxPrice,
                pageable
        ).map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }


}