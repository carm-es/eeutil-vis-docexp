
package es.mpt.dsic.inside.ws.service.model.documento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import es.mpt.dsic.inside.ws.service.model.adicionales.TipoMetadatosAdicionales;
import es.mpt.dsic.inside.ws.service.model.eni.documento.TipoDocumento;


/**
 * <p>Java class for DocumentoEniConMAdicionales complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
@XmlType(name = "DocumentoEniConMAdicionales", propOrder = {
    "documento",
    "metadatosAdicionales"
})
public class DocumentoEniConMAdicionales {

    @XmlElement(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento", required = true)
    protected TipoDocumento documento;
    protected TipoMetadatosAdicionales metadatosAdicionales;

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link TipoDocumento }
     *     
     */
    public TipoDocumento getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoDocumento }
     *     
     */
    public void setDocumento(TipoDocumento value) {
        this.documento = value;
    }

    /**
     * Gets the value of the metadatosAdicionales property.
     * 
     * @return
     *     possible object is
     *     {@link TipoMetadatosAdicionales }
     *     
     */
    public TipoMetadatosAdicionales getMetadatosAdicionales() {
        return metadatosAdicionales;
    }

    /**
     * Sets the value of the metadatosAdicionales property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoMetadatosAdicionales }
     *     
     */
    public void setMetadatosAdicionales(TipoMetadatosAdicionales value) {
        this.metadatosAdicionales = value;
    }

}
