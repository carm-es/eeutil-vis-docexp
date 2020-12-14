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

package es.mpt.dsic.inside.visualizacion;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;

public class PieVisualizacionDesigner implements PdfPTableDesigner {

  private float widthTabla;
  private Font fontTexto = FontFactory.getFont(BaseFont.HELVETICA, 9);

  public PieVisualizacionDesigner(float widthTabla) {

    this.widthTabla = widthTabla;

  }

  @Override
  public PdfPTable designTable(Object[] params) throws ContentNotAddedException {

    if (params.length != 1) {
      throw new ContentNotAddedException("El numero de argumentos tiene que ser igual a 1");
    }
    if (!(params[0] instanceof String)) {
      throw new ContentNotAddedException(
          new IllegalArgumentException("El primer argumento debe ser un String"));
    }

    PdfPTable tabla = new PdfPTable(1);

    tabla.setTotalWidth(widthTabla);

    tabla.addCell(celdaTextoPie((String) params[0]));

    return tabla;

  }

  private PdfPCell celdaTextoPie(String texto) {
    PdfPCell cellTxt = new PdfPCell();
    cellTxt.setBorder(PdfPCell.NO_BORDER);
    cellTxt.setPaddingTop(25f);

    Paragraph p = new Paragraph(texto, fontTexto);
    p.setLeading(8f);
    p.setAlignment(Element.ALIGN_RIGHT);
    cellTxt.addElement(p);
    return cellTxt;

  }

}
