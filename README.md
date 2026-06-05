# Práctica - CRUD Web y Validaciones

**Nombre:** Toporek Coca Eric
**Num de Cuenta:** 314284987

Este es un proyecto backend desarrollado en **Java usando Spring Boot**, el cual permite la administración de una entidad llamada `Categoría` de forma RESTful sobre una base de datos MySQL. Cuenta con capacidades de realizar un "soft-delete" de categorías y consultar listados activos e inactivos.

## 🆕 Changelog de la última versión

Los principales cambios y características añadidas a la aplicación en sus últimas entregas son:

- **Gestión de Productos (Product API):** Creación de la entidad `Product` y exposición de su CRUD estructurado mediante las DTOs de entrada y salida a través de `CtrlProduct`.
- **Administración de Imágenes de Producto en Base64:** Implementación de persistencia de archivos en un volumen del sistema de archivos interceptando codificaciones string dentro de `SvcProductImageImp`. Soporta guardado dinámico de UUID, inyección de directorios, consultas en base de datos (`product_image`) e incluye verificaciones estrictas de pertenencia de imagen y producto.
- **Manejo Avanzado de Excepciones SQL de Producto:** El sistema es totalmente tolerante a problemas de integridad arrojando al API limpiamente (HTTP 409 y HTTP 404) los fallos al violarse `ux_product_gtin`, `ux_product_product` y `fk_product_category`.
- **Esquema de Base de Datos Unificado:** Las instrucciones de instanciación del ambiente MySQL (`create_database.sql`) fueron enriquecidas compactando relacionalmente las nuevas tablas pertinentes (`product`, `product_image`).
- **Seguridad y JWT (Spring Security):** Implementación integral de un protocolo `Stateless` de validación. Se creó el filtro de intercepción lógica `JwtAuthFilter` para decodificar arreglos de roles dinámicos, aislando la estructura a través un secred compartido (Symmetric validation) que previene el acceso no autorizado a través del `SecurityConfig`.
- **CRUD de Categorías (Previo):** Rutas asiladas para operaciones con Soft-Delete.
- **Validaciones Integradas:** Uso continuado de `@Valid` y `jakarta.validation` para depurar entradas del usuario desde la capa del mapeador.

## �🚀 Requisitos Previos

Para ejecutar la aplicación localmente vas a necesitar:
- **Java 17 o superior** (configurado en el PATH o a través de SDKMAN).
- **Maven** (usualmente provisto con Spring Boot, o `mvn` independiente).
- **MySQL 9.x+** para servir la base de datos (con credenciales y base de datos configurados mediante el archivo SQL provisto).

## 🛠️ Configuración de la Base de Datos

Antes de arrancar la aplicación, debes proveer la base de datos, las credenciales, los permisos y opcionalmente los datos mock.

Encontrarás el archivo `src/sql/create_database.sql`. Puedes ejecutarlo directamente contra tu cliente de MySQL local iniciando sesión con root:

```bash
mysql -u root -p < src/sql/create_database.sql
```

**Lo que hace el archivo SQL es:**
1. Crear una base de datos llamada `SWDB2026`.
2. Crear la tabla pertinente de `category` con restricciones de tipo `UNIQUE`.
3. Crear un usuario de MySQL local llamado `swdb_admin` con contraseña.
4. Conceder los permisos de dicha base a este usuario.
5. Llenar la base de datos con unas cuantas categorías de prueba.

La aplicación conectará con esta base usando el perfil default que apunta a `jdbc:mysql://localhost:3306/SWDB2026` con dichas credenciales (a menos que lo cambies en `application.properties`).

## ⚙️ Cómo Compilar y Ejecutar

Con la base de datos y la versión de Java correctas en pie, ejecutar o compilar el proyecto es tan fácil como usar:

### 1) Compilación (Build)
Para compilar y descargar las dependencias de Maven, así como asegurar el código:
```bash
mvn clean install
```
> *(Para evitar ejecutar test largos en la etapa inicial puedes usar `mvn clean install -DskipTests`)*.

### 2) Ejecución
Puedes arrancar la aplicación usando la orden que expone el wrapper de Spring Boot por Maven:
```bash
mvn spring-boot:run
```
O de manera manual arrancando el `jar` resultante tras haber corrido `mvn clean install`:
```bash
java -jar target/product-0.0.1-SNAPSHOT.jar
```

## 🌐 Uso de la API
* La aplicación por defecto correrá en el puerto `8080`.
* Actualmente expone los siguientes Paths REST principales: 
  * Configuración de Categorías: `http://localhost:8080/category`
  * Administración de Productos: `http://localhost:8080/product`
  * Control de Imágenes anidadas: Ej. `http://localhost:8080/product/{id}/image`

## 🔒 Autenticación y Pruebas (JWT)

A partir de las versiones recientes, el acceso a la base de datos se encuentra restringido al uso de validación Bearer nativo a través de **Spring Security**. 

Para inyectar llamadas HTTP lícitas hacia esta API, es preciso seguir este flujo de verificación:

1. **Obtener el Token desde Autorización:** Efectúa una petición válida al enrutador `POST /login` de tu servicio adjunto (`auth-service` en modo local apuntando normalmente a `:8082`), y captura la respuesta cifrada que simula la cadena JSON del token.
2. **Configuración de Cabecera:** Anexa la firma obtenida al protocolo `Authorization` dentro de tus headers cliente.

### Ejemplo de Prueba via cURL

```bash
curl -X GET http://localhost:8080/category/active \
     -H "Authorization: Bearer <INSERTA_TU_TOKEN_JWT_AQUI>" \
     -i
```

> [!NOTE] 
> **Barrera de Permisos Interceptados**
> Ten en cuenta que si tu Token fue emitido bajo la etiqueta de rol `"CUSTOMER"`, la API delegará tu navegación restringiéndote únicamente a visualizaciones seguras (`GET /category/active`, `GET /product`...); cualquier manipulación superior arrojará un error 403 Forbidden. Modificaciones, subidas de archivos o Soft-Deletes se encuentran atrincherados exclusivamentre para la cabecera del rol `"ADMIN"`.
