package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        if (productRepository.findByName(dto.getName()).isPresent()) {
            throw new ConflictException("Product already registered");
        }

        ProductModel model = ProductMapper.toModelFromDTO(dto);
        ProductEntity entity = ProductMapper.toEntityFromModel(model);

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel savedModel = ProductMapper.toModelFromEntity(savedEntity);

        return ProductMapper.toResponse(savedModel);
    }

    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        entity.setName(dto.getName());
        entity.setStock(dto.getStock());
        entity.setPrice(dto.getPrice());

        ProductEntity savedEntity = productRepository.save(entity);

        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);

        return ProductMapper.toResponse(model);
    }

    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
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
}