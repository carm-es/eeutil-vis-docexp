
package es.mpt.dsic.inside.ws.service.model.eni.documento;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the es.mpt.dsic.inside.ws.service.model.eni.documento package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation
 * for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model
 * groups. Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

  private final static QName _Documento_QNAME =
      new QName("https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento", "documento");
  private final static QName _Metadatos_QNAME =
      new QName("https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/metadatos", "metadatos");
  private final static QName _Contenido_QNAME =
      new QName("https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/contenido", "contenido");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes
   * for package: es.mpt.dsic.inside.ws.service.model.eni.documento
   * 
   */
  public ObjectFactory() {}

  /**
   * Create an instance of {@link TipoEstadoElaboracion }
   * 
   */
  public TipoEstadoElaboracion createTipoEstadoElaboracion() {
    return new TipoEstadoElaboracion();
  }

  /**
   * Create an instance of {@link TipoContenido }
   * 
   */
  public TipoContenido createTipoContenido() {
    return new TipoContenido();
  }

  /**
   * Create an instance of {@link TipoDocumento }
   * 
   */
  public TipoDocumento createTipoDocumento() {
    return new TipoDocumento();
  }

  /**
   * Create an instance of {@link TipoMetadatos }
   * 
   */
  public TipoMetadatos createTipoMetadatos() {
    return new TipoMetadatos();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link TipoDocumento }{@code >}}
   * 
   */
  @XmlElementDecl(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento",
      name = "documento")
  public JAXBElement<TipoDocumento> createDocumento(TipoDocumento value) {
    return new JAXBElement<TipoDocumento>(_Documento_QNAME, TipoDocumento.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link TipoMetadatos }{@code >}}
   * 
   */
  @XmlElementDecl(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/metadatos",
      name = "metadatos")
  public JAXBElement<TipoMetadatos> createMetadatos(TipoMetadatos value) {
    return new JAXBElement<TipoMetadatos>(_Metadatos_QNAME, TipoMetadatos.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link TipoContenido }{@code >}}
   * 
   */
  @XmlElementDecl(namespace = "https://ssweb.seap.minhap.es/Eeutil/XSD/v1.0/documento/contenido",
      name = "contenido")
  public JAXBElement<TipoContenido> createContenido(TipoContenido value) {
    return new JAXBElement<TipoContenido>(_Contenido_QNAME, TipoContenido.class, null, value);
  }

}
