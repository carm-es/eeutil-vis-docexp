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

package es.mpt.dsic.inside.ws.service.model;


import java.io.Serializable;


public class ItemGeneric implements Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = 5270528123456889972L;

  private String identificador;
  private String nombre;
  private ItemGeneric padre;
  private ListaItemGeneric hijos;
  private DocumentoContenidoGeneric documentoContenido;
  private ListaPropiedades propiedades;


  public ItemGeneric() {
    super();
  }

  public String getIdentificador() {
    return identificador;
  }

  public void setIdentificador(String identificador) {
    this.identificador = identificador;
  }

  public ItemGeneric(String nombre) {
    this.nombre = nombre;
  }

  public ItemGeneric(String nombre, ItemGeneric padre) {
    this.nombre = nombre;
    this.padre = padre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public ItemGeneric getPadre() {
    return padre;
  }

  public void setPadre(ItemGeneric padre) {
    this.padre = padre;
  }

  public ListaItemGeneric getHijos() {
    return hijos;
  }

  public void setHijos(ListaItemGeneric hijos) {
    this.hijos = hijos;
  }

  public DocumentoContenidoGeneric getDocumentoContenido() {
    return this.documentoContenido;
  }

  public void setDocumentoContenido(DocumentoContenidoGeneric documentoContenido) {
    this.documentoContenido = documentoContenido;
  }

  public void addHijo(ItemGeneric hijo) {
    if (hijos == null) {
      hijos = new ListaItemGeneric();
    }

    hijos.getItems().add(hijo);
  }

  public ListaPropiedades getPropiedades() {
    return propiedades;
  }

  public void setPropiedades(ListaPropiedades propiedades) {
    this.propiedades = propiedades;
  }
}
