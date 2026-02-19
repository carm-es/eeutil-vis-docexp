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

package es.mpt.dsic.inside.ws.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.mpt.dsic.inside.aop.AuditEntryPointAnnotation;
import es.mpt.dsic.inside.fop.converter.RequestConverter;
import es.mpt.dsic.inside.reflection.MapUtil;
import es.mpt.dsic.inside.reflection.UtilReflection;
import es.mpt.dsic.inside.security.model.ApplicationLogin;
import es.mpt.dsic.inside.service.VisualizarService;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.ws.service.EeUtilService;
import es.mpt.dsic.inside.ws.service.exception.InSideException;
import es.mpt.dsic.inside.ws.service.model.EstadoInfo;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.Plantilla;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionGeneric;
import es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales;

@Service("eeUtilService")
@WebService(endpointInterface = "es.mpt.dsic.inside.ws.service.EeUtilService")
@SOAPBinding(style = Style.RPC, parameterStyle = ParameterStyle.BARE, use = Use.LITERAL)
public class EeUtilServiceImpl implements EeUtilService {

  private static final String ERROR_MSG = "ERROR";

  protected static final Log logger = LogFactory.getLog(EeUtilServiceImpl.class);

  @Autowired
  VisualizarService visualizarService;

  @Autowired
  EeUtilServiceImplBusiness eeUtilServiceImplBusiness;

  @Override
  @AuditEntryPointAnnotation(nombreApp = "EEUTIL-VIS-DOCEXP")
  public SalidaVisualizacion visualizar(ApplicationLogin info, Item item,
      OpcionesVisualizacion opciones) throws InSideException {
    try {
      // Se transforma la petici�n byte[] a una gen�rica
      ItemGeneric itemGeneric = RequestConverter.convertItemToItemGeneric(item, false);

      // Se realiza el proceso de visualizaci�n
      SalidaVisualizacionGeneric salidaGeneric =
          eeUtilServiceImplBusiness.visualizar(itemGeneric, opciones, info.getIdApplicacion());

      // Se transforma la salida gen�rica a salida byte[]
      return RequestConverter
          .convertSalidaVisualizacionGenericToSalidaVisualizacionBytes(salidaGeneric);
    } catch (EeutilException e) {
      ingresarMDCAppUUID(info.getIdApplicacion());
      visualizarMDC(item, opciones);
      logger.error(e.getMessage(), e);
      throw new InSideException(e.getMessage(), e);
    } catch (Exception e) {
      ingresarMDCAppUUID(info.getIdApplicacion());
      visualizarMDC(item, opciones);
      logger.error(e.getMessage(), e);
      throw new InSideException(e.getMessage(),
          new EstadoInfo(ERROR_MSG, ERROR_MSG, e.getMessage()), e);
    }
  }

  /**
   * @param item
   * @param opciones
   */
  private void visualizarMDC(Item item, OpcionesVisualizacion opciones) {
    try {
      Object[] objs = new Object[2];
      String[] strP = new String[] {"item", "opciones"};
      objs[0] = item;
      objs[1] = opciones;

      Map<String, String> mParametros =
          UtilReflection.getInstance().extractMultipleDataPermitted(null, objs, strP);
      String resultado = MapUtil.mapToString(mParametros);
      MDC.put("ExtraParaM", resultado);

    } catch (IOException e1) {

      // si falla palante

    }
  }

  @Override
  @AuditEntryPointAnnotation(nombreApp = "EEUTIL-VIS-DOCEXP")
  public SalidaVisualizacion visualizarContenidoOriginal(ApplicationLogin info, Item item)
      throws InSideException {
    try {
      return eeUtilServiceImplBusiness.visualizarContenidoOriginal(info.getIdApplicacion(), item);
    } catch (EeutilException e) {
      ingresarMDCAppUUID(info.getIdApplicacion());
      visualizarContenidoOriginalMDC(item);
      logger.error(e.getMessage(), e);
      throw new InSideException(e.getMessage(),
          new EstadoInfo(ERROR_MSG, ERROR_MSG, e.getMessage()), e);
    } catch (Exception e) {
      ingresarMDCAppUUID(info.getIdApplicacion());
      visualizarContenidoOriginalMDC(item);
      logger.error(e.getMessage(), e);
      throw new InSideException(e.getMessage(),
          new EstadoInfo(ERROR_MSG, ERROR_MSG, e.getMessage()), e);
    }
  }

  /**
   * @param item
   */
  private void visualizarContenidoOriginalMDC(Item item) {
    try {
      Object[] objs = new Object[1];
      String[] strP = new String[] {"item"};
      objs[0] = item;

      Map<String, String> mParametros =
          UtilReflection.getInstance().extractMultipleDataPermitted(null, objs, strP);
      String resultado = MapUtil.mapToString(mParametros);
      MDC.put("ExtraParaM", resultado);

    } catch (IOException e1) {

      // si falla palante

    }
  }

  @Override
  @Deprecated
  public SalidaVisualizacion visualizarDocumentoConPlantilla(ApplicationLogin info,
      DocumentoEniConMAdicionales docEniAdicionales, String plantilla) throws InSideException {
    return visualizarService.visualizarDocumentoConPlantilla(info, docEniAdicionales, plantilla);
  }

  @Override
  @Deprecated
  public List<Plantilla> obtenerPlantillas(ApplicationLogin info) throws InSideException {
    return visualizarService.obtenerPlantillas(info);
  }

  @Override
  @Deprecated
  public List<Plantilla> asociarPlantilla(ApplicationLogin info, String idPlantilla,
      byte[] plantilla) throws InSideException {
    return visualizarService.asociarPlantilla(info, idPlantilla, plantilla);
  }

  @Override
  @Deprecated
  public List<Plantilla> eliminarPlantilla(ApplicationLogin info, String idPlantilla)
      throws InSideException {
    return visualizarService.eliminarPlantilla(info, idPlantilla);
  }


  private void ingresarMDCAppUUID(String idApp) {
    MDC.put("idApli", idApp);
    MDC.put("uUId", UUID.randomUUID().toString());
  }

}
