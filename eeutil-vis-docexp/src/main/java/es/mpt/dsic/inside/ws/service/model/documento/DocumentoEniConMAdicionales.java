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


package es.mpt.dsic.inside.ws.service.model.documento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.mpt.dsic.inside.ws.service.model.adicionales.TipoMetadatosAdicionales;
import es.mpt.dsic.inside.ws.service.model.eni.documento.TipoDocumento;


/**
 * <p>
 * Java class for DocumentoEniConMAdicionales complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentoEniConMAdicionales">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento}documento"/>
 *         &lt;element name="metadatosAdicionales" type="{https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/metadatosAdicionales}TipoMetadatosAdicionales" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoEniConMAdicionales", propOrder = {"documento", "metadatosAdicionales"})
public class DocumentoEniConMAdicionales {

  @XmlElement(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento", required = true)
  protected TipoDocumento documento;
  protected TipoMetadatosAdicionales metadatosAdicionales;

  /**
   * Gets the value of the documento property.
   * 
   * @return possible object is {@link TipoDocumento }
   * 
   */
  public TipoDocumento getDocumento() {
    return documento;
  }

  /**
   * Sets the value of the documento property.
   * 
   * @param value allowed object is {@link TipoDocumento }
   * 
   */
  public void setDocumento(TipoDocumento value) {
    this.documento = value;
  }

  /**
   * Gets the value of the metadatosAdicionales property.
   * 
   * @return possible object is {@link TipoMetadatosAdicionales }
   * 
   */
  public TipoMetadatosAdicionales getMetadatosAdicionales() {
    return metadatosAdicionales;
  }

  /**
   * Sets the value of the metadatosAdicionales property.
   * 
   * @param value allowed object is {@link TipoMetadatosAdicionales }
   * 
   */
  public void setMetadatosAdicionales(TipoMetadatosAdicionales value) {
    this.metadatosAdicionales = value;
  }

}
