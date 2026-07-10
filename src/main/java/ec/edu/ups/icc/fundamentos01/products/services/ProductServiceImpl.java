package ec.edu.ups.icc.fundamentos01.products.services;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;

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
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
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
     * Crea un producto usando como owner al usuario autenticado.
     */
    @Override
    @Transactional
    public ProductResponseDto create(
            CreateProductDto dto,
            UserDetailsImpl currentUser
    ) {

        UserEntity owner = findCurrentUserEntity(currentUser);

        Set<CategoryEntity> categories = findActiveCategories(dto.getCategoryIds());

        validateProductNameForCreate(dto.getName());

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
     * Actualiza completamente un producto.
     */
    @Override
    @Transactional
    public ProductResponseDto update(
            Long id,
            UpdateProductDto dto,
            UserDetailsImpl currentUser
    ) {
        ProductEntity entity = findActiveProductOrThrow(id);

        validateOwnership(entity, currentUser);

        validateProductNameForUpdate(entity.getId(), dto.getName());

        Set<CategoryEntity> categories = findActiveCategories(dto.getCategoryIds());

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);

        return ProductMapper.toResponse(model);
    }

    /*
     * Actualiza parcialmente un producto.
     */
    @Override
    @Transactional
    public ProductResponseDto partialUpdate(
            Long id,
            PartialUpdateProductDto dto,
            UserDetailsImpl currentUser
    ) {
        ProductEntity entity = findActiveProductOrThrow(id);

        validateOwnership(entity, currentUser);

        if (dto.getName() != null) {
            validateProductNameForUpdate(entity.getId(), dto.getName());
            entity.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }

        if (dto.getCategoryIds() != null) {
            entity.setCategories(findActiveCategories(dto.getCategoryIds()));
        }

        ProductEntity savedEntity = productRepository.save(entity);

        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);

        return ProductMapper.toResponse(model);
    }

    /*
     * Elimina lógicamente un producto.
     */
    @Override
    @Transactional
    public void delete(
            Long id,
            UserDetailsImpl currentUser
    ) {
        ProductEntity entity = findActiveProductOrThrow(id);

        validateOwnership(entity, currentUser);

        entity.setDeleted(true);
        productRepository.save(entity);
    }

    /*
     * Busca un producto activo.
     * Si no existe o está eliminado, devuelve 404.
     */
    private ProductEntity findActiveProductOrThrow(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    /*
     * Obtiene el usuario autenticado como entidad JPA.
     */
    private UserEntity findCurrentUserEntity(UserDetailsImpl currentUser) {

        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autorizado"));
    }

    /*
     * Valida si el usuario autenticado puede modificar o eliminar el producto.
     *
     * Reglas:
     * 1. ROLE_ADMIN puede modificar cualquier producto.
     * 2. ROLE_USER solo puede modificar productos propios.
     */
    private void validateOwnership(
            ProductEntity product,
            UserDetailsImpl currentUser
    ) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        if (hasRole(currentUser, "ROLE_ADMIN")) {
            return;
        }

        if (product.getOwner() == null || product.getOwner().getId() == null) {
            throw new AccessDeniedException("El producto no tiene propietario válido");
        }

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No puedes modificar productos ajenos");
        }
    }

    /*
     * Verifica si el usuario tiene un rol específico.
     */
    private boolean hasRole(
            UserDetailsImpl user,
            String role
    ) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    /*
     * Valida que el nombre no esté registrado al crear.
     */
    private void validateProductNameForCreate(String name) {

        if (productRepository.findByNameIgnoreCaseAndDeletedFalse(name.trim()).isPresent()) {
            throw new ConflictException("Product name already registered");
        }
    }

    /*
     * Valida que el nombre no esté registrado en otro producto al actualizar.
     * Se permite que el producto conserve su mismo nombre.
     */
    private void validateProductNameForUpdate(
            Long currentProductId,
            String name
    ) {
        productRepository.findByNameIgnoreCaseAndDeletedFalse(name.trim())
                .filter(product -> !product.getId().equals(currentProductId))
                .ifPresent(product -> {
                    throw new ConflictException("Product name already registered");
                });
    }

    /*
     * Busca categorías activas por ids.
     * Si una categoría no existe o está eliminada, se rechaza la operación.
     */
    private Set<CategoryEntity> findActiveCategories(Set<Long> categoryIds) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos una categoría");
        }

        Set<Long> uniqueIds = new HashSet<>(categoryIds);

        Set<CategoryEntity> categories = categoryRepository.findAllById(uniqueIds)
                .stream()
                .filter(category -> !category.isDeleted())
                .collect(Collectors.toSet());

        if (categories.size() != uniqueIds.size()) {
            throw new NotFoundException("One or more categories were not found");
        }

        return categories;
    }

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

    private String normalizeName(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return name.trim();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination) {

        Pageable pageable = createPageable(pagination);

        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }

    /*
    * Retorna productos activos del usuario autenticado usando Slice.
    *
    * No incluye totalElements ni totalPages.
    * Es más liviano para navegación secuencial.
    */
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(
            PaginationDto pagination,
            UserDetailsImpl currentUser
    ) {
        UserEntity owner = findCurrentUserEntity(currentUser);

        Pageable pageable = createPageable(pagination);

        return productRepository.findActiveSliceByOwnerId(owner.getId(), pageable)
                .map(ProductMapper::toResponseFromEntity);
    }

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