package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.Miembro;
import ar.edu.utn.frba.dds.Usuario;
import ar.edu.utn.frba.dds.apimail.ServicioNotificacionMail;
import ar.edu.utn.frba.dds.repositorio.RepositorioMiembros;
import ar.edu.utn.frba.dds.repositorio.RepositorioUsuario;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.time.LocalDateTime;

public class RegistroController implements WithSimplePersistenceUnit {
  public ModelAndView registro(Request request, Response response) {
    return new ModelAndView(null, "registro.html.hbs");
  }
  public ModelAndView crearUsuario(Request request, Response response) {
    //TODO: ver los datos que te deberán llegar ahora y hacer que cuando creas un usuario se le asocie un miembro
    String email = request.queryParams("correo");
    String contrasenia = request.queryParams("contrasenia");
    String repiteContrasenia = request.queryParams("repetirContrasenia");
    String categoria=request.queryParams("repetirContrasenia");
    String nombre=request.queryParams("nombre");
    String apellido=request.queryParams("apellido");
    String celular=request.queryParams("celular");

    System.out.println(contrasenia+"  "+email);
    Usuario u=new Usuario(contrasenia, LocalDateTime.now(),email,5);
    withTransaction(() -> {
      RepositorioUsuario.instancia.agregar(u);
    });
    Usuario o= RepositorioUsuario.instancia.buscarPorMail(email);

    Session session = request.session(true);
    session.attribute("usuarioID", o.getUsuario());

    response.redirect("/listadoIncidentes?estado=activos");
    return null;
  }


  //Para la Pantalla de miembro

}
