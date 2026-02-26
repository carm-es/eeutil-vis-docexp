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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import es.mpt.dsic.inside.exception.base.DataWsEntradaBase;
import es.mpt.dsic.inside.exception.interfaz.IMDCAble;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "itemMtom",
    propOrder = {"identificador", "nombre", "padre", "hijos", "documentoContenido", "propiedades"})
public class ItemMtom extends DataWsEntradaBase implements Serializable, IMDCAble {


  /**
   * 
   */
  private static final long serialVersionUID = 5270528123456889972L;

  @XmlID
  @XmlElement(required = true, name = "identificador")
  private String identificador;
  // @XmlID
  @XmlElement(required = true, name = "nombre")
  private String nombre;
  @XmlElement(required = false, name = "padre", type = ItemMtom.class)
  @XmlIDREF
  private ItemMtom padre;
  @XmlElement(required = false, name = "hijos")
  private ListaItemMtom hijos;
  @XmlElement(required = false, name = "documentoContenido")
  private DocumentoContenidoMtom documentoContenido;
  @XmlElement(required = false, name = "propiedadesItem")
  private ListaPropiedades propiedades;


  public ItemMtom() {
    super();
  }

  public String getIdentificador() {
    return identificador;
  }

  public void setIdentificador(String identificador) {
    this.identificador = identificador;
  }

  public ItemMtom(String nombre) {
    this.nombre = nombre;
  }

  public ItemMtom(String nombre, ItemMtom padre) {
    this.nombre = nombre;
    this.padre = padre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public ItemMtom getPadre() {
    return padre;
  }

  public void setPadre(ItemMtom padre) {
    this.padre = padre;
  }

  public ListaItemMtom getHijos() {
    return hijos;
  }

  public void setHijos(ListaItemMtom hijos) {
    this.hijos = hijos;
  }

  public DocumentoContenidoMtom getDocumentoContenido() {
    return this.documentoContenido;
  }

  public void setDocumentoContenido(DocumentoContenidoMtom documentoContenido) {
    this.documentoContenido = documentoContenido;
  }

  public void addHijo(ItemMtom hijo) {
    if (hijos == null) {
      hijos = new ListaItemMtom();
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
