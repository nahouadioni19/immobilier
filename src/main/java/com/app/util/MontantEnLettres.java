package com.app.util;

/**
 * Convertit un entier en lettres (français). Ex : 1 250 000 -> "un million deux cent cinquante mille".
 */
public final class MontantEnLettres {

    private static final String[] UNITES = {
            "zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
            "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize",
            "dix-sept", "dix-huit", "dix-neuf"
    };
    private static final String[] DIZAINES = {
            "", "", "vingt", "trente", "quarante", "cinquante", "soixante"
    };

    private MontantEnLettres() {
    }

    public static String convertir(long n) {
        if (n == 0) {
            return "zéro";
        }
        if (n < 0) {
            return "moins " + convertir(-n);
        }
        long milliards = n / 1_000_000_000L;
        long millions = (n / 1_000_000L) % 1000;
        long milliers = (n / 1000L) % 1000;
        long reste = n % 1000;

        StringBuilder sb = new StringBuilder();
        if (milliards > 0) {
            ajouter(sb, moinsDeMille(milliards, false) + " milliard" + (milliards > 1 ? "s" : ""));
        }
        if (millions > 0) {
            ajouter(sb, moinsDeMille(millions, false) + " million" + (millions > 1 ? "s" : ""));
        }
        if (milliers > 0) {
            ajouter(sb, milliers == 1 ? "mille" : moinsDeMille(milliers, false) + " mille");
        }
        if (reste > 0) {
            ajouter(sb, moinsDeMille(reste, true));
        }
        return sb.toString();
    }

    private static void ajouter(StringBuilder sb, String s) {
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(s);
    }

    /** @param finale true si le nombre termine l'expression (accord de "cents" et "quatre-vingts"). */
    private static String moinsDeMille(long n, boolean finale) {
        int h = (int) (n / 100);
        int r = (int) (n % 100);
        if (h == 0) {
            return moinsDeCent(r, finale);
        }
        String centaine = (h == 1) ? "cent" : UNITES[h] + " cent";
        if (r == 0) {
            return (h > 1 && finale) ? centaine + "s" : centaine;
        }
        return centaine + " " + moinsDeCent(r, finale);
    }

    private static String moinsDeCent(int n, boolean finale) {
        if (n < 20) {
            return UNITES[n];
        }
        int d = n / 10;
        int u = n % 10;
        switch (d) {
            case 7:
                return u == 1 ? "soixante et onze" : "soixante-" + UNITES[10 + u];
            case 8:
                return u == 0 ? (finale ? "quatre-vingts" : "quatre-vingt") : "quatre-vingt-" + UNITES[u];
            case 9:
                return "quatre-vingt-" + UNITES[10 + u];
            default:
                String base = DIZAINES[d];
                if (u == 0) {
                    return base;
                }
                return u == 1 ? base + " et un" : base + "-" + UNITES[u];
        }
    }
}