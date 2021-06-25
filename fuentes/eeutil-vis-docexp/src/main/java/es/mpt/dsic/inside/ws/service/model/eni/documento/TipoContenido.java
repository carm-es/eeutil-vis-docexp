
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
