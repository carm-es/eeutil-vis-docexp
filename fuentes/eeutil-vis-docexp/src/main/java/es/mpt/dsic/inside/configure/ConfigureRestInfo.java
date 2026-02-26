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

package es.mpt.dsic.inside.configure;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import es.mpt.dsic.inside.config.EeutilApplicationDataConfig;
import es.mpt.dsic.inside.utils.exception.EeutilException;

@Component
public class ConfigureRestInfo {

  private static String baseUrlRestIgae;

  private static String tokenNameRestIgae;
  private static String tokenValueRestIgae;



  public static String getBaseUrlRestIgae() {
    return baseUrlRestIgae;
  }



  public static String getTokenNameRestIgae() {
    return tokenNameRestIgae;
  }



  public static String getTokenValueRestIgae() {
    return tokenValueRestIgae;
  }



  @PostConstruct
  public static void cargarRutasRelativas() throws EeutilException, IOException {

    // obtenemos el path de las propiedades externas.
    String path = EeutilApplicationDataConfig.CONFIG_PATH;

    // FileReader fReader= null;

    try (FileReader fReader = new FileReader(path + "/" + "eeutil.properties");) {
      // fReader=new FileReader(path+"/"+"eeutil.properties");

      Properties prop = new Properties();
      prop.load(fReader);

      baseUrlRestIgae = prop.getProperty("eeutil.igae.rs.base.url");
      tokenNameRestIgae = prop.getProperty("eeutil.igae.token.name");
      tokenValueRestIgae = prop.getProperty("eeutil.igae.token.value");
    }

  }

}
