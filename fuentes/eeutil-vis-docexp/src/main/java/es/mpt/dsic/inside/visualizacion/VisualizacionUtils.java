/*
 * Copyright (C) 2012-13 MINHAP, Gobierno de España This program is licensed and may be used,
 * modified and redistributed under the terms of the European Public License (EUPL), either version
 * 1.1 or (at your option) any later version as soon as they are approved by the European
 * Commission. Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * more details. You should have received a copy of the EUPL1.1 license along with this program; if
 * not, you may find it at http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package es.mpt.dsic.inside.visualizacion;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.Propiedad;

public class VisualizacionUtils {


  /**
   * Recorre la estructura de árbol en preorden para obtener una lista ordenada de Items de
   * Visualizacion. *
   * 
   * @param nodo Estructura en árbol que contiene la información que quiere foliarse.
   * 
   * @return Lista ordenada de VisualizacionItem en el orden en que se quieren imprimir.
   */
  public static List<VisualizacionItem> obtenerVisualizacionItems(Item nodo) throws Exception {

    List<VisualizacionItem> visualizacionItems = new ArrayList<VisualizacionItem>();

    int prof = 0;

    int[] marcadores = {0};

    addVisualizacionItemToList(nodo, prof, visualizacionItems, marcadores);

    /*
     * for (FoliadoItem f : foliadoItems) { System.out.println(f); }
     */


    return visualizacionItems;

  }

  /**
   * Añade un nuevo elemento a la lista pasada como parámetro.
   * 
   * @param nodo
   * @param prof
   * @param lista
   */
  public static void addVisualizacionItemToList(final Item nodo, int prof,
      List<VisualizacionItem> lista, int[] marcadores) throws Exception {

    VisualizacionItem ii = new VisualizacionItem();
    ii.setNombre(nodo.getNombre());
    ii.setProfundidad(prof + 1);
    ii.setPropiedades(listaPropiedadesToEntryList(nodo));
    ii.setMarcadores(marcadores);
    lista.add(ii);

    if (nodo.getHijos() != null) {
      prof++;

      for (Item hijo : nodo.getHijos().getItems()) {
        int[] nuevosMarcadores = Arrays.copyOf(marcadores, marcadores.length + 1);
        nuevosMarcadores[nuevosMarcadores.length - 1] =
            nodo.getHijos().getItems().indexOf(hijo) + 1;
        addVisualizacionItemToList(hijo, prof, lista, nuevosMarcadores);
      }
    }
  }

  public static List<Entry<String, String>> listaPropiedadesToEntryList(final Item nodo) {
    List<Entry<String, String>> entryList = null;
    if (nodo.getPropiedades() != null) {
      entryList = new ArrayList<Entry<String, String>>();
      for (Propiedad propiedad : nodo.getPropiedades().getPropiedades()) {
        Entry<String, String> entry =
            new AbstractMap.SimpleEntry<String, String>(propiedad.getClave(), propiedad.getValor());
        entryList.add(entry);
      }
    }

    return entryList;

  }

}
