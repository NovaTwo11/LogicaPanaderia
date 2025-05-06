## **Panadería Logicapanaderia Backend**
Este repositorio contiene la API REST del backend del proyecto de panadería "Logicapanaderia". Está desarrollado en Java con Spring Boot y se conecta a una base de datos Oracle para gestionar entidades como Clientes, Productos, Pedidos, Repartidores y Administradores.

## Tecnologías:
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Oracle Database 19c+
- Maven
- Logback

## Requisitos Previos
Antes de comenzar, asegúrate de tener instalado:
- Java 17 (o superior)
- Maven 3.6+
- Oracle Database (con una instancia y usuario creados)

## Instalación
1. Clona el repositorio:
  git clone https://github.com/tu-organizacion/logicapanaderia-backend.git
  cd logicapanaderia-backend

2. Configura las variables de entorno o el archivo application.properties con los datos de conexión a Oracle.

## Configuración
En src/main/resources/application.properties o application.yml, define:
- spring.datasource.url=jdbc:oracle:thin:@//{HOST}:{PUERTO}/{SERVICIO}  
- spring.datasource.username=${DB_USER}
- spring.datasource.password=${DB_PASSWORD}
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true
O usando variables de entorno:
- spring.datasource.username=${ORACLE_USER}
- spring.datasource.password=${ORACLE_PASS}

## Ejecución
1. Para compilar y ejecutar el proyecto:
mvn clean install
mvn spring-boot:run

2. La API estará disponible en http://localhost:8080/api.

## Estructura del proyecto

src/main/java/co/edu/uniquindio/logicapanaderia.

├── controller        # Controladores REST
├── dto               # Clases DTO (PedidoDTO, PedidoProductoDTO, etc.)
├── entity            # Entidades JPA (Cliente, Producto, Pedido, Repartidor, Administrador)
├── repository        # Repositorios Spring Data JPA
├── service           # Lógica de negocio
└── LogicapanaderiaApplication.java

## EndPoints de la Api

1. Clientes:

| Método | Ruta                 | Descripción               |
| ------ | -------------------- | ------------------------- |
| GET    | `/api/clientes`      | Listar todos los clientes |
| GET    | `/api/clientes/{id}` | Obtener cliente por ID    |
| POST   | `/api/clientes`      | Crear un nuevo cliente    |
| PUT    | `/api/clientes/{id}` | Actualizar cliente        |
| DELETE | `/api/clientes/{id}` | Eliminar cliente          |

2. Productos:

| Método | Ruta                  | Descripción                |
| ------ | --------------------- | -------------------------- |
| GET    | `/api/productos`      | Listar todos los productos |
| GET    | `/api/productos/{id}` | Obtener producto por ID    |
| POST   | `/api/productos`      | Crear un nuevo producto    |
| PUT    | `/api/productos/{id}` | Actualizar producto        |
| DELETE | `/api/productos/{id}` | Eliminar producto          |

3. Pedidos:

| Método | Ruta                | Descripción                        |
| ------ | ------------------- | ---------------------------------- |
| GET    | `/api/pedidos`      | Listar todos los pedidos           |
| GET    | `/api/pedidos/{id}` | Obtener pedido por ID              |
| POST   | `/api/pedidos`      | Crear un nuevo pedido con detalles |
| PUT    | `/api/pedidos/{id}` | Actualizar pedido existente        |
| DELETE | `/api/pedidos/{id}` | Cancelar / eliminar un pedido      |

4. Repartidores:

| Método | Ruta                     | Descripción           |
| ------ | ------------------------ | --------------------- |
| GET    | `/api/repartidores`      | Listar repartidores   |
| POST   | `/api/repartidores`      | Crear repartidor      |
| PUT    | `/api/repartidores/{id}` | Actualizar repartidor |
| DELETE | `/api/repartidores/{id}` | Eliminar repartidor   |

5. Administradores:

| Método | Ruta                        | Descripción              |
| ------ | --------------------------- | ------------------------ |
| GET    | `/api/administradores`      | Listar administradores   |
| POST   | `/api/administradores`      | Crear administrador      |
| PUT    | `/api/administradores/{id}` | Actualizar administrador |
| DELETE | `/api/administradores/{id}` | Eliminar administrador   |

## Manejo de DTOs
- PedidoDTO: contiene datos generales del pedido (id, fecha, cliente, total).
- PedidoProductoDTO: lista de productos y cantidades asociadas al pedido.
Los DTOs se configuran en el paquete dto y se usan en los controladores para entrada y salida de datos.

## Consideraciones de Seguridad
- Las credenciales de la base de datos nunca se deben subir al repositorio.
- Para ambientes de producción, configura un perfil de Spring con OAuth2 o JWT.

## Testing
Para ejecutar pruebas unitarias y de integración:
mvn test
