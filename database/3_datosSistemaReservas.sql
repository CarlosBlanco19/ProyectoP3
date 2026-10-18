USE sistemaReservas;

INSERT INTO usuario(pk_usuario, clave, rol) VALUES ('admin','1234', 'ADMINISTRADOR'),
	('ISS',   'ISS',   'FUNCIONARIO'),
    ('ISS-1', 'ISS-1', 'FUNCIONARIO'),
    ('ISS-2', 'ISS-2', 'FUNCIONARIO'),
    ('CEO',   'CEO',   'FUNCIONARIO');

INSERT INTO funcionario(fk_usuario, nombre, telefono) VALUES ('ISS', 'Carlos', '88887777'),
	('ISS-1', 'Anthony', '88889999'),
    ('ISS-2', 'Juan', '88880000'),
    ('CEO', 'Deivert', '88886666');
    
INSERT INTO categoriaRecursos (descripcion) VALUES ('Laptop'), ('Proyector');

INSERT INTO recurso (pk_recurso, descripcion, fk_categoria) VALUES ('HP-1', 'HP basica', 1);