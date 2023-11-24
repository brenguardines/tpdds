package ar.edu.utn.frba.dds.parser;

import ar.edu.utn.frba.dds.Entidad;
import com.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Rankingscsv {
  public static Rankingscsv rank;
  public List<String[]> entidades;

  public Rankingscsv() {
    entidades = new ArrayList<>();
  }

  public void crearRankingscsv(Map<Entidad,Integer> ranking, String nombre) {
    File f = new File(crearPath(nombre));

    try (FileWriter fileWriter = new FileWriter(f);
         CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Nombre", "Tipo", "Resultado"))) {
      List<Entidad> entidades=ranking.keySet().stream().toList();
      List<Integer> resultados=ranking.values().stream().toList();
      int i=0;
      for (Entidad entidad : entidades) {

        csvPrinter.printRecord(entidad.getNombre(), entidad.getTipo(), resultados.get(i));
        i++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static Rankingscsv getInstance() {
    if (rank == null) {
      return new Rankingscsv();
    }
    return rank;
  }

  public String crearPath(String nombre) {
    return ("./" + nombre + ".csv");
  }

  public String[] fila(Entidad e) {
    String[] w = {e.getNombre(), e.getTipo()};
    return w;
  }
}


