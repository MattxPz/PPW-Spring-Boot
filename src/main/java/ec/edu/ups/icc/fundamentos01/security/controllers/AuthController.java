package ec.edu.ups.icc.fundamentos01.security.controllers;

// imports packages y clases....

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Autenticación",
    description = "Gestión de autenticación y registro de usuarios"
)
@RestController
@RequestMapping("/auth") // Prefijo para todos los endpoints de autenticación
public class AuthController {

    private final AuthService authService; // Servicio de lógica de autenticación

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Iniciar sesión",
        description = """
                Autentica a un usuario en el sistema utilizando su email y contraseña.
                
                Este es un endpoint público (configurado en SecurityConfig).
                
                El cuerpo de la petición debe contener un objeto LoginRequestDto válido
                (email y password requeridos).
                
                Si las credenciales son correctas, retorna un 200 OK con el token JWT 
                y el token de refresco.
                """
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        // @Valid valida anotaciones en LoginRequestDto (email, password requeridos)
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response); // 200 OK con JWT
    }

    @Operation(
        summary = "Registrar un nuevo usuario",
        description = """
                Crea una nueva cuenta de usuario en el sistema.
                
                Este es un endpoint público (configurado en SecurityConfig).
                
                El cuerpo de la petición debe contener un objeto RegisterRequestDto con
                los datos válidos del usuario.
                
                Retorna un 201 Created junto con los tokens de autenticación para 
                iniciar sesión automáticamente tras el registro.
                """
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        // @Valid valida anotaciones en RegisterRequestDto
        AuthResponseDto response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created con JWT
    }

    @Operation(
        summary = "Refrescar token JWT",
        description = """
                Genera un nuevo token de acceso utilizando un refresh token válido.
                
                Este es un endpoint público (configurado en SecurityConfig).
                
                El cuerpo de la petición debe contener un objeto RefreshTokenRequestDto
                que incluya el token de refresco actual.
                
                Retorna un 200 OK con un nuevo conjunto de tokens de autenticación.
                """
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Cerrar sesión (Logout)",
        description = """
                Invalida el token de refresco del usuario para cerrar su sesión de forma segura.
                
                Este es un endpoint público (configurado en SecurityConfig).
                
                El cuerpo de la petición debe contener el RefreshTokenRequestDto que se 
                desea invalidar en la base de datos o sistema de caché.
                
                Si la operación es exitosa, retorna un 204 No Content sin cuerpo de respuesta.
                """
    )
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        authService.logout(request);
    }
}