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

package es.mpt.dsic.inside.ws.service.impl;

import java.util.List;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.mpt.dsic.inside.security.model.ApplicationLogin;
import es.mpt.dsic.inside.security.wss4j.CredentialUtil;
import es.mpt.dsic.inside.service.VisualizarService;
import es.mpt.dsic.inside.ws.service.EeUtilVisDocExpUserNameTokenService;
import es.mpt.dsic.inside.ws.service.exception.InSideException;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.Plantilla;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales;

@Service("eeUtilVisDocExpUserNameTokenService")
@WebService(endpointInterface = "es.mpt.dsic.inside.ws.service.EeUtilVisDocExpUserNameTokenService")
@SOAPBinding(style = Style.RPC, parameterStyle = ParameterStyle.BARE, use = Use.LITERAL)
public class EeUtilVisDocExpUserNameTokenServiceImpl
    implements EeUtilVisDocExpUserNameTokenService {

  protected final static Log logger =
      LogFactory.getLog(EeUtilVisDocExpUserNameTokenServiceImpl.class);

  @Autowired
  VisualizarService visualizarService;

  @Resource
  private WebServiceContext wsContext;

  @Autowired
  CredentialUtil credentialUtil;

  @Override
  public SalidaVisualizacion visualizar(Item item, OpcionesVisualizacion opciones)
      throws InSideException {
    return visualizarService.visualizar(credentialUtil.getCredentialEeutilUserToken(wsContext),
        item, opciones);
  }

  @Override
  public SalidaVisualizacion visualizarContenidoOriginal(Item item) throws InSideException {
    return visualizarService
        .visualizarContenidoOriginal(credentialUtil.getCredentialEeutilUserToken(wsContext), item);
  }

  @Override
  public SalidaVisualizacion visualizarDocumentoConPlantilla(
      DocumentoEniConMAdicionales docEniAdicionales, String plantilla) throws InSideException {
    return visualizarService.visualizarDocumentoConPlantilla(
        credentialUtil.getCredentialEeutilUserToken(wsContext), docEniAdicionales, plantilla);
  }

  @Override
  public List<Plantilla> obtenerPlantillas(ApplicationLogin info) throws InSideException {
    return visualizarService.obtenerPlantillas(info);
  }

  @Override
  public List<Plantilla> asociarPlantilla(String idPlantilla, byte[] plantilla)
      throws InSideException {
    return visualizarService.asociarPlantilla(
        credentialUtil.getCredentialEeutilUserToken(wsContext), idPlantilla, plantilla);
  }

  @Override
  public List<Plantilla> eliminarPlantilla(String idPlantilla) throws InSideException {
    return visualizarService
        .eliminarPlantilla(credentialUtil.getCredentialEeutilUserToken(wsContext), idPlantilla);
  }

}
