
package es.mpt.dsic.inside.ws.service.model.eni.documento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.mpt.dsic.inside.ws.service.model.eni.firma.Firmas;


/**
 * <p>Java class for TipoDocumento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TipoDocumento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/contenido}contenido"/>
 *         &lt;element ref="{https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/metadatos}metadatos"/>
 *         &lt;element ref="{https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/firma}firmas" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoDocumento", namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento", propOrder = {
    "contenido",
    "metadatos",
    "firmas"
})
public class TipoDocumento {

    @XmlElement(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/contenido", required = true)
    protected TipoContenido contenido;
    @XmlElement(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/metadatos", required = true)
    protected TipoMetadatos metadatos;
    @XmlElement(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/firma")
    protected Firmas firmas;

    /**
     * Gets the value of the contenido property.
     * 
     * @return
     *     possible object is
     *     {@link TipoContenido }
     *     
     */
    public TipoContenido getContenido() {
        return contenido;
    }

    /**
     * Sets the value of the contenido property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoContenido }
     *     
     */
    public void setContenido(TipoContenido value) {
        this.contenido = value;
    }

    /**
     * Gets the value of the metadatos property.
     * 
     * @return
     *     possible object is
     *     {@link TipoMetadatos }
     *     
     */
    public TipoMetadatos getMetadatos() {
        return metadatos;
    }

    /**
     * Sets the value of the metadatos property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoMetadatos }
     *     
     */
    public void setMetadatos(TipoMetadatos value) {
        this.metadatos = value;
    }

    /**
     * La firma es obligatoria para el documento administrativo electrónico y para todo aquel documento electrónico susceptible de ser incorporado en un expediente electrónico.
     * 
     * @return
     *     possible object is
     *     {@link Firmas }
     *     
     */
    public Firmas getFirmas() {
        return firmas;
    }

    /**
     * Sets the value of the firmas property.
     * 
     * @param value
     *     allowed object is
     *     {@link Firmas }
     *     
     */
    public void setFirmas(Firmas value) {
        this.firmas = value;
    }

}
