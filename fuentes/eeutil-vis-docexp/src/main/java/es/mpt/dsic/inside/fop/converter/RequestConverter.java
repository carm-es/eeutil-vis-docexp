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

package es.mpt.dsic.inside.fop.converter;

import java.io.IOException;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenidoGeneric;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenidoMtom;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.ItemMtom;
import es.mpt.dsic.inside.ws.service.model.ListaItemGeneric;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionGeneric;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionMtom;

/**
 * Clase de utilidad que convierte los distintos tipos de peticiones (byte[], DataHandler) a una
 * petici�n gen�rica. Y del mismo modo, convierte una respuesta gen�rica a una respuesta del tipo
 * necesario (byte[], DataHandler).
 *
 */
public class RequestConverter {

  /**
   * Constructor privado para hacer de esta clase una clase de Utilidad e impedir que se instancie.
   * Por tanto, todos los m�todos de esta clase deben ser est�ticos
   */
  private RequestConverter() {}

  /**
   * Convierte una petici�n con documentos en byte[] a una petici�n gen�rica.
   * 
   * @param item Petici�n con documentos en byte[].
   * @param isPadre Si se est� procesando el padre de un item. Control para romper la reflexi�n
   *        infinita.
   * @return ItemGeneric Petici�n gen�rica.
   */
  public static ItemGeneric convertItemToItemGeneric(Item item, boolean isPadre) {
    ItemGeneric itemGeneric = new ItemGeneric();

    itemGeneric.setIdentificador(item.getIdentificador());
    itemGeneric.setNombre(item.getNombre());

    if (!isPadre && item.getPadre() != null) {
      itemGeneric.setPadre(convertItemToItemGeneric(item.getPadre(), true));
    }

    if (item.getHijos() != null && item.getHijos().getItems() != null
        && item.getHijos().getItems().size() > 0) {
      itemGeneric.setHijos(new ListaItemGeneric());
      itemGeneric.getHijos().setItems(new ArrayList<ItemGeneric>());
      for (Item itemHijo : item.getHijos().getItems()) {
        itemGeneric.getHijos().getItems().add(convertItemToItemGeneric(itemHijo, isPadre));
      }
    }

    if (item.getDocumentoContenido() != null
        && item.getDocumentoContenido().getBytesDocumento() != null) {
      itemGeneric.setDocumentoContenido(new DocumentoContenidoGeneric());
      itemGeneric.getDocumentoContenido().setDocumento(new ByteArrayDataSource(
          item.getDocumentoContenido().getBytesDocumento(), "application/octet-stream"));
      itemGeneric.getDocumentoContenido()
          .setMimeDocumento(item.getDocumentoContenido().getMimeDocumento());
    }

    if (item.getPropiedades() != null) {
      itemGeneric.setPropiedades(item.getPropiedades());
    }

    return itemGeneric;
  }

  /**
   * Convierte una petici�n con documentos en DataHandler a una petici�n gen�rica.
   * 
   * @param item Petici�n con documentos en DataHandler.
   * @param isPadre Si se est� procesando el padre de un item. Control para romper la reflexi�n
   *        infinita.
   * @return ItemGeneric Petici�n gen�rica.
   */
  public static ItemGeneric convertItemToItemGeneric(ItemMtom item, boolean isPadre) {
    ItemGeneric itemGeneric = new ItemGeneric();

    itemGeneric.setIdentificador(item.getIdentificador());
    itemGeneric.setNombre(item.getNombre());

    if (!isPadre && item.getPadre() != null) {
      itemGeneric.setPadre(convertItemToItemGeneric(item.getPadre(), true));
    }

    if (item.getHijos() != null && item.getHijos().getItems() != null
        && item.getHijos().getItems().size() > 0) {
      itemGeneric.setHijos(new ListaItemGeneric());
      itemGeneric.getHijos().setItems(new ArrayList<ItemGeneric>());
      for (ItemMtom itemHijo : item.getHijos().getItems()) {
        itemGeneric.getHijos().getItems().add(convertItemToItemGeneric(itemHijo, isPadre));
      }
    }

    if (item.getDocumentoContenido() != null
        && item.getDocumentoContenido().getBytesDocumento() != null) {
      itemGeneric.setDocumentoContenido(new DocumentoContenidoGeneric());
      itemGeneric.getDocumentoContenido()
          .setDocumento(item.getDocumentoContenido().getBytesDocumento().getDataSource());
    }

    if (item.getPropiedades() != null) {
      itemGeneric.setPropiedades(item.getPropiedades());
    }

    return itemGeneric;
  }

  /**
   * Convierte una respuesta gen�rica a una respuesta con documentos en byte[].
   * 
   * @param salidaGeneric Salida gen�rica.
   * @return SalidaVisualizacion Salida con documentos en byte[].
   * @throws IOException
   */
  public static SalidaVisualizacion convertSalidaVisualizacionGenericToSalidaVisualizacionBytes(
      SalidaVisualizacionGeneric salidaGeneric) throws IOException {
    SalidaVisualizacion salida = new SalidaVisualizacion();

    if (salidaGeneric.getDocumentoContenido() != null) {
      salida.setDocumentoContenido(new DocumentoContenido());
      salida.getDocumentoContenido()
          .setMimeDocumento(salidaGeneric.getDocumentoContenido().getMimeDocumento());
      salida.getDocumentoContenido().setBytesDocumento(IOUtils
          .toByteArray(salidaGeneric.getDocumentoContenido().getDocumento().getInputStream()));
    }

    return salida;
  }

  /**
   * Convierte una respuesta gen�rica a una respuesta con documentos en DataHandler.
   * 
   * @param salidaGeneric Salida gen�rica.
   * @return SalidaVisualizacion Salida con documentos en DataHandler.
   * @throws IOException
   */
  public static SalidaVisualizacionMtom convertSalidaVisualizacionGenericToSalidaVisualizacionMtom(
      SalidaVisualizacionGeneric salidaGeneric) throws IOException {
    SalidaVisualizacionMtom salida = new SalidaVisualizacionMtom();

    if (salidaGeneric.getDocumentoContenido() != null) {
      salida.setDocumentoContenido(new DocumentoContenidoMtom());
      salida.getDocumentoContenido()
          .setMimeDocumento(salidaGeneric.getDocumentoContenido().getMimeDocumento());
      salida.getDocumentoContenido()
          .setBytesDocumento(new DataHandler(salidaGeneric.getDocumentoContenido().getDocumento()));
    }

    return salida;
  }
}
