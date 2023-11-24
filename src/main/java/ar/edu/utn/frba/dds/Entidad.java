package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.rankings.CriterioRankings;
import ar.edu.utn.frba.dds.repositorio.RepositorioEntidades;
import ar.edu.utn.frba.dds.repositorio.RepositorioIncidentes;
import ar.edu.utn.frba.dds.repositorio.RepositorioMiembros;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
//son las lineas de transporte y los establecimientos
public class Entidad {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  int id;
  private String nombre;
  private String tipo;

  @OneToMany
  @JoinColumn(name = "entidad_id")
  private List<Establecimiento> establecimientos = new ArrayList<>();

  @Transient
  private RepositorioEntidades repoEntidades = new RepositorioEntidades();

  public Entidad(String nombre, String tipo) {
    this.nombre = nombre;
    this.tipo = tipo;
    this.repoEntidades.agregarEntidad(this);
  }

  public Entidad() {

  }

  public String getNombre() {
    return nombre;
  }

  public String getTipo() {
    return tipo;
  }

  public void setId(int i) {
    this.id = i;
  }

  public List<Establecimiento> getEstablecimientos() {
    return establecimientos;
  }

  public int getId() {
    return id;
  }

  public void crearIncidente(Incidente i, RepositorioIncidentes incidentes, RepositorioMiembros miembros) {
    incidentes.getIncidentes().add(i);
    miembros.notificarMiembrosDeInteres(i);
  }
}
