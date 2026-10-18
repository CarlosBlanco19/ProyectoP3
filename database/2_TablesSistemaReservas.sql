USE sistemaReservas;

CREATE TABLE usuario(
	pk_usuario varchar(30) NOT NULL,
    clave varchar(50) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'FUNCIONARIO') NOT NULL,
    PRIMARY KEY(pk_usuario)
);

CREATE TABLE funcionario(
	fk_usuario varchar(30) NOT NULL,
    nombre varchar(50) NOT NULL,
    telefono varchar(30) NOT NULL,
    PRIMARY KEY(fk_usuario),
    FOREIGN KEY(fk_usuario) REFERENCES usuario(pk_usuario)
);

CREATE TABLE categoriaRecursos(
	pk_categoria INT NOT NULL auto_increment,
    descripcion varchar(150) NOT NULL,
    PRIMARY KEY(pk_categoria)
);

CREATE TABLE recurso(
	pk_recurso varchar(30) NOT NULL,
	descripcion varchar(150) NOT NULL,
	fk_categoria INT NOT NULL,
	PRIMARY KEY(pk_recurso),
    FOREIGN KEY (fk_categoria) REFERENCES categoriaRecursos (pk_categoria)
);

CREATE TABLE reserva(
	pk_reserva VARCHAR(30) NOT NULL,
    fk_funcionario Varchar(30) NOT NULL,
    actividad Varchar(100) NOT NULL,
    fecha DATE NOT NULL,
    hora_init TIME NOT NULL,
    hora_final TIME NOT NULL,
    estado ENUM('ACTIVA', 'CANCELADA') NOT NULL DEFAULT 'ACTIVA',
    PRIMARY KEY(pk_reserva),
    FOREIGN KEY(fk_funcionario) REFERENCES funcionario(fk_usuario)
);

CREATE TABLE reservaCategoria(
	fk_reserva VARCHAR(30) NOT NULL,
    fk_categoria INT NOT NULL,
    PRIMARY KEY(fk_reserva, fk_categoria),
    FOREIGN KEY (fk_reserva) REFERENCES reserva (pk_reserva),
    FOREIGN KEY (fk_categoria) REFERENCES categoriaRecursos (pk_categoria)
);

CREATE TABLE reservaRecurso(
	fk_reserva Varchar(30) NOT NULL,
    fk_recurso Varchar(30) NOT NULL,
    PRIMARY KEY (fk_reserva, fk_recurso),
    FOREIGN KEY (fk_reserva) REFERENCES reserva (pk_reserva),
    FOREIGN KEY (fk_recurso) REFERENCES recurso (pk_recurso)
);