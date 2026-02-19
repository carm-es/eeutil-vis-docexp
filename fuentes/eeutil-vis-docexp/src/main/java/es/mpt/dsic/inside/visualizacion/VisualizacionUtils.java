/*
 * Copyright (C) 2025, Gobierno de España This program is licensed and may be used, modified and
 * redistributed under the terms of the European Public License (EUPL), either version 1.1 or (at
 * your option) any later version as soon as they are approved by the European Commission. Unless
 * required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and more details. You
 * should have received a copy of the EUPL1.1 license along with this program; if not, you may find
 * it at http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package es.mpt.dsic.inside.visualizacion;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.Propiedad;

public class VisualizacionUtils {


  private VisualizacionUtils() {

  }

  /**
   * Recorre la estructura de arbol en preorden para obtener una lista ordenada de Items de
   * Visualizacion. *
   * 
   * @param nodo Estructura en arbol que contiene la informacion que quiere foliarse.
   * 
   * @return Lista ordenada de VisualizacionItem en el orden en que se quieren imprimir.
   */
  public static List<VisualizacionItem> obtenerVisualizacionItems(ItemGeneric nodo)
      throws EeutilException {

    List<VisualizacionItem> visualizacionItems = new ArrayList<>();
    try {


      int prof = 0;

      int[] marcadores = {0};

      addVisualizacionItemToList(nodo, prof, visualizacionItems, marcadores);

      /*
       * for (FoliadoItem f : foliadoItems) { System.out.println(f); }
       */


    }

    catch (EeutilException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    }

    return visualizacionItems;


  }

  /**
   * A�ade un nuevo elemento a la lista pasada como parametro.
   * 
   * @param nodo
   * @param prof
   * @param lista
   */
  public static void addVisualizacionItemToList(final ItemGeneric nodo, int prof,
      List<VisualizacionItem> lista, int[] marcadores) throws EeutilException {

    try {

      VisualizacionItem ii = new VisualizacionItem();
      ii.setNombre(nodo.getNombre());
      ii.setProfundidad(prof + 1);
      ii.setPropiedades(listaPropiedadesToEntryList(nodo));
      ii.setMarcadores(marcadores);
      lista.add(ii);

      if (nodo.getHijos() != null) {
        prof++;

        for (ItemGeneric hijo : nodo.getHijos().getItems()) {
          int[] nuevosMarcadores = Arrays.copyOf(marcadores, marcadores.length + 1);
          nuevosMarcadores[nuevosMarcadores.length - 1] =
              nodo.getHijos().getItems().indexOf(hijo) + 1;
          addVisualizacionItemToList(hijo, prof, lista, nuevosMarcadores);
        }
      }

    } catch (EeutilException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    }
  }

  /**
   * Convierte la lista de propiedades de una petici�n a una lista de Entry.
   * 
   * @param nodo Petici�n.
   * @return List<Entry<String, String>> Lista convertida.
   */
  public static List<Entry<String, String>> listaPropiedadesToEntryList(final ItemGeneric nodo) {
    List<Entry<String, String>> entryList = null;
    if (nodo.getPropiedades() != null) {
      entryList = new ArrayList<>();
      for (Propiedad propiedad : nodo.getPropiedades().getPropiedades()) {
        Entry<String, String> entry =
            new AbstractMap.SimpleEntry<>(propiedad.getClave(), propiedad.getValor());
        entryList.add(entry);
      }
    }

    return entryList;

  }

}
