package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.Usuario;
import ar.edu.utn.frba.dds.repositorio.RepositorioUsuario;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

public class UsuarioController {
    public ModelAndView inicioSesion(Request request, Response response){
        System.out.println("pase por el inicio");
        return new ModelAndView(null,"login.html.hbs");
    }
    public ModelAndView startLogin(Request request, Response response) {
        String username = request.queryParams("correo");
        String password = request.queryParams("contrasenia");

        Usuario o= RepositorioUsuario.instancia.buscarPorMail(username);

        Session session = request.session(true);

        session.attribute("usuarioID", Integer.toString(o.getId()));

        if (o.getContrasenia().compareTo(password)==0) {

            response.redirect("/listadoIncidentes?estado=activos");
        } else {

            System.out.println("Credenciales incorrectas. Por favor, intenta de nuevo.");
        }
        return new ModelAndView(null,"login.html.hbs");

    }
}
