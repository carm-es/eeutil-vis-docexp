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

package es.mpt.dsic.inside.fop.converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import es.mpt.dsic.inside.exception.AfirmaException;
import es.mpt.dsic.inside.fop.model.documento.DocumentoEniConMAdicionales;
import es.mpt.dsic.inside.fop.model.documento.MetadatoAdicional;
import es.mpt.dsic.inside.fop.model.documento.TipoContenido;
import es.mpt.dsic.inside.fop.model.documento.TipoDocumento;
import es.mpt.dsic.inside.fop.model.documento.TipoFirmasElectronicas;
import es.mpt.dsic.inside.fop.model.documento.TipoMetadatos;
import es.mpt.dsic.inside.model.InformacionFirmaAfirma;
import es.mpt.dsic.inside.services.AfirmaService;
import es.mpt.dsic.inside.ws.service.model.adicionales.TipoMetadatosAdicionales;
import es.mpt.dsic.inside.ws.service.model.eni.documento.EnumeracionEstadoElaboracion;
import es.mpt.dsic.inside.ws.service.model.eni.documento.TipoDocumental;
import es.mpt.dsic.inside.ws.service.model.eni.documento.TipoEstadoElaboracion;
import es.mpt.dsic.inside.ws.service.model.eni.firma.TipoFirma;

@Component
public class DocumentConverter {

  public DocumentoEniConMAdicionales documentoEniConMAdicionalesWsToToModel(
      es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales input,
      AfirmaService afirmaService, String aplicacion) {
    DocumentoEniConMAdicionales retorno = new DocumentoEniConMAdicionales();
    retorno.setDocumento(tipoDocumentoWsToModel(input.getDocumento(), afirmaService, aplicacion));
    retorno.setMetadatoAdicional(metadatoAdicionaWsToModel(input.getMetadatosAdicionales()));
    return retorno;
  }

  private List<MetadatoAdicional> metadatoAdicionaWsToModel(TipoMetadatosAdicionales input) {
    List<MetadatoAdicional> retorno = new ArrayList<MetadatoAdicional>();
    if (input != null && CollectionUtils.isNotEmpty(input.getMetadatoAdicional())) {
      for (es.mpt.dsic.inside.ws.service.model.adicionales.MetadatoAdicional adicional : input
          .getMetadatoAdicional()) {
        MetadatoAdicional data = new MetadatoAdicional();
        data.setNombre(adicional.getNombre());
        data.setTipo(adicional.getTipo());
        data.setValor(adicional.getValor());
        retorno.add(data);
      }
    }
    return retorno;
  }

  private TipoDocumento tipoDocumentoWsToModel(
      es.mpt.dsic.inside.ws.service.model.eni.documento.TipoDocumento input,
      AfirmaService afirmaService, String aplicacion) {
    TipoDocumento retorno = new TipoDocumento();
    retorno.setContenido(contenidoWsToModel(input.getContenido()));
    retorno.setFirmas(firmasWsToModel(input.getFirmas().getFirma(), afirmaService, aplicacion));
    retorno.setMetadatos(tipoMetadatosWsToModel(input.getMetadatos()));
    return retorno;
  }

  private List<TipoFirmasElectronicas> firmasWsToModel(
      List<es.mpt.dsic.inside.ws.service.model.eni.firma.TipoFirmasElectronicas> input,
      AfirmaService afirmaService, String aplicacion) {
    List<TipoFirmasElectronicas> retorno = new ArrayList<TipoFirmasElectronicas>();
    if (CollectionUtils.isNotEmpty(input)) {
      for (es.mpt.dsic.inside.ws.service.model.eni.firma.TipoFirmasElectronicas firma : input) {
        if (!firma.getTipoFirma().equals(TipoFirma.TF_01)) {
          if (afirmaService != null) {
            try {
              InformacionFirmaAfirma infoAfirma = afirmaService.obtenerInformacionFirma(aplicacion,
                  firma.getContenidoFirma().getFirmaConCertificado().getFirmaBase64(), true, false,
                  true, null);
              TipoFirmasElectronicas data = new TipoFirmasElectronicas();
              data.setFechaFirma(infoAfirma.getFirmantes().get(0).getFecha());
              data.setTipoFirma(firma.getTipoFirma().value() + " - "
                  + infoAfirma.getTipoDeFirma().getTipoFirma());
              data.setFirmante(infoAfirma.getFirmantes().get(0).getNombre() + " "
                  + infoAfirma.getFirmantes().get(0).getApellido1() + " "
                  + infoAfirma.getFirmantes().get(0).getApellido2());
              retorno.add(data);
            } catch (AfirmaException e) {
              e.printStackTrace();
            }
          } else {
            TipoFirmasElectronicas data = new TipoFirmasElectronicas();
            data.setFechaFirma("2016/10/13 00:00:00");
            data.setTipoFirma("TF06");
            data.setFirmante("Pepito Perez");
            retorno.add(data);
          }
        } else {
          TipoFirmasElectronicas data = new TipoFirmasElectronicas();
          data.setFechaFirma(firma.getContenidoFirma().getCSV().getRegulacionGeneracionCSV());
          data.setTipoFirma("TF01 - CSV");
          data.setFirmante(firma.getContenidoFirma().getCSV().getValorCSV());
          retorno.add(data);
        }
      }
    }
    return retorno;
  }

  private TipoContenido contenidoWsToModel(
      es.mpt.dsic.inside.ws.service.model.eni.documento.TipoContenido input) {
    TipoContenido retorno = new TipoContenido();
    retorno.setValorBinario(input.getValorBinario());
    return retorno;
  }

  private TipoMetadatos tipoMetadatosWsToModel(
      es.mpt.dsic.inside.ws.service.model.eni.documento.TipoMetadatos input) {
    TipoMetadatos retorno = new TipoMetadatos();
    retorno.setEstadoElaboracion(estadoElaboracionWsToString(input.getEstadoElaboracion()));
    retorno.setFechaCaptura(fechaWsToModel(input.getFechaCaptura()));
    retorno.setIdentificador(input.getIdentificador());
    retorno.setOrigenCiudadanoAdministracion(
        converOrigenCiudadano(input.isOrigenCiudadanoAdministracion()));
    retorno.setTipoDocumental(tipoDocumentoWsToModel(input.getTipoDocumental()));
    retorno.setVersionNTI(input.getVersionNTI());
    retorno.setOrgano(input.getOrgano());
    return retorno;
  }

  private String tipoDocumentoWsToModel(TipoDocumental input) {
    String retorno = null;
    if (input.equals(TipoDocumental.TD_01)) {
      retorno = "Resoluci�n";
    }
    if (input.equals(TipoDocumental.TD_02)) {
      retorno = "Acuerdo";
    }
    if (input.equals(TipoDocumental.TD_03)) {
      retorno = "Contrato";
    }
    if (input.equals(TipoDocumental.TD_04)) {
      retorno = "Convenio";
    }
    if (input.equals(TipoDocumental.TD_05)) {
      retorno = "Declaraci�n";
    }
    if (input.equals(TipoDocumental.TD_06)) {
      retorno = "Comunicaci�n";
    }
    if (input.equals(TipoDocumental.TD_07)) {
      retorno = "Notificaci�n";
    }
    if (input.equals(TipoDocumental.TD_08)) {
      retorno = "Publicaci�n";
    }
    if (input.equals(TipoDocumental.TD_09)) {
      retorno = "Acuse de recibo";
    }
    if (input.equals(TipoDocumental.TD_10)) {
      retorno = "Acta";
    }
    if (input.equals(TipoDocumental.TD_11)) {
      retorno = "Certificado";
    }
    if (input.equals(TipoDocumental.TD_12)) {
      retorno = "Diligencia";
    }
    if (input.equals(TipoDocumental.TD_13)) {
      retorno = "Informe";
    }
    if (input.equals(TipoDocumental.TD_14)) {
      retorno = "Solicitud";
    }
    if (input.equals(TipoDocumental.TD_15)) {
      retorno = "Denuncia";
    }
    if (input.equals(TipoDocumental.TD_16)) {
      retorno = "Alegaci�n";
    }
    if (input.equals(TipoDocumental.TD_17)) {
      retorno = "Recursos";
    }
    if (input.equals(TipoDocumental.TD_18)) {
      retorno = "Comunicaci�n ciudadano";
    }
    if (input.equals(TipoDocumental.TD_19)) {
      retorno = "Factura";
    }
    if (input.equals(TipoDocumental.TD_20)) {
      retorno = "Otros incautados";
    }

    if (input.equals(TipoDocumental.TD_51)) {
      retorno = "Ley";
    }
    if (input.equals(TipoDocumental.TD_52)) {
      retorno = "Moci�n";
    }
    if (input.equals(TipoDocumental.TD_53)) {
      retorno = "Instrucci�n";
    }
    if (input.equals(TipoDocumental.TD_54)) {
      retorno = "Convocatoria";
    }
    if (input.equals(TipoDocumental.TD_55)) {
      retorno = "Orden del d�a";
    }
    if (input.equals(TipoDocumental.TD_56)) {
      retorno = "Informe de Ponencia";
    }
    if (input.equals(TipoDocumental.TD_57)) {
      retorno = "Dictamen de Comisi�n";
    }
    if (input.equals(TipoDocumental.TD_58)) {
      retorno = "Iniciativa legislativa";
    }
    if (input.equals(TipoDocumental.TD_59)) {
      retorno = "Pregunta";
    }
    if (input.equals(TipoDocumental.TD_60)) {
      retorno = "Interpelaci�n";
    }
    if (input.equals(TipoDocumental.TD_61)) {
      retorno = "Respuesta";
    }
    if (input.equals(TipoDocumental.TD_62)) {
      retorno = "Proposici�n no de ley";
    }
    if (input.equals(TipoDocumental.TD_63)) {
      retorno = "Enmienda";
    }
    if (input.equals(TipoDocumental.TD_64)) {
      retorno = "Propuesta de resoluci�n";
    }
    if (input.equals(TipoDocumental.TD_65)) {
      retorno = "Comparecencia";
    }
    if (input.equals(TipoDocumental.TD_66)) {
      retorno = "Solicitud de informaci�n";
    }
    if (input.equals(TipoDocumental.TD_67)) {
      retorno = "Escrito";
    }
    if (input.equals(TipoDocumental.TD_68)) {
      retorno = "Iniciativa legislativa";
    }
    if (input.equals(TipoDocumental.TD_69)) {
      retorno = "Petici�n";
    }
    if (input.equals(TipoDocumental.TD_99)) {
      retorno = "Otros";
    }
    return retorno;
  }

  private String converOrigenCiudadano(boolean input) {
    return input ? "Ciudadano" : "Administraci�n";
  }

  private String fechaWsToModel(XMLGregorianCalendar input) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    return sdf.format(input.toGregorianCalendar().getTime());
  }

  private String estadoElaboracionWsToString(TipoEstadoElaboracion input) {
    String retorno = null;
    if (input.getValorEstadoElaboracion().equals(EnumeracionEstadoElaboracion.EE_01)) {
      retorno = "Original";
    }
    if (input.getValorEstadoElaboracion().equals(EnumeracionEstadoElaboracion.EE_02)) {
      retorno = "Copia electr�nica aut�ntica con cambio de formato";
    }
    if (input.getValorEstadoElaboracion().equals(EnumeracionEstadoElaboracion.EE_03)) {
      retorno = "Copia electr�nica aut�ntica de documento papel";
    }
    if (input.getValorEstadoElaboracion().equals(EnumeracionEstadoElaboracion.EE_04)) {
      retorno = "Copia electr�nica parcial aut�ntica";
    }
    if (input.getValorEstadoElaboracion().equals(EnumeracionEstadoElaboracion.EE_99)) {
      retorno = "Otros";
    }
    return retorno;
  }

}
