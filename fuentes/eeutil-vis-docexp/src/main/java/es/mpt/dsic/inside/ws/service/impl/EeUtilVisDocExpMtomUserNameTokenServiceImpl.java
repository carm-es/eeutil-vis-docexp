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
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.WebServiceContext;

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
import es.mpt.dsic.inside.security.wss4j.CredentialUtil;
import es.mpt.dsic.inside.service.VisualizarService;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.ws.service.EeUtilVisDocExpMtomUserNameTokenService;
import es.mpt.dsic.inside.ws.service.exception.InSideException;
import es.mpt.dsic.inside.ws.service.model.EstadoInfo;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.ItemMtom;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionGeneric;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionMtom;

@Service("eeUtilVisDocExpMtomUserNameTokenService")
@WebService(
    endpointInterface = "es.mpt.dsic.inside.ws.service.EeUtilVisDocExpMtomUserNameTokenService")
@SOAPBinding(style = Style.RPC, parameterStyle = ParameterStyle.BARE, use = Use.LITERAL)
public class EeUtilVisDocExpMtomUserNameTokenServiceImpl
    implements EeUtilVisDocExpMtomUserNameTokenService {

  private static final String ERROR_MSG = "ERROR";

  protected static final Log logger =
      LogFactory.getLog(EeUtilVisDocExpMtomUserNameTokenServiceImpl.class);

  @Autowired
  VisualizarService visualizarService;

  @Resource
  private WebServiceContext wsContext;

  @Autowired
  CredentialUtil credentialUtil;

  @Autowired
  EeUtilServiceImplBusiness eeUtilServiceImplBusiness;

  @Override
  @AuditEntryPointAnnotation(nombreApp = "EEUTIL-VIS-DOCEXP")
  public SalidaVisualizacionMtom visualizar(ItemMtom item, OpcionesVisualizacion opciones)
      throws InSideException {
    try {

      ApplicationLogin login = credentialUtil.getCredentialEeutilUserToken(wsContext);

      // Se transforma la petici�n Mtom a una gen�rica
      ItemGeneric itemGeneric = RequestConverter.convertItemToItemGeneric(item, false);

      // Se realiza el proceso de visualizaci�n
      SalidaVisualizacionGeneric salidaGeneric =
          eeUtilServiceImplBusiness.visualizar(itemGeneric, opciones, login.getIdApplicacion());

      // Se transforma la salida gen�rica a salida Mtom
      return RequestConverter
          .convertSalidaVisualizacionGenericToSalidaVisualizacionMtom(salidaGeneric);
    } catch (EeutilException e) {
      visualizarMDC(item, opciones);
      logger.error(e.getMessage(), e);
      throw new InSideException(e.getMessage(),
          new EstadoInfo(ERROR_MSG, ERROR_MSG, e.getMessage()), e);
    } catch (Exception e) {
      visualizarMDC(item, opciones);
      logger.error(e.getMessage(), e);
      throw new InSideException(e.getMessage(),
          new EstadoInfo(ERROR_MSG, ERROR_MSG, e.getMessage()), e);
    }
  }

  private void visualizarMDC(ItemMtom item, OpcionesVisualizacion opciones) {
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
}
