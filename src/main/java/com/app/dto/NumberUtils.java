package com.app.dto;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

    public static String formatFrench(Long montant) {
        if (montant == null) return "0";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator('\u00A0'); // espace insécable
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(montant);
    }
}
