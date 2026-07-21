# HomeStay (Proyecto Java)

## Descripción
Este repositorio contiene una aplicación de consola en Java que modela un sistema tipo *marketplace* de alojamiento (anfitriones, huéspedes, propiedades, unidades, reservas, reseñas e incidentes).

El código está organizado con separación por capas:
- `modelo`: entidades del dominio (`Usuario`, `Huesped`, `Anfitrion`, `Propiedad`, `Unidad`, `Reporte`, etc.).
- `logica`: casos de uso y coordinación (`HuespedManager`, `AnfitrionManager`, `BuscadorPropiedades`).
- `ui`: interacción de consola para flujo de huésped.
- `notificaciones`: jerarquía de notificadores (`Email`, `Sms`, `Mensajeria`).
- `ec.edu.espol`: punto de entrada y menús principales.

## Estructura principal
- `proyectods/pom.xml`: configuración Maven (Java 25 + JUnit 5).
- `proyectods/src/main/java`: código fuente.
- `proyectods/src/test/java`: pruebas unitarias.
- `proyectods/docker-compose.yml`: entorno de SonarQube + PostgreSQL.

## Funcionalidades implementadas
- Registro e inicio de sesión por rol (`Huesped` y `Anfitrion`).
- Gestión de propiedades y unidades de alojamiento.
- Búsqueda de unidades por:
  - ubicación,
  - precio máximo,
  - tipo de propiedad (`Casa`, `DepartamentoCompleto`, `HabitacionPrivada`),
  - servicios (`PetFriendly`, `WiFi`, `Piscina`, `Estacionamiento`).
- Flujo de reserva/cancelación y consulta de historial de reservas.
- Reseñas entre usuarios.
- Reporte de incidentes con resolución encadenada (`Anfitrion -> Moderador -> SoporteLegal`).

## Requisitos
- JDK 25
- Maven 3.9+

## Cómo ejecutar
Desde el módulo Maven:

```bash
cd proyectods
mvn clean compile
mvn exec:java -Dexec.mainClass="ec.edu.espol.Main"
```

> Nota: si `exec:java` no está disponible en tu entorno, puedes compilar y ejecutar con `java` sobre las clases generadas.

## Cómo ejecutar pruebas
```bash
cd proyectods
mvn test
```

Resultado verificado en este repositorio: **70 tests**, **0 fallos**, **0 errores**.

## Calidad de código (opcional)
El repositorio incluye `docker-compose.yml` para levantar SonarQube local con PostgreSQL.

```bash
cd proyectods
docker compose up -d
```

Luego se puede ejecutar el análisis Maven Sonar desde el mismo módulo.

## Interfaz Grafica
```bash
cd proyectods
java -cp target\classes ec.edu.espol.Main
```

