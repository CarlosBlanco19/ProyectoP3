USE sistemaReservas;

INSERT INTO usuario (pk_usuario, clave, rol) VALUES
    ('admin', '1234',  'ADMINISTRADOR'),
    ('ISS',   'ISS',   'FUNCIONARIO'),
    ('ISS-1', 'ISS-1', 'FUNCIONARIO'),
    ('ISS-2', 'ISS-2', 'FUNCIONARIO'),
    ('CEO',   'CEO',   'FUNCIONARIO');

INSERT INTO funcionario (fk_usuario, nombre, telefono) VALUES
    ('ISS',   'Carlos',  '88887777'),
    ('ISS-1', 'Anthony', '88889999'),
    ('ISS-2', 'Juan',    '88880000'),
    ('CEO',   'Deivert', '88886666');

INSERT INTO categoriaRecursos (pk_categoria, descripcion) VALUES
    (1, 'Laptop'),
    (2, 'Proyector'),
    (3, 'Sala de reuniones'),
    (4, 'Altavoz y Micrófono'),
    (5, 'Control de Aire Acondicionado');

INSERT INTO recurso (pk_recurso, descripcion, fk_categoria) VALUES
    ('HP-1',   'HP básica',                      1),
    ('HP-2',   'HP ProBook',                     1),
    ('DELL-1', 'Dell Latitude',                  1),
    ('PROY-1', 'Proyector Epson sala 1',         2),
    ('PROY-2', 'Proyector BenQ portátil',        2),
    ('SALA-1', 'Sala 1 primer piso (10 personas)', 3),
    ('SALA-2', 'Sala de juntas segundo piso',    3),
    ('ATV-1',  'Parlante JBL con micrófono',     4),
    ('AIR-1',  'Control A/C sala 1',             5);

-- Algunas reservas para que Calendarización, Actividades y Estadísticas tengan datos
INSERT INTO reserva (pk_reserva, fk_funcionario, actividad, fecha, hora_init, hora_final, estado) VALUES
    ('RES-000001', 'ISS',   'Reunión de planificación',   '2026-10-19', '08:00', '10:00', 'ACTIVA'),
    ('RES-000002', 'ISS-1', 'Capacitación de Excel',      '2026-10-21', '13:00', '15:00', 'ACTIVA'),
    ('RES-000003', 'ISS-2', 'Presentación de resultados', '2026-10-28', '09:00', '11:00', 'ACTIVA'),
    ('RES-000004', 'CEO',   'Reunión con clientes',       '2026-10-29', '10:00', '12:00', 'CANCELADA'),
    ('RES-000005', 'ISS',   'Revisión de presupuesto',    '2026-11-09', '08:00', '09:00', 'ACTIVA'),
    ('RES-000006', 'ISS-1', 'Taller de seguridad',        '2026-11-10', '14:00', '16:00', 'ACTIVA'),
    ('RES-000007', 'CEO',   'Junta directiva',            '2026-11-11', '09:00', '12:00', 'ACTIVA'),
    ('RES-000008', 'ISS-2', 'Entrevistas',                '2026-11-12', '10:00', '11:00', 'ACTIVA');

INSERT INTO reservaCategoria (fk_reserva, fk_categoria) VALUES
    ('RES-000001', 1), ('RES-000001', 3),
    ('RES-000002', 1), ('RES-000002', 2),
    ('RES-000003', 2), ('RES-000003', 3),
    ('RES-000004', 3),
    ('RES-000005', 1),
    ('RES-000006', 2), ('RES-000006', 4),
    ('RES-000007', 3), ('RES-000007', 2), ('RES-000007', 5),
    ('RES-000008', 3);

INSERT INTO reservaRecurso (fk_reserva, fk_recurso) VALUES
    ('RES-000001', 'HP-1'),   ('RES-000001', 'SALA-1'),
    ('RES-000002', 'HP-2'),   ('RES-000002', 'PROY-1'),
    ('RES-000003', 'PROY-1'), ('RES-000003', 'SALA-2'),
    ('RES-000004', 'SALA-1'),
    ('RES-000005', 'HP-1'),
    ('RES-000006', 'PROY-2'), ('RES-000006', 'ATV-1'),
    ('RES-000007', 'SALA-2'), ('RES-000007', 'PROY-1'), ('RES-000007', 'AIR-1'),
    ('RES-000008', 'SALA-1');
