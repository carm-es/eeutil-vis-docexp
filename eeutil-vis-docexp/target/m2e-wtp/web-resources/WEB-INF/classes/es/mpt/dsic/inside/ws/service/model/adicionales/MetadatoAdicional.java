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


package es.mpt.dsic.inside.ws.service.model.adicionales;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for MetadatoAdicional complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MetadatoAdicional">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nombre" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadatoAdicional", propOrder = {"valor"})
public class MetadatoAdicional {

  @XmlElement(required = true)
  protected Object valor;
  @XmlAttribute(name = "tipo")
  protected String tipo;
  @XmlAttribute(name = "nombre", required = true)
  protected String nombre;

  /**
   * Gets the value of the valor property.
   * 
   * @return possible object is {@link Object }
   * 
   */
  public Object getValor() {
    return valor;
  }

  /**
   * Sets the value of the valor property.
   * 
   * @param value allowed object is {@link Object }
   * 
   */
  public void setValor(Object value) {
    this.valor = value;
  }

  /**
   * Gets the value of the tipo property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * Sets the value of the tipo property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setTipo(String value) {
    this.tipo = value;
  }

  /**
   * Gets the value of the nombre property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getNombre() {
    return nombre;
  }

  /**
   * Sets the value of the nombre property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setNombre(String value) {
    this.nombre = value;
  }

}
