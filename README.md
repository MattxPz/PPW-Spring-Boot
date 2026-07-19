# Proyecto Spring Boot

## Autor
- Nombre: Mateo Paez
- Materia: Programación y Plataformas Web

## Descripcion
Este proyecto consiste en un proyecto de SpringBoot de varias practicas de desarrollo, desde la creacion de ENDPOINTS hasta el despliegue de la aplicacion en varias aplicaciones como Render, Ubuntu Server, NGINX.

## Estructura implementada

### Modelo: Student.java

La clase `Student` representa la entidad estudiante y contiene los siguientes atributos:

- `id`: Identificador del estudiante.
- `name`: Nombre del estudiante.
- `age`: Edad del estudiante.

Además, incluye:
- Constructor parametrizado.
- Métodos `get` y `set` para cada atributo.

### Controlador: StudentController.java

El controlador `StudentController` expone los servicios REST bajo la ruta base:

```
/api/students
```

Al iniciar la aplicación se crean dos estudiantes de ejemplo:

| ID | Nombre | Edad |
|----|---------|------|
| 1 | Juan | 30 |
| 2 | Diego | 10 |

## Endpoints disponibles

### Obtener todos los estudiantes

```
GET /api/students
```

Respuesta esperada:

```json
[
  {
    "id": 1,
    "name": "Juan",
    "age": 30
  },
  {
    "id": 2,
    "name": "Diego",
    "age": 10
  }
]
```

### Obtener la cantidad de estudiantes

```
GET /api/students/count
```

Respuesta esperada:

```
Total de estudiantes: 2
```

## Evidencia de ejecucion

### Servidor Spring Boot en ejecucion

### Endpoint `/api/students`


![Endpoint students](docs/images/2.1.png)


## Tecnologias utilizadas

- Java
- Spring Boot
- Spring Web
- Maven

## EndPoints a analizar

```
GET /api/students
GET /api/students/count
```

---

## Resolucion de Practicas

### Practica 01 - 02

Para iniciar el proyecto, se verifica que Java esté instalado y ejecuta la aplicación con Gradle. Una vez iniciada, el servidor quedará disponible en `http://localhost:8080` y podrás comprobar su funcionamiento accediendo al endpoint `/api/status`.

```bash
java -version
./gradlew bootRun
curl http://localhost:8080/api/status
```

![springboot](docs/images/1.1.png)

![api-students](docs/images/2.1.png)

---

### Practica 03

- Descripción

Este proyecto implementa una API REST básica en Spring Boot para gestionar usuarios mediante operaciones CRUD, utilizando controladores, DTOs, modelos y mappers para separar responsabilidades y mantener una arquitectura organizada. Durante esta práctica no se emplea una base de datos, por lo que la información se almacena temporalmente en memoria mediante una lista de objetos.

- Añadidos

Durante el desarrollo de la actividad, se recplico la creacion de los usuarios para la creacion de Productos, con las variables de nombre, stock y precio, ejecutando los endpoints correspondientes, en las capturas se evidencian los endpoints de `api/users` y la de `api/products`

![api-users](docs/images/3.1.png)

![api-products](docs/images/3.2.png)

---

### Practica 04

- Descripcion

Esta práctica implementa una arquitectura más organizada para una API REST en Spring Boot mediante la incorporación de la capa de servicios y la inyección de dependencias. La lógica de negocio se traslada desde los controladores hacia clases anotadas con `@Service`, permitiendo que los controladores se limiten a recibir solicitudes HTTP y delegar las operaciones correspondientes. Además, se mantiene el uso de DTOs, modelos y mappers para separar responsabilidades y facilitar la mantenibilidad del código, utilizando almacenamiento temporal en memoria sin recurrir todavía a una base de datos.

- Pruebas en BRUNO

A continuación se incluyen las evidencias de las pruebas realizadas con Bruno para verificar el funcionamiento de los endpoints de la API REST. Las capturas muestran la ejecución de operaciones como creación, consulta, actualización y eliminación de recursos, comprobando que el controlador delega correctamente la lógica al servicio y que las respuestas se generan de forma adecuada.

##### -- Productos

![Get Products](docs/images/4.1.png)

![Post Products](docs/images/4.2.png)

##### -- Users

![Get Products](docs/images/4.3.png)

![Post Products](docs/images/4.4.png)

---

### Practica 05

- Descripcion

Esta práctica incorpora persistencia real de datos en una aplicación Spring Boot mediante PostgreSQL y Spring Data JPA. Se reemplaza el almacenamiento temporal en memoria por entidades JPA y repositorios, permitiendo que las operaciones CRUD se ejecuten directamente sobre una base de datos. Además, se mantiene una separación clara entre DTOs, modelos, entidades y servicios, utilizando mappers para transformar la información entre cada capa y una clase BaseEntity para centralizar atributos comunes de persistencia.

![Docker Products](docs/images/5.1.png)

---

### Practica 06

- Descripcion

Esta práctica incorpora validación de datos de entrada en una API REST desarrollada con Spring Boot mediante Jakarta Validation. Se añaden restricciones sobre los DTOs para verificar que la información recibida cumpla requisitos como campos obligatorios, formatos válidos y valores mínimos antes de ejecutar la lógica de negocio o persistir los datos en la base de datos. Asimismo, se complementan estas verificaciones con reglas implementadas en los servicios y restricciones definidas en las entidades JPA para fortalecer la integridad de la información.


- Imagenes

En esta sección se muestran las evidencias obtenidas al probar las validaciones de la API utilizando Bruno. Las capturas incluyen casos de solicitudes válidas e inválidas, respuestas de error generadas por Spring Boot ante datos incorrectos y la verificación del funcionamiento correcto del CRUD cuando las entradas cumplen las reglas definidas.

![Bruno Products Valid](docs/images/6.1.png)

![Bruno Products Valid](docs/images/6.2.png)

---

### Practica 07

- Descripcion

Esta práctica introduce el manejo centralizado de excepciones y la generación de respuestas de error consistentes en una aplicación Spring Boot. Se implementan mecanismos para capturar errores de validación, recursos inexistentes y reglas de negocio incumplidas mediante excepciones personalizadas y controladores globales, permitiendo que la API devuelva respuestas claras, estructuradas y adecuadas para el cliente sin exponer detalles internos de la aplicación.

- Resultados

En esta sección se presentan las evidencias obtenidas mediante pruebas realizadas con Bruno, mostrando tanto operaciones exitosas como distintos escenarios de error. Las capturas permiten verificar el correcto funcionamiento del manejo centralizado de excepciones, incluyendo respuestas ante validaciones fallidas, recursos no encontrados y otras situaciones controladas por la API.

![Error Product Duplicated](docs/images/7.1.png)

![No Product Validation](docs/images/7.2.png)

---

### Practica 08

- Capturas

1. Captura de la descripción de la tabla products en PostgreSQL

![Products on PostgreSQL](docs/images/8.1.png)

2. Captura de la creación de un producto con relaciones
![Bruno Product Relations](docs/images/8.2.png)

3. Captura de la consulta de productos por categoría
![Endpoint Products Category](docs/images/8.3.png)

- Explicación breve

`ProductEntity` se relaciona con `UserEntity` y `CategoryEntity` mediante `@ManyToOne`, indicando que varios productos pueden pertenecer a un mismo usuario o categoría. La anotación `@JoinColumn` define las claves foráneas (`user_id` y `category_id`) que enlazan la tabla products con las tablas `users` y `categories`.

---

### Practica 09

- Capturas

**1. Captura de producto creado con varias categorías**
![Product Created on Categories](docs/images/9.1.png)

**2. Captura de consulta con filtros por usuario**
![Products with Filters by User](docs/images/9.2.png)

**3. Captura de consulta con filtros por categoría**
![Products with Filters by Category](docs/images/9.3.png)

- Explicación breve

**1) ¿Por qué se usa ProductService y ProductRepository para consultar productos aunque el endpoint esté dentro del contexto ``/users/{id}/products o /categories/{id}/products``?**

Aunque la ruta expresa un contexto semántico basado en el usuario o la categoría para mantener una `API RESTFUL` e intuitiva, el recurso principal que se está consultando, filtrando y devolviendo son los productos. Por lo tanto, la responsabilidad de la lógica de negocio y el acceso a la base de datos recae estrictamente en `ProductService` y `ProductRepository`, respetando el principio de responsabilidad única de cada componente.

**2) ¿Qué cambió al pasar de Product N ─- 1 Category a Product N ── N Category?**

Se eliminó la relación directa que usaba una única clave foránea en la entidad de productos con la anotación `@ManyToOne`. En su lugar, se implementó una relación `@ManyToMany`, lo que genera automáticamente una tabla intermedia en la base de datos para enlazar ambas entidades sin duplicar registros. A nivel de código, el atributo individual `CategoryEntity category` se transformó en una colección `Set<CategoryEntity>` categories. Esto obligó a modificar los DTOs para recibir arreglos de IDs, cambiar los mappers para devolver listas de categorías y actualizar las consultas JPQL en los repositorios utilizando JOIN con la cláusula `DISTINCT` para evitar productos duplicados en los resultados.

---

### Practica 10

- Capturas

**1. Captura de respuesta con Page**
![Products with Page](docs/images/10.1.png)

**2. Captura de respuesta con Slice**
![Products with Slice](docs/images/10.2.png)

**3. Captura de error por paginación inválida**
![Products with Page Invalid](docs/images/10.3.png)

**4. Captura de endpoint de categoría paginado con Page**
![Products with Page Category](docs/images/10.4.png)

**5. Captura de endpoint de categoría paginado con Slice**
![Products with Slice Category](docs/images/10.5.png)

- Explicación breve

**1) ¿Cuál es la diferencia entre Page y Slice?**

Page representa una respuesta paginada completa e incluye metadatos como el total de elementos y el total de páginas. Para lograr esto, ejecuta dos consultas en la base de datos: una para obtener los datos con `LIMIT` y `OFFSET`, y otra de tipo `COUNT` para saber el tamaño total de la tabla. Por otro lado, Slice es una versión más ligera que no incluye el total de elementos ni páginas, ya que omite la consulta `COUNT`. Simplemente solicita un registro adicional a la base de datos para determinar si existe una página siguiente, siendo ideal y más eficiente para funcionalidades como navegación secuencial o scroll infinito.

**2) ¿Por qué la paginación debe aplicarse en el repositorio y no después de traer todos los datos en memoria?**

Si la paginación se realiza en memoria, el sistema intentará consultar y cargar todos los registros existentes desde la base de datos al backend simultáneamente. Esto genera un consumo excesivo de recursos, sobrecarga de red, lentitud y posibles caídas por falta de memoria. Al aplicar la paginación a nivel de repositorio usando Pageable, el motor de base de datos se encarga de filtrar la cantidad exacta de registros requeridos mediante comandos SQL, enviando al servidor únicamente la pequeña fracción de datos solicitada, lo que garantiza el rendimiento y la escalabilidad de la API.

---

### Practica 11

- Capturas

**1. Captura de registro exitoso**
![Register Successful](docs/images/11.1.png)

**2. Captura de login exitoso**
![Login Successful](docs/images/11.2.png)

**3. Captura de endpoint protegido sin token**
![Endpoint protected w no Token](docs/images/11.3.png)

**4. Captura de endpoint protegido con token**
![Endpoint protected w Token](docs/images/11.4.png)

---

### Practica 12

- Capturas

**1. Captura de usuario autenticado**
![User authenticated](docs/images/12.1.png)

**2. Captura de acceso denegado por rol**
![Access denied by role](docs/images/12.2.png)

**3. Captura de acceso permitido por rol ADMIN**
![Access granted for ADMIN role](docs/images/12.3.png)

- Explicación breve

**1) ¿Cuál es la diferencia entre autenticación y autorización?**

Autenticación: verifica quién eres. Consiste en confirmar la identidad de un usuario, por ejemplo, mediante un nombre de usuario y contraseña, huella dactilar o reconocimiento facial.
Autorización: determina qué puedes hacer una vez que ya has sido autenticado. Define los permisos o recursos a los que tienes acceso según tu rol o privilegios.

**2) ¿Por qué GET /api/products debe ser solo para ADMIN, mientras GET /api/products/page puede ser consumido por cualquier usuario autenticado?**

`GET /api/products` es exclusivo para `ADMIN` porque está orientado a la administración del catálogo de productos. En cambio, `GET /api/products/page` puede ser usado por cualquier usuario autenticado, ya que solo permite consultar los productos de forma paginada.

---

### Practica 13

- Capturas

**1. Captura de creación de producto con usuario autenticado**
![POST products with authenticated user](docs/images/13.1.png)

**2. Captura de bloqueo por producto ajeno**
![Unaffiliated product block](docs/images/13.2.png)

**3. Captura de eliminación de producto ajeno bloqueada**
![Deletion of unaffiliated product blocked](docs/images/13.3.png)

**4. Captura de ADMIN modificando producto ajeno**
![ADMIN modifying another user's product](docs/images/13.4.png)

- Explicación breve

**1) ¿Qué es ownership?**

En Spring Boot y desarrollo web, ownership es la responsabilidad o propiedad de una parte del código o recurso. Significa que una persona, equipo o componente es el encargado de mantener, modificar y garantizar el correcto funcionamiento de ese elemento, como un controlador, servicio, entidad o API.


**2) ¿Por qué no es seguro recibir userId en CreateProductDto?**

No es seguro recibir `userId` en `CreateProductDto` porque un usuario podría modificar ese valor y crear un producto a nombre de otra persona. Lo correcto es obtener el ID del usuario autenticado desde el contexto de seguridad, en lugar de confiar en un dato enviado por el cliente.


**3) ¿Cuál es la diferencia entre autorización por rol y autorización por ownership?**

La autorización por rol permite o deniega acciones según el rol del usuario. En cambio, la autorización por ownership verifica si el usuario es el propietario del recurso sobre el que intenta realizar una acción, aunque tenga el rol requerido.

---

### Practica 14

- Capturas

**1. Captura de login con refresh token**
![POST login with refresh token](docs/images/14.1.png)

**2. Captura de refresh exitoso**
![POST with new refresh token](docs/images/14.2.png)

**3. Captura de logout**
![Account logout](docs/images/14.3.png)

**4. Captura de refresh después de logout**
![RefreshToken canceled after logout](docs/images/14.4.png)

- Explicación breve

**1) ¿Cuál es la diferencia entre access token y refresh token?**

El access token es un token de corta duración que se utiliza para autenticar las solicitudes a la API y acceder a los recursos protegidos. En cambio, el refresh token tiene una vida útil más larga y solo se utiliza para obtener un nuevo access token cuando este expira, sin que el usuario tenga que volver a iniciar sesión.


**2) ¿Por qué el refresh token no debe usarse en Authorization: Bearer?**

El refresh token no debe enviarse en el encabezado `Authorization: Bearer` porque no está diseñado para acceder a recursos protegidos. Su único propósito es solicitar un nuevo access token, por lo que exponerlo en cada petición aumenta el riesgo de robo y uso indebido.


**3) ¿Qué significa rotar un refresh token?**

Rotar un refresh token significa que cada vez que se utiliza para obtener un nuevo access token, el servidor genera también un nuevo refresh token e invalida el anterior. Esto mejora la seguridad, ya que un token robado deja de ser válido después de ser usado.

---

### Practica 15

- Capturas
**1. Captura de Swagger UI cargado**
![Swagger UI loaded](docs/images/15.1.png)

**2. Captura del JSON OpenAPI**
![OpenAPI JSON](docs/images/15.2.png)

**3. Captura de AuthController documentado**
![AuthController docs](docs/images/15.3.png)

**4. Captura del botón Authorize**
![Authorize button](docs/images/15.4.png)

**5. Captura de endpoint protegido sin token**
![Protected endpoint without token](docs/images/15.5.png)

**6. Captura de endpoint protegido con token desde Swagger**
![Protected endpoint with token](docs/images/15.6.png)

**7. Captura de endpoint ADMIN con usuario normal**
![Admin endpoint with normal user](docs/images/15.7.png)

**8. Captura de endpoint ADMIN con usuario administrador**
![Admin endpoint with admin user](docs/images/15.8.png)
Se realizo en bruno y no en SWAGGER debido al peso de los 20000 productos cargados en postgres

- Explicación breve

**1) ¿Cuál es la diferencia entre Swagger UI y OpenAPI?**

OpenAPI es una especificación estándar que describe cómo está estructurada una API REST (endpoints, parámetros, respuestas, autenticación, etc.). Swagger UI es una herramienta que utiliza esa especificación para generar una interfaz web interactiva donde se puede visualizar y probar la API desde el navegador.


**2) ¿Por qué Swagger puede ser público pero los endpoints seguir protegidos?**

Porque Swagger UI solo muestra la documentación de la API. Aunque cualquier usuario pueda acceder a esa documentación, los endpoints siguen siendo protegidos por los mecanismos de seguridad de la aplicación (como JWT o Spring Security). Si un endpoint requiere autenticación, las solicitudes sin un token válido serán rechazadas con un error como 401 Unauthorized.


**3) ¿Cómo se configura Swagger para enviar un JWT en Authorization: Bearer?**

Se define un esquema de seguridad de tipo HTTP Bearer en la configuración de OpenAPI y se indica que la API utiliza ese esquema. Luego, en Swagger UI aparece el botón Authorize, donde el usuario ingresa su token JWT. A partir de ese momento, Swagger envía automáticamente el encabezado:
`Authorization: Bearer <JWT>`

---

### Practica 16

- Capturas

**1. Captura de docker ps de Ubuntu Server mostrando ambos contenedores en ejecución.**
![Docker ps on Ubuntu Server](docs/images/16.1.png)

**2. Captura de curl de `/api/actuator/health` desde Ubuntu Server**
![CURL on ubuntu server](docs/images/16.2.png)

**3. Captura de curl de `/api/actuator/health` desde la máquina anfitriona.**
![CURL on host](docs/images/16.3.png)

**4. Captura consumo de login desde la máquina anfitriona con Bruno o Postman.**
![Bruno login endpoint](docs/images/16.4.png)

- Explicación breve

**1) Explicación de la conexión a PostgreSQL externo.**

La aplicación Spring Boot, corriendo en un contenedor Docker dentro de Ubuntu Server, se conecta a una instancia de PostgreSQL que corre en un contenedor Docker en la máquina HOST (Windows), publicada en el puerto `5432`. La conexión se realiza a través de la red Host-Only de VirtualBox (`192.168.56.0/24`), usando la IP `192.168.56.1` del HOST como `DB_HOST`. Se verificó la conectividad primero con `psql` desde la VM antes de desplegar el contenedor de la API.

---