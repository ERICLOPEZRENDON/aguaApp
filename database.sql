CREATE TABLE estados_republica (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE municipios (
    id SERIAL PRIMARY KEY,
    estado_id INT REFERENCES estados_republica(id) NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE codigos_postales (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE colonias (
    id BIGSERIAL PRIMARY KEY,
    municipio_id INT REFERENCES municipios(id) NOT NULL,
    cp_id INT REFERENCES codigos_postales(id) NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE permisos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE roles_permisos (
    rol_id INT REFERENCES roles(id) ON DELETE CASCADE,
    permiso_id INT REFERENCES permisos(id) ON DELETE CASCADE,
    PRIMARY KEY (rol_id, permiso_id)
);

CREATE TABLE usuarios_auth (
    id BIGSERIAL PRIMARY KEY,
    rol_id INT REFERENCES roles(id) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE perfiles_usuario (
    id BIGSERIAL PRIMARY KEY,
    usuario_auth_id BIGINT UNIQUE REFERENCES usuarios_auth(id) ON DELETE CASCADE,
    colonia_id BIGINT REFERENCES colonias(id),
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    telefono VARCHAR(20)
);

CREATE TABLE dias_semana (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(15) UNIQUE NOT NULL
);

CREATE TABLE estados_semaforo (
    id SERIAL PRIMARY KEY,
    color VARCHAR(20) UNIQUE NOT NULL, 
    descripcion VARCHAR(100)
);

CREATE TABLE tipos_problema (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    nivel_prioridad INT DEFAULT 1
);

CREATE TABLE estados_reporte (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE horarios_suministro (
    id BIGSERIAL PRIMARY KEY,
    colonia_id BIGINT REFERENCES colonias(id) NOT NULL,
    dia_id INT REFERENCES dias_semana(id) NOT NULL,
    estado_semaforo_id INT REFERENCES estados_semaforo(id) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reportes (
    id BIGSERIAL PRIMARY KEY,
    perfil_usuario_id BIGINT REFERENCES perfiles_usuario(id) NOT NULL,
    colonia_id BIGINT REFERENCES colonias(id) NOT NULL,
    tipo_problema_id INT REFERENCES tipos_problema(id) NOT NULL,
    descripcion TEXT,
    latitud DECIMAL(10, 8),
    longitud DECIMAL(11, 8),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE historial_estados_reporte (
    id BIGSERIAL PRIMARY KEY,
    reporte_id BIGINT REFERENCES reportes(id) ON DELETE CASCADE,
    estado_reporte_id INT REFERENCES estados_reporte(id) NOT NULL,
    modificado_por_auth_id BIGINT REFERENCES usuarios_auth(id),
    comentarios_resolucion TEXT,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE evidencias_reporte (
    id BIGSERIAL PRIMARY KEY,
    reporte_id BIGINT REFERENCES reportes(id) ON DELETE CASCADE,
    ruta_archivo VARCHAR(255) NOT NULL, 
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_reportes_colonia ON reportes(colonia_id);
CREATE INDEX idx_historial_reporte ON historial_estados_reporte(reporte_id);

INSERT INTO roles (nombre) VALUES 
('ADMIN'), 
('CIUDADANO'), 
('OPERADOR_HIDRAULICO');

INSERT INTO tipos_problema (nombre, nivel_prioridad) VALUES 
('FUGA_MASIVA', 3), 
('FALTA_DE_AGUA', 2), 
('BAJA_PRESION', 1);

INSERT INTO estados_semaforo (color, descripcion) VALUES 
('VERDE', 'Suministro normal y constante'), 
('AMARILLO', 'Suministro intermitente o baja presión'), 
('ROJO', 'Corte total por mantenimiento o reparación');

INSERT INTO codigos_postales (codigo) VALUES 
('55740'), 
('55760'), 
('55770');

INSERT INTO usuarios_auth (rol_id, email, password_hash, activo) VALUES 
(1, 'admin@aguaapp.com', 'hash_seguro_123', true),
(2, 'ciudadano1@correo.com', 'hash_seguro_456', true); 