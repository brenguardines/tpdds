package ar.edu.utn.frba.dds.main;

import ar.edu.utn.frba.dds.controller.*;
import ar.edu.utn.frba.dds.spark.template.handlebars.HandlebarsTemplateEngine;
import spark.Spark;

public class Routes{

  public static void main(String[] args) {
    Spark.port(8000);

    Spark.staticFileLocation("/public");

    HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
    RegistroController registroController = new RegistroController();
    IncidenteController incidenteController = new IncidenteController();
    MiembroController miembroController = new MiembroController();
    UsuarioController usuarioController = new UsuarioController();
    ComunidadController comunidadController = new ComunidadController();


    Spark.get("/",usuarioController::inicioSesion, engine);
    Spark.post("/", usuarioController::startLogin, engine);

    Spark.get("/listadoIncidentes",incidenteController::listarIncidentes, engine);

    Spark.get("/aperturaIncidentes",incidenteController::pantallaAbrirIncidente, engine);
    Spark.post("/crearIncidente",incidenteController::crearIncidente, engine);

    Spark.get("/cierreIncidentes",incidenteController::pantallaCerrarIncidente, engine);
    Spark.post("/cierreIncidentes",incidenteController::buscarIncidente, engine);
    Spark.post("/cerrar",incidenteController::cerrarIncidente, engine);

    Spark.get("/rankingIncidentes",incidenteController::listarRankings, engine);

    Spark.get("/revisionDeIncidentes",incidenteController::sugerenciaIncidente, engine);

    Spark.get("/registro", registroController::registro, engine);

    Spark.post("/crearUsuario",registroController::crearUsuario,engine);

    Spark.get("/configurarMiembro",miembroController::configurarMiembro,engine);

    Spark.post("/guardar_e_iniciar", miembroController::terminarRegistro, engine);

    Spark.get("/listadoComunidades",comunidadController::listarComunidades,engine);

    Spark.get("/crearComunidad",comunidadController::crearComunidad,engine);
    Spark.post("/crearComunidad",comunidadController::creandoComunidad,engine);

    Spark.get("/detalleComunidad",comunidadController::detalleComunidad,engine);
    Spark.post("/eliminarComunidad",comunidadController::eliminarComunidad,engine);

    Spark.post("/agregarMiembroComunidad",comunidadController::agregarMiembroComunidad,engine);

    Spark.post("/agregarServicioComunidad",comunidadController::agregarServicioComunidad,engine);

    Spark.post("/eliminarMiembroComunidad",comunidadController::eliminarMiembroComunidad,engine);

    Spark.post("/eliminarServicioComunidad",comunidadController::eliminarMiembroComunidad,engine);

/*
    Spark.get("/agregarMiembro",comunidadController::agregarMiembroPantalla,engine);
    Spark.post("/agregarMiembro",comunidadController::agregarMiembro,engine);
*/






    //Spark.init();
  }



}
