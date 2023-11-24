package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.*;
import ar.edu.utn.frba.dds.notificacion.Notificacion;
import ar.edu.utn.frba.dds.repositorio.*;
import ar.edu.utn.frba.dds.servicio.Servicio;
import com.google.gson.JsonObject;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncidenteController implements WithSimplePersistenceUnit {

    public ModelAndView listarIncidentes(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");
        if(usuarioID==null){
            response.redirect("/");
        }
        System.out.println(usuarioID);

        String filtro = request.queryParams("estado");
        System.out.println(filtro);

        if(filtro==null){
            response.redirect("/listadoIncidentes?estado=activos");
        }

        List<Incidente> lista = RepositorioIncidentes.instancia.buscarPorEstado(filtro);

        Map<String, Object> listado = new HashMap<>();

        List<IncidenteVista> listaFormatteada = new ArrayList<>();

        lista.forEach(l -> {
            String fechaApertura = "";
            String fechaCierre = "";
            if(l.getApertura() != null){
                fechaApertura = this.darFormato(l.getApertura());
            }
            if(l.getCierre() != null){
                fechaCierre = this.darFormato(l.getCierre());
            }

            IncidenteVista elementoFormateado = new IncidenteVista();
            elementoFormateado.setId(l.getId());
            elementoFormateado.setObservacion(l.getObservacion());
            elementoFormateado.setMiembroApertura(l.getMiembroApertura());
            elementoFormateado.setEstablecimiento(l.getEstablecimiento());
            elementoFormateado.setServicio(l.getServicio());
            elementoFormateado.setVigente(l.getVigente());
            elementoFormateado.setApertura(fechaApertura);
            elementoFormateado.setCierre(fechaCierre);

            listaFormatteada.add(elementoFormateado);
        });

        listado.put("incidentes", listaFormatteada);

        return new ModelAndView(listado, "listadoIncidentes.html.hbs");
    }

    public static String darFormato(LocalDateTime date) {
        DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        return date.format(CUSTOM_FORMATTER);
    }

    public ModelAndView listarRankings(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");
        System.out.println(usuarioID);
        if(usuarioID==null){
            response.redirect("/");
        }
        String filtro=request.queryParams("valor");
        String csvFilePath="";
        if(filtro==null){
            return new ModelAndView(null, "rankingIncidentes.html.hbs");
        }
        switch(Integer.parseInt(filtro)){
            case 1: csvFilePath="RankingPromedio.csv";
                break;
            case 2: csvFilePath="RankingPorCantidad.csv";
                break;
            default:break;
        }
        List<JsonObject> jsonList = new ArrayList<>();
        String[] vector={"Nombre","Tipo","Resultado"};
        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord csvRecord : csvParser) {
                JsonObject jsonObject = new JsonObject();

                for (int i = 0; i < csvRecord.size(); i++) {
                    jsonObject.addProperty(vector[i], csvRecord.get(i));
                }

                jsonList.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonList.remove(0);
        Map<String, Object> apertura = new HashMap<>();

        apertura.put("rankings",jsonList);

        return new ModelAndView(apertura, "rankingIncidentes.html.hbs");
    }

    public ModelAndView pantallaAbrirIncidente(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");
        if(usuarioID==null){
            response.redirect("/");
        }
        String selectedEstablecimiento=request.queryParams("establecimiento");

        if(selectedEstablecimiento==null){
            List<Establecimiento> lista= RepositorioEstablecimiento.instancia.listar();
            Map<String, Object> apertura = new HashMap<>();
            apertura.put("establecimientos",lista);

            return new ModelAndView(apertura, "aperturaIncidentes.html.hbs");
        }else{
            List<Establecimiento>lista=RepositorioEstablecimiento.instancia.listar();
            List<Servicio> listaServicio= RepositorioServicio.instancia.listarSegunEstablecimiento(Integer.parseInt(selectedEstablecimiento));

            Map<String, Object> apertura = new HashMap<>();

            apertura.put("establecimientos",lista);
            apertura.put("servicios",listaServicio);
            apertura.put("establecimiento",selectedEstablecimiento);

            return new ModelAndView(apertura, "aperturaIncidentes.html.hbs");
        }

    }
    public ModelAndView crearIncidente(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");

        String observacion=request.queryParams("observacion");
        String idEstablecimiento=request.queryParams("establecimiento");
        String idServicio=request.queryParams("servicio");

        if(RepositorioUsuario.instancia.buscarPorMail(usuarioID).getMiembro()==null){
            response.redirect("/configurarMiembro");
        }
        if(idServicio==null){
            return new ModelAndView(null, "aperturaIncidentes.html.hbs");
        }

        withTransaction(() -> {
            RepositorioIncidentes.instancia.agregar(
                new Incidente(RepositorioServicio.instancia.buscar(Integer.parseInt(idServicio))
                    ,observacion,
                    RepositorioEstablecimiento.instancia.buscar(Integer.parseInt(idEstablecimiento)),
                    RepositorioUsuario.instancia.getById(Integer.parseInt(usuarioID)).getMiembro()));
        });


        return new ModelAndView(null, "listadoIncidentes.html.hbs");
    }


    public ModelAndView pantallaCerrarIncidente(Request request, Response response){
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");
        System.out.println(usuarioID);
        if(usuarioID==null){
            response.redirect("/");
        }

        return new ModelAndView(null, "cierreIncidentes.html.hbs");
    }
    public ModelAndView buscarIncidente(Request request, Response response) {
        String respuesta=request.queryParams("servicio");

        List<Incidente> list=RepositorioIncidentes.instancia.buscarPorServicio(respuesta);

        if(list.size()==0){
            return new ModelAndView(null, "cierreIncidentes.html.hbs");
        }
        Map<String, Object> apertura = new HashMap<>();
        apertura.put("incidentes",list);

        return new ModelAndView(apertura, "cierreIncidentes.html.hbs");
    }

    public ModelAndView cerrarIncidente(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");

        String[] incidenteIds = request.queryParamsValues("incidenteId");
        //String id = request.queryParams("incidenteId");
        System.out.println("==================================================");
        System.out.println("ESta es la id "+ incidenteIds.length);
        System.out.println("==================================================");

        for (int j=0 ; j < incidenteIds.length ; j++){
            Incidente i=RepositorioIncidentes.instancia.buscar(Integer.parseInt(incidenteIds[j]));
            i.cerrarIncidente();

            withTransaction(() -> {
                RepositorioIncidentes.instancia.agregar(i);
            });
        }

        /*List<Incidente> list=RepositorioIncidentes.instancia.buscarPorServicio(i.getServicio().getNombre());
        if(list.size()==0){
            return new ModelAndView(null, "cierreIncidentes.html.hbs");
        }

        Map<String, Object> apertura = new HashMap<>();
        apertura.put("incidentes",list);*/

        return new ModelAndView(null, "cierreIncidentes.html.hbs");
    }

    public ModelAndView sugerenciaIncidente(Request request, Response response) {
        Session session = request.session();
        String usuarioID = session.attribute("usuarioID");
        if(usuarioID==null){
            response.redirect("/");
        }

        Usuario u=RepositorioUsuario.instancia.buscarPorMail(usuarioID);

        if(u.getMiembro()==null){
            response.redirect("/configurarMiembro");
        }
        Miembro m =RepositorioMiembros.instancia.buscar(u.getMiembro().getId());
        Posicion p=m.obtenerposicion();
        System.out.println("==================================================================================================");
        System.out.println("Mi posicion X "+p.getLongitud()+" e Y"+p.getLatitud());
        System.out.println("==================================================================================================");
        List<Establecimiento> listadoE=RepositorioEstablecimiento.instancia.listar();

        List<Establecimiento> filtrados=listadoE.stream().filter(e->m.estaCercaDeEstablecimiento(p,e.getUbicacion())).toList();
        System.out.println("Cantidad "+filtrados.size());

        List<Incidente> lista=RepositorioIncidentes.instancia.listar();
        System.out.println("Cantidad incidentes "+lista.size());

        List<Incidente> cercanos=new ArrayList<>();
        for(Establecimiento e:filtrados){
            for(Incidente i:lista){
                if(i.getEstablecimiento().getId()==e.getId() && i.getVigente()){
                    cercanos.add(i);
                }
            }
        }
        System.out.println("Cantidad Incidentes cercanos "+cercanos.size());

        //List<Notificacion> notificaciones=m.getNotificaciones().stream().filter(c->c.getEntregado()).toList();

        Map<String, Object> apertura = new HashMap<>();
        apertura.put("notificaciones",cercanos);

        return new ModelAndView(apertura, "revisionDeIncidentes.html.hbs");
    }
}