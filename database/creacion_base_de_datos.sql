CREATE DATABASE IF NOT EXISTS veterinaria
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE veterinaria;

CREATE TABLE clientes (
    id_cliente     INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    dni            CHAR(8)      NOT NULL UNIQUE,
    telefono       CHAR(9)      NOT NULL,
    email          VARCHAR(100),
    direccion      VARCHAR(150) NOT NULL,
    fecha_registro datetime default current_timestamp()
);

CREATE TABLE personal (
    id_empleado      INT AUTO_INCREMENT PRIMARY KEY,
    dni              CHAR(8)      NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    horario_atencion VARCHAR(50)  NOT NULL,
    rol              VARCHAR(30)  NOT NULL
);

CREATE TABLE mascotas (
    id_mascota       INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente       INT          NOT NULL,
    nombre           VARCHAR(60)  NOT NULL,
    especie          VARCHAR(40) not null,
    raza             VARCHAR(60) not null,
    fecha_nacimiento date not null,
    sexo             char(1) not null,
    esterilizado     BOOLEAN      NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE historial_peso (
    id_peso    INT AUTO_INCREMENT PRIMARY KEY,
    id_mascota INT           NOT NULL,
    peso       DECIMAL(6,2)  NOT NULL,
    fecha     datetime default current_timestamp(),
    FOREIGN KEY (id_mascota) REFERENCES mascotas(id_mascota)
);


CREATE TABLE citas (
    id_cita      INT AUTO_INCREMENT PRIMARY KEY,
    id_mascota   INT          NOT NULL,
    fecha   date  NOT NULL,
    hora time not null,
    id_empleado  int NOT NULL,
    tipo         VARCHAR(30) not null default 'Consulta',
    estado       VARCHAR(20)  NOT NULL DEFAULT 'Pendiente',
    FOREIGN KEY (id_mascota) REFERENCES mascotas(id_mascota),
    foreign key (id_empleado) references personal(id_empleado)
);

CREATE TABLE consultas (
    id_consulta           INT AUTO_INCREMENT PRIMARY KEY,
    id_mascota            INT          NOT NULL,
    id_cita               INT          NOT NULL,
    fecha_hora            datetime default current_timestamp(),
    motivo_consulta       VARCHAR(200),
    veterinario           VARCHAR(100),
    diagnostico           VARCHAR(300) NOT NULL,
    tratamiento_indicado  VARCHAR(300),
    peso                  DECIMAL(6,2),
    temperatura           DECIMAL(4,1),
    observaciones         VARCHAR(300),
    proxima_cita_sugerida VARCHAR(30),
    FOREIGN KEY (id_mascota) REFERENCES mascotas(id_mascota),
    FOREIGN KEY (id_cita)    REFERENCES citas(id_cita)
);

CREATE TABLE facturas (
    id_factura  INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente  INT           NOT NULL,
    id_consulta INT           NOT NULL,
    fecha       datetime default current_timestamp(),
    monto       DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(20)   NOT NULL,
    FOREIGN KEY (id_cliente)  REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_consulta) REFERENCES consultas(id_consulta)
);

SHOW TABLES;
