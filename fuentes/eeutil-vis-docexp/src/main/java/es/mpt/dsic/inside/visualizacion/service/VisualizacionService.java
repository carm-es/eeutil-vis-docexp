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

package es.mpt.dsic.inside.visualizacion.service;

import es.mpt.dsic.inside.services.AfirmaService;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenidoGeneric;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;

public interface VisualizacionService {

  public DocumentoContenidoGeneric doVisualizable(ItemGeneric item,
      OpcionesVisualizacion oVisualizacion, AfirmaService afirmaService, String idAplicacion)
      throws EeutilException;

  public DocumentoContenido doVisualizableOriginal(Item item, AfirmaService afirmaService,
      String idAplicacion) throws EeutilException;

}
