package ec.edu.ups.icc.fundamentos01.users.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByUserDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.dtos.CurrentUserResponseDto;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final ProductService productService;

    private final UserService service;

    public UsersController(UserService service, ProductService productService) {
        this.service = service;
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo usuarios con rol ADMIN pueden acceder
    public List<UserResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto findOne(@PathVariable Long id) { 
        return service.findOne(id);
    }

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody CreateUserDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public UserResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto dto 
    ) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateUserDto dto 
    ) {
        return service.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id); 
    }

    @GetMapping("/{id}/products")
    public List<ProductResponseDto> findProductsByUser(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductFilterByUserDto filters
    ) {
        return productService.findByUserIdWithFilters(id, filters);
    }

    /*
     * Retorna los datos del usuario autenticado.
     *
     * @AuthenticationPrincipal obtiene el usuario que fue colocado
     * en el SecurityContext por JwtAuthenticationFilter.
     */
    @GetMapping("/me")
    public CurrentUserResponseDto me(
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        Set<String> roles = currentUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new CurrentUserResponseDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                roles
        );
    }
}