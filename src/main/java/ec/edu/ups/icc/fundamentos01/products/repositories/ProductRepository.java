package ec.edu.ups.icc.fundamentos01.products.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByNameIgnoreCaseAndDeletedFalse(String name);

    List<ProductEntity> findByDeletedFalse();

    Optional<ProductEntity> findByName(String name);

    Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

    Optional<ProductEntity> findByIdAndDeleted(Long id, boolean deleted);

    Optional<ProductEntity> findByIdAndPrice(Long id, double price);

    List<ProductEntity> findByOwner_IdAndDeletedFalse(Long ownerId);

    List<ProductEntity> findByCategory_IdAndDeletedFalse(Long categoryId);

    List<ProductEntity> findByCategory_NameIgnoreCaseAndDeletedFalse(String categoryName);

    
    /*
     * Busca productos activos de un usuario aplicando filtros opcionales.
     *
     * Si un filtro llega como null, no se aplica.
     */
    @Query("""
            SELECT p
            FROM ProductEntity p
            WHERE p.deleted = false
              AND p.owner.id = :userId
              AND p.owner.deleted = false
              AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:categoryId IS NULL OR p.category.deleted = false)
            """)
    List<ProductEntity> findByOwnerIdWithFilters(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Long categoryId
    );

    /*
     * Busca productos activos de una categoría aplicando filtros opcionales.
     *
     * Si un filtro llega como null, no se aplica.
     */
    @Query("""
            SELECT p
            FROM ProductEntity p
            WHERE p.deleted = false
              AND p.category.id = :categoryId
              AND p.category.deleted = false
              AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:userId IS NULL OR p.owner.id = :userId)
              AND (:userId IS NULL OR p.owner.deleted = false)
            """)
    List<ProductEntity> findByCategoryIdWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("userId") Long userId
    );
}