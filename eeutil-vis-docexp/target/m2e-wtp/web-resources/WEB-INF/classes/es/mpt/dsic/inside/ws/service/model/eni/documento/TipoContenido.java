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


package es.mpt.dsic.inside.ws.service.model.eni.documento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for TipoContenido complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TipoContenido">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="ValorBinario" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *           &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoContenido",
    namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/contenido",
    propOrder = {"valorBinario", "referencia"})
public class TipoContenido {

  @XmlElement(name = "ValorBinario")
  protected byte[] valorBinario;
  protected String referencia;

  /**
   * Gets the value of the valorBinario property.
   * 
   * @return possible object is byte[]
   */
  public byte[] getValorBinario() {
    return valorBinario;
  }

  /**
   * Sets the value of the valorBinario property.
   * 
   * @param value allowed object is byte[]
   */
  public void setValorBinario(byte[] value) {
    this.valorBinario = ((byte[]) value);
  }

  /**
   * Gets the value of the referencia property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getReferencia() {
    return referencia;
  }

  /**
   * Sets the value of the referencia property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setReferencia(String value) {
    this.referencia = value;
  }

}
