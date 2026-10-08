
CREATE TABLE colonias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    municipio VARCHAR(100) NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    colonia_id BIGINT REFERENCES colonias(id),
    nombre_completo VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(50) DEFAULT 'CIUDADANO', 
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE horarios_suministro (
    id BIGSERIAL PRIMARY KEY,
    colonia_id BIGINT REFERENCES colonias(id) NOT NULL,
    dia_semana INT NOT NULL, 
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado_semaforo VARCHAR(20) NOT NULL, 
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE reportes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(id) NOT NULL,
    colonia_id BIGINT REFERENCES colonias(id) NOT NULL,
    tipo_problema VARCHAR(100) NOT NULL, 
    descripcion TEXT,
    latitud DECIMAL(10, 8), 
    longitud DECIMAL(11, 8),
    estado VARCHAR(50) DEFAULT 'PENDIENTE', 
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_reportes_colonia ON reportes(colonia_id);
CREATE INDEX idx_reportes_estado ON reportes(estado);
CREATE INDEX idx_horarios_colonia ON horarios_suministro(colonia_id);