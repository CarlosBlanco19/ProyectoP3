Sistema de Reservas/
│
├── pom.xml
│
├── README.md
│
└── src/
    │
    ├── main/
    │   │
    │   ├── java/
    │   │   └── una.eif206.sistemadereservas/
    │   │       │
    │   │       ├── App.java
    │   │       │
    │   │       ├── model/
    │   │       │   ├── Usuario.java
    │   │       │   ├── Funcionario.java
    │   │       │   ├── Administrador.java
    │   │       │   ├── CategoriaRecurso.java
    │   │       │   ├── Recurso.java
    │   │       │   ├── Reserva.java
    │   │       │   └── Actividad.java
    │   │       │
    │   │       ├── controller/
    │   │       │   ├── LoginController.java
    │   │       │   ├── CambiarClaveController.java
    │   │       │   ├── ReservasController.java
    │   │       │   ├── FuncionariosController.java
    │   │       │   ├── CategoriasController.java
    │   │       │   ├── RecursosController.java
    │   │       │   ├── CalendarizacionRecursosController.java
    │   │       │   ├── CalendarizacionActividadesController.java
    │   │       │   └── EstadisticasController.java
    │   │       │
    │   │       ├── service/
    │   │       │   ├── UsuarioService.java
    │   │       │   ├── ReservaService.java
    │   │       │   ├── FuncionarioService.java
    │   │       │   ├── CategoriaService.java
    │   │       │   ├── RecursoService.java
    │   │       │   ├── CalendarizacionService.java
    │   │       │   ├── EstadisticasService.java
    │   │       │   ├── ReporteService.java
    │   │       │   └── IAService.java
    │   │       │
    │   │       ├── repository/
    │   │       │   ├── UsuarioRepository.java
    │   │       │   ├── FuncionarioRepository.java
    │   │       │   ├── CategoriaRepository.java
    │   │       │   ├── RecursoRepository.java
    │   │       │   └── ReservaRepository.java
    │   │       │
    │   │       ├── persistence/
    │   │       │   ├── XMLManager.java
    │   │       │   ├── XMLReader.java
    │   │       │   └── XMLWriter.java
    │   │       │
    │   │       ├── dto/
    │   │       │   └── ...
    │   │       │
    │   │       ├── util/
    │   │       │   ├── Validaciones.java
    │   │       │   ├── DateUtil.java
    │   │       │   └── Session.java
    │   │       │
    │   │       └── exception/
    │   │           ├── SistemaException.java
    │   │           ├── UsuarioException.java
    │   │           ├── ReservaException.java
    │   │           └── RecursoException.java
    │   │
    │   └── resources/
    │       │
    │       ├── view/
    │       │   ├── Login.fxml
    │       │   ├── CambiarClave.fxml
    │       │   ├── MenuPrincipal.fxml
    │       │   ├── Reservas.fxml
    │       │   ├── Funcionarios.fxml
    │       │   ├── Categorias.fxml
    │       │   ├── Recursos.fxml
    │       │   ├── CalendarizacionRecursos.fxml
    │       │   ├── CalendarizacionActividades.fxml
    │       │   └── Estadisticas.fxml
    │       │
    │       ├── css/
    │       │   └── estilos.css
    │       │
    │       ├── xml/
    │       │   ├── usuarios.xml
    │       │   ├── funcionarios.xml
    │       │   ├── categorias.xml
    │       │   ├── recursos.xml
    │       │   └── reservas.xml
    │       │
    │       └── images/
    │           └── ...
    │
    └── test/
        │
        ├── java/
        │   └── una.eif206.sistemadereservas/
        │       │
        │       ├── unit/
        │       │   ├── UsuarioTest.java
        │       │   ├── FuncionarioTest.java
        │       │   ├── CategoriaRecursoTest.java
        │       │   ├── RecursoTest.java
        │       │   └── ReservaTest.java
        │       │
        │       └── integration/
        │           ├── LoginIntegrationTest.java
        │           ├── ReservaIntegrationTest.java
        │           └── PersistenceIntegrationTest.java
        │
        └── resources/
            └── test-data/
