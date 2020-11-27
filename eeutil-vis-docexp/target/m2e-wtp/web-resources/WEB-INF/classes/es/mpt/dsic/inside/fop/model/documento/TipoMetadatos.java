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


package es.mpt.dsic.inside.fop.model.documento;

import java.util.List;

public class TipoMetadatos {

  protected String versionNTI;
  protected String identificador;
  protected List<String> organo;
  protected String fechaCaptura;
  protected String origenCiudadanoAdministracion;
  protected String estadoElaboracion;
  protected String tipoDocumental;

  public String getVersionNTI() {
    return versionNTI;
  }

  public void setVersionNTI(String versionNTI) {
    this.versionNTI = versionNTI;
  }

  public String getIdentificador() {
    return identificador;
  }

  public void setIdentificador(String identificador) {
    this.identificador = identificador;
  }

  public List<String> getOrgano() {
    return organo;
  }

  public void setOrgano(List<String> organo) {
    this.organo = organo;
  }

  public String getFechaCaptura() {
    return fechaCaptura;
  }

  public void setFechaCaptura(String fechaCaptura) {
    this.fechaCaptura = fechaCaptura;
  }

  public String getOrigenCiudadanoAdministracion() {
    return origenCiudadanoAdministracion;
  }

  public void setOrigenCiudadanoAdministracion(String origenCiudadanoAdministracion) {
    this.origenCiudadanoAdministracion = origenCiudadanoAdministracion;
  }

  public String getEstadoElaboracion() {
    return estadoElaboracion;
  }

  public void setEstadoElaboracion(String estadoElaboracion) {
    this.estadoElaboracion = estadoElaboracion;
  }

  public String getTipoDocumental() {
    return tipoDocumental;
  }

  public void setTipoDocumental(String tipoDocumental) {
    this.tipoDocumental = tipoDocumental;
  }

}
