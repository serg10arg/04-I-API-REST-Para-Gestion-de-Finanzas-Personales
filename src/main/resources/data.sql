-- Sintaxis MERGE INTO compatible con H2 para asegurar que los roles básicos existan.
-- KEY(nombre) le dice a H2 que la columna 'nombre' es la clave para la fusión.
MERGE INTO roles (nombre) KEY(nombre) VALUES ('ROLE_USER');
MERGE INTO roles (nombre) KEY(nombre) VALUES ('ROLE_ADMIN');