DROP DATABASE IF EXISTS ColegioDB;
CREATE DATABASE IF NOT EXISTS ColegioDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ColegioDB;

CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL UNIQUE,
  PRIMARY KEY (id_rol)
) ENGINE=InnoDB;

CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  id_rol INT NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  correo VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ultimo_acceso DATETIME,
  PRIMARY KEY (id_usuario),
  CONSTRAINT fk_usuario_id_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
) ENGINE=InnoDB;

CREATE TABLE token_recuperacion (
  id_token INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  token VARCHAR(100) NOT NULL UNIQUE,
  fecha_expiracion DATETIME NOT NULL,
  usado BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (id_token),
  CONSTRAINT fk_token_recuperacion_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE empleado (
  id_empleado INT NOT NULL AUTO_INCREMENT,
  id_usuario INT UNIQUE,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  dpi VARCHAR(13) NOT NULL UNIQUE,
  fecha_nacimiento DATE NOT NULL,
  telefono VARCHAR(15),
  direccion VARCHAR(200),
  puesto VARCHAR(60) NOT NULL,
  fecha_contratacion DATE NOT NULL,
  salario DECIMAL(10,2) NOT NULL,
  estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_empleado),
  CONSTRAINT fk_empleado_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CHECK (dpi REGEXP '^[0-9]{13}$'),
  CHECK (salario >= 0),
  CHECK (puesto IN ('Maestro','Secretaría','Biblioteca','Limpieza','Mantenimiento','Administrativo'))
) ENGINE=InnoDB;

CREATE TABLE ciclo_escolar (
  id_ciclo INT NOT NULL AUTO_INCREMENT,
  anio INT NOT NULL UNIQUE,
  fecha_inicio DATE NOT NULL,
  fecha_fin DATE NOT NULL,
  estado ENUM('PLANIFICADO','ACTIVO','CERRADO') NOT NULL DEFAULT 'PLANIFICADO',
  PRIMARY KEY (id_ciclo),
  CHECK (fecha_fin > fecha_inicio)
) ENGINE=InnoDB;

CREATE TABLE nivel (
  id_nivel INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL UNIQUE,
  PRIMARY KEY (id_nivel)
) ENGINE=InnoDB;

CREATE TABLE carrera (
  id_carrera INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  descripcion TEXT,
  activo BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (id_carrera)
) ENGINE=InnoDB;

CREATE TABLE grado (
  id_grado INT NOT NULL AUTO_INCREMENT,
  id_nivel INT NOT NULL,
  id_carrera INT,
  nombre VARCHAR(60) NOT NULL,
  cupo_default INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (id_grado),
  CONSTRAINT fk_grado_id_nivel FOREIGN KEY (id_nivel) REFERENCES nivel (id_nivel),
  CONSTRAINT fk_grado_id_carrera FOREIGN KEY (id_carrera) REFERENCES carrera (id_carrera),
  UNIQUE (id_nivel, id_carrera, nombre),
  CHECK (cupo_default > 0)
) ENGINE=InnoDB;

CREATE TABLE seccion (
  id_seccion INT NOT NULL AUTO_INCREMENT,
  id_grado INT NOT NULL,
  nombre VARCHAR(10) NOT NULL,
  cupo INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (id_seccion),
  CONSTRAINT fk_seccion_id_grado FOREIGN KEY (id_grado) REFERENCES grado (id_grado),
  UNIQUE (id_grado, nombre),
  CHECK (cupo > 0)
) ENGINE=InnoDB;

CREATE TABLE curso (
  id_curso INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  descripcion TEXT,
  activo BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (id_curso)
) ENGINE=InnoDB;

CREATE TABLE curriculo (
  id_curriculo INT NOT NULL AUTO_INCREMENT,
  id_grado INT NOT NULL,
  id_ciclo INT NOT NULL,
  PRIMARY KEY (id_curriculo),
  CONSTRAINT fk_curriculo_id_grado FOREIGN KEY (id_grado) REFERENCES grado (id_grado),
  CONSTRAINT fk_curriculo_id_ciclo FOREIGN KEY (id_ciclo) REFERENCES ciclo_escolar (id_ciclo),
  UNIQUE (id_grado, id_ciclo)
) ENGINE=InnoDB;

CREATE TABLE curriculo_curso (
  id_curriculo_curso INT NOT NULL AUTO_INCREMENT,
  id_curriculo INT NOT NULL,
  id_curso INT NOT NULL,
  PRIMARY KEY (id_curriculo_curso),
  CONSTRAINT fk_curriculo_curso_id_curriculo FOREIGN KEY (id_curriculo) REFERENCES curriculo (id_curriculo),
  CONSTRAINT fk_curriculo_curso_id_curso FOREIGN KEY (id_curso) REFERENCES curso (id_curso),
  UNIQUE (id_curriculo, id_curso)
) ENGINE=InnoDB;

CREATE TABLE asignacion_maestro (
  id_asignacion INT NOT NULL AUTO_INCREMENT,
  id_empleado INT NOT NULL,
  id_curriculo_curso INT NOT NULL,
  id_seccion INT NOT NULL,
  PRIMARY KEY (id_asignacion),
  CONSTRAINT fk_asignacion_maestro_id_empleado FOREIGN KEY (id_empleado) REFERENCES empleado (id_empleado),
  CONSTRAINT fk_asignacion_maestro_id_curriculo_curso FOREIGN KEY (id_curriculo_curso) REFERENCES curriculo_curso (id_curriculo_curso),
  CONSTRAINT fk_asignacion_maestro_id_seccion FOREIGN KEY (id_seccion) REFERENCES seccion (id_seccion),
  UNIQUE (id_curriculo_curso, id_seccion)
) ENGINE=InnoDB;

CREATE TABLE estudiante (
  id_estudiante INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL UNIQUE,
  carnet VARCHAR(20) NOT NULL UNIQUE,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  dpi VARCHAR(13) UNIQUE,
  fecha_nacimiento DATE NOT NULL,
  genero ENUM('M','F') NOT NULL,
  direccion VARCHAR(200),
  estado ENUM('ACTIVO','INACTIVO','GRADUADO') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_estudiante),
  CONSTRAINT fk_estudiante_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CHECK (dpi IS NULL OR dpi REGEXP '^[0-9]{13}$')
) ENGINE=InnoDB;

CREATE TABLE ficha_medica (
  id_ficha INT NOT NULL AUTO_INCREMENT,
  id_estudiante INT NOT NULL UNIQUE,
  alergias TEXT,
  condiciones TEXT,
  contacto_emergencia VARCHAR(100) NOT NULL,
  telefono_emergencia VARCHAR(15) NOT NULL,
  PRIMARY KEY (id_ficha),
  CONSTRAINT fk_ficha_medica_id_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante)
) ENGINE=InnoDB;

CREATE TABLE encargado (
  id_encargado INT NOT NULL AUTO_INCREMENT,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  dpi VARCHAR(13) NOT NULL UNIQUE,
  telefono VARCHAR(15) NOT NULL,
  correo VARCHAR(100),
  direccion VARCHAR(200),
  PRIMARY KEY (id_encargado),
  CHECK (dpi REGEXP '^[0-9]{13}$')
) ENGINE=InnoDB;

CREATE TABLE estudiante_encargado (
  id_estudiante INT NOT NULL,
  id_encargado INT NOT NULL,
  parentesco VARCHAR(30) NOT NULL,
  es_principal BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (id_estudiante, id_encargado),
  CONSTRAINT fk_estudiante_encargado_id_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_estudiante_encargado_id_encargado FOREIGN KEY (id_encargado) REFERENCES encargado (id_encargado)
) ENGINE=InnoDB;

CREATE TABLE inscripcion (
  id_inscripcion INT NOT NULL AUTO_INCREMENT,
  id_estudiante INT NOT NULL,
  id_ciclo INT NOT NULL,
  id_seccion INT NOT NULL,
  id_usuario INT NOT NULL,
  fecha_inscripcion DATE NOT NULL,
  estado ENUM('ACTIVA','RETIRADA') NOT NULL DEFAULT 'ACTIVA',
  PRIMARY KEY (id_inscripcion),
  CONSTRAINT fk_inscripcion_id_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_inscripcion_id_ciclo FOREIGN KEY (id_ciclo) REFERENCES ciclo_escolar (id_ciclo),
  CONSTRAINT fk_inscripcion_id_seccion FOREIGN KEY (id_seccion) REFERENCES seccion (id_seccion),
  CONSTRAINT fk_inscripcion_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  UNIQUE (id_estudiante, id_ciclo)
) ENGINE=InnoDB;

CREATE TABLE ponderacion (
  id_ponderacion INT NOT NULL AUTO_INCREMENT,
  id_asignacion INT NOT NULL,
  nombre VARCHAR(60) NOT NULL,
  porcentaje DECIMAL(5,2) NOT NULL,
  nota_maxima DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (id_ponderacion),
  CONSTRAINT fk_ponderacion_id_asignacion FOREIGN KEY (id_asignacion) REFERENCES asignacion_maestro (id_asignacion),
  CHECK (porcentaje > 0 AND porcentaje <= 100),
  CHECK (nota_maxima > 0)
) ENGINE=InnoDB;

CREATE TABLE nota (
  id_nota INT NOT NULL AUTO_INCREMENT,
  id_inscripcion INT NOT NULL,
  id_ponderacion INT NOT NULL,
  id_usuario INT NOT NULL,
  valor DECIMAL(5,2) NOT NULL,
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_nota),
  CONSTRAINT fk_nota_id_inscripcion FOREIGN KEY (id_inscripcion) REFERENCES inscripcion (id_inscripcion),
  CONSTRAINT fk_nota_id_ponderacion FOREIGN KEY (id_ponderacion) REFERENCES ponderacion (id_ponderacion),
  CONSTRAINT fk_nota_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  UNIQUE (id_inscripcion, id_ponderacion),
  CHECK (valor >= 0)
) ENGINE=InnoDB;

CREATE TABLE asistencia (
  id_asistencia INT NOT NULL AUTO_INCREMENT,
  id_inscripcion INT NOT NULL,
  id_asignacion INT NOT NULL,
  fecha DATE NOT NULL,
  estado ENUM('PRESENTE','AUSENTE','TARDANZA') NOT NULL,
  PRIMARY KEY (id_asistencia),
  CONSTRAINT fk_asistencia_id_inscripcion FOREIGN KEY (id_inscripcion) REFERENCES inscripcion (id_inscripcion),
  CONSTRAINT fk_asistencia_id_asignacion FOREIGN KEY (id_asignacion) REFERENCES asignacion_maestro (id_asignacion),
  UNIQUE (id_inscripcion, id_asignacion, fecha)
) ENGINE=InnoDB;

CREATE TABLE libro (
  id_libro INT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(150) NOT NULL,
  autor VARCHAR(100) NOT NULL,
  editorial VARCHAR(100) NOT NULL,
  cantidad_total INT NOT NULL,
  cantidad_disponible INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (id_libro),
  CHECK (cantidad_total >= 0),
  CHECK (cantidad_disponible >= 0 AND cantidad_disponible <= cantidad_total)
) ENGINE=InnoDB;

CREATE TABLE config_biblioteca (
  id_config INT NOT NULL AUTO_INCREMENT,
  dias_prestamo INT NOT NULL,
  multa_por_dia DECIMAL(8,2) NOT NULL,
  fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_config),
  CHECK (dias_prestamo > 0),
  CHECK (multa_por_dia >= 0)
) ENGINE=InnoDB;

CREATE TABLE prestamo (
  id_prestamo INT NOT NULL AUTO_INCREMENT,
  id_libro INT NOT NULL,
  id_estudiante INT,
  id_empleado INT,
  id_usuario INT NOT NULL,
  fecha_prestamo DATE NOT NULL,
  fecha_esperada DATE NOT NULL,
  fecha_devolucion DATE,
  estado ENUM('ACTIVO','DEVUELTO') NOT NULL DEFAULT 'ACTIVO',
  observacion TEXT,
  PRIMARY KEY (id_prestamo),
  CONSTRAINT fk_prestamo_id_libro FOREIGN KEY (id_libro) REFERENCES libro (id_libro),
  CONSTRAINT fk_prestamo_id_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_prestamo_id_empleado FOREIGN KEY (id_empleado) REFERENCES empleado (id_empleado),
  CONSTRAINT fk_prestamo_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CHECK ((id_estudiante IS NULL) <> (id_empleado IS NULL)),
  CHECK (fecha_esperada >= fecha_prestamo)
) ENGINE=InnoDB;

CREATE TABLE multa (
  id_multa INT NOT NULL AUTO_INCREMENT,
  id_prestamo INT NOT NULL UNIQUE,
  dias_atraso INT NOT NULL,
  monto DECIMAL(10,2) NOT NULL,
  estado ENUM('PENDIENTE','PAGADA') NOT NULL DEFAULT 'PENDIENTE',
  fecha_generacion DATE NOT NULL,
  PRIMARY KEY (id_multa),
  CONSTRAINT fk_multa_id_prestamo FOREIGN KEY (id_prestamo) REFERENCES prestamo (id_prestamo),
  CHECK (dias_atraso > 0),
  CHECK (monto >= 0)
) ENGINE=InnoDB;

CREATE TABLE tarifa (
  id_tarifa INT NOT NULL AUTO_INCREMENT,
  id_grado INT NOT NULL,
  id_ciclo INT NOT NULL,
  monto_inscripcion DECIMAL(10,2) NOT NULL,
  monto_mensualidad DECIMAL(10,2) NOT NULL,
  dia_vencimiento TINYINT NOT NULL,
  PRIMARY KEY (id_tarifa),
  CONSTRAINT fk_tarifa_id_grado FOREIGN KEY (id_grado) REFERENCES grado (id_grado),
  CONSTRAINT fk_tarifa_id_ciclo FOREIGN KEY (id_ciclo) REFERENCES ciclo_escolar (id_ciclo),
  UNIQUE (id_grado, id_ciclo),
  CHECK (dia_vencimiento BETWEEN 1 AND 31),
  CHECK (monto_inscripcion >= 0 AND monto_mensualidad >= 0)
) ENGINE=InnoDB;

CREATE TABLE actividad_extra (
  id_actividad INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  costo DECIMAL(10,2) NOT NULL,
  cupo_total INT NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT 1,
  PRIMARY KEY (id_actividad),
  CHECK (costo >= 0),
  CHECK (cupo_total > 0)
) ENGINE=InnoDB;

CREATE TABLE pago (
  id_pago INT NOT NULL AUTO_INCREMENT,
  id_estudiante INT NOT NULL,
  id_ciclo INT NOT NULL,
  id_actividad INT,
  id_usuario INT NOT NULL,
  concepto ENUM('INSCRIPCION','MENSUALIDAD','ACTIVIDAD') NOT NULL,
  mes TINYINT,
  monto DECIMAL(10,2) NOT NULL,
  fecha_pago DATE NOT NULL,
  PRIMARY KEY (id_pago),
  CONSTRAINT fk_pago_id_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_pago_id_ciclo FOREIGN KEY (id_ciclo) REFERENCES ciclo_escolar (id_ciclo),
  CONSTRAINT fk_pago_id_actividad FOREIGN KEY (id_actividad) REFERENCES actividad_extra (id_actividad),
  CONSTRAINT fk_pago_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CHECK (monto > 0),
  CHECK ((concepto = 'MENSUALIDAD' AND mes BETWEEN 1 AND 12) OR (concepto <> 'MENSUALIDAD' AND mes IS NULL)),
  CHECK ((concepto = 'ACTIVIDAD') = (id_actividad IS NOT NULL))
) ENGINE=InnoDB;

CREATE TABLE comprobante (
  id_comprobante INT NOT NULL AUTO_INCREMENT,
  id_pago INT NOT NULL UNIQUE,
  numero VARCHAR(20) NOT NULL UNIQUE,
  fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_comprobante),
  CONSTRAINT fk_comprobante_id_pago FOREIGN KEY (id_pago) REFERENCES pago (id_pago)
) ENGINE=InnoDB;

CREATE TABLE solvencia (
  id_solvencia INT NOT NULL AUTO_INCREMENT,
  id_estudiante INT NOT NULL,
  id_ciclo INT NOT NULL,
  id_usuario INT NOT NULL,
  fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_solvencia),
  CONSTRAINT fk_solvencia_id_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_solvencia_id_ciclo FOREIGN KEY (id_ciclo) REFERENCES ciclo_escolar (id_ciclo),
  CONSTRAINT fk_solvencia_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE categoria_gasto (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL UNIQUE,
  PRIMARY KEY (id_categoria)
) ENGINE=InnoDB;

CREATE TABLE gasto (
  id_gasto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  id_usuario INT NOT NULL,
  descripcion VARCHAR(200) NOT NULL,
  monto DECIMAL(10,2) NOT NULL,
  fecha DATE NOT NULL,
  anulado BOOLEAN NOT NULL DEFAULT 0,
  motivo_anulacion VARCHAR(200),
  PRIMARY KEY (id_gasto),
  CONSTRAINT fk_gasto_id_categoria FOREIGN KEY (id_categoria) REFERENCES categoria_gasto (id_categoria),
  CONSTRAINT fk_gasto_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CHECK (monto > 0),
  CHECK (anulado = 0 OR motivo_anulacion IS NOT NULL)
) ENGINE=InnoDB;

-- paginación
CREATE INDEX idx_pago_est_ciclo ON pago (id_estudiante, id_ciclo);
CREATE INDEX idx_pago_fecha ON pago (fecha_pago);
CREATE INDEX idx_gasto_fecha ON gasto (fecha);
CREATE INDEX idx_prestamo_estado_fecha ON prestamo (estado, fecha_esperada);
CREATE INDEX idx_nota_ponderacion ON nota (id_ponderacion);
CREATE INDEX idx_insc_seccion_ciclo ON inscripcion (id_seccion, id_ciclo);
CREATE INDEX idx_est_apellidos ON estudiante (apellidos, nombres);
CREATE INDEX idx_emp_apellidos ON empleado (apellidos, nombres);
CREATE INDEX idx_libro_titulo ON libro (titulo);

-- Datos iniciales
INSERT INTO rol (nombre) VALUES
('SUPER_ADMIN'),
('ADMIN'),
('SECRETARIA'),
('MAESTRO'),
('BIBLIOTECARIO'),
('ESTUDIANTE'),
('MANTENIMIENTO');

INSERT INTO nivel (nombre) VALUES
('Pre-primaria'),
('Primaria'),
('Básico'),
('Diversificado');

INSERT INTO categoria_gasto (nombre) VALUES
('Salarios'),
('Servicios'),
('Mantenimiento'),
('Insumos'),
('Otros');

INSERT INTO config_biblioteca (dias_prestamo, multa_por_dia)
VALUES (7, 2.00);

INSERT INTO usuario (id_rol, username, correo, password)
VALUES (
    (SELECT id_rol FROM rol WHERE nombre = 'SUPER_ADMIN'),
    'superadmin',
    'superadmin@colegio.local',
    TO_BASE64('1234')
);