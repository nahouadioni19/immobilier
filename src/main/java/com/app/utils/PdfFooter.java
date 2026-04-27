package com.app.utils;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class PdfFooter extends PdfPageEventHelper {

    Font footerFont = new Font(Font.HELVETICA, 8);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte cb = writer.getDirectContent();

        String text = "Page " + writer.getPageNumber();

        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase(text, footerFont),
                (document.right() + document.left()) / 2,
                document.bottom() - 10,
                0
        );
    }
}