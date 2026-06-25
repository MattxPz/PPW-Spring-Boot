package ec.edu.ups.icc.fundamentos01.products.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByName(String name);

    Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

    Optional<ProductEntity> findByIdAndDeleted(Long id, boolean deleted);

    Optional<ProductEntity> findByIdAndPrice(Long id, double price);


}