package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.Miembro;
import ar.edu.utn.frba.dds.Usuario;
import ar.edu.utn.frba.dds.apimail.ApiMail;
import ar.edu.utn.frba.dds.apimail.ServicioNotificacionMail;
import ar.edu.utn.frba.dds.repositorio.RepositorioMiembros;
import ar.edu.utn.frba.dds.repositorio.RepositorioUsuario;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;
import java.util.HashMap;
import java.util.Map;

public class MiembroController implements WithSimplePersistenceUnit{

    public ModelAndView configurarMiembro(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");
        if(usuarioID==null){
            response.redirect("/");
        }
        Usuario u=RepositorioUsuario.instancia.getById(Integer.parseInt(usuarioID));
        Map<String, Object> apertura = new HashMap<>();

        apertura.put("nombre",u.getMiembro().getNombre());
        apertura.put("email",u.getMiembro().getEmail());
        apertura.put("celular",u.getMiembro().getCelular());
        apertura.put("apellido",u.getMiembro().getApellido());


        return new ModelAndView(null, "configurar_miembro.html.hbs");
    }

    public ModelAndView terminarRegistro(Request request, Response response){
        String email = request.queryParams("correo");
        String celular = request.queryParams("celular");
        String nombre = request.queryParams("nombre");
        String apellido = request.queryParams("apellido");




        System.out.println(nombre+" "+apellido+" "+email+" "+celular);
        Usuario o= RepositorioUsuario.instancia.buscarPorMail(email);
        withTransaction(() -> {

            if(o.getMiembro()==null){
                Miembro m= new Miembro(nombre,apellido,email,Long.parseLong(celular),new ServicioNotificacionMail(new ApiMail()));
                RepositorioMiembros.instancia.agregar(m);
                Miembro a= RepositorioMiembros.instancia.buscarPorMail(email);
                o.setMiembro(a);
                RepositorioUsuario.instancia.agregar(o);
            }else{
                Miembro a= RepositorioMiembros.instancia.buscarPorMail(email);
                a.setNombre(nombre);
                a.setApellido(apellido);
                a.setCelular(Long.parseLong(celular));
                RepositorioMiembros.instancia.agregar(a);
            }

        });

        response.redirect("/listadoIncidentes");
        return null;

    }
}
