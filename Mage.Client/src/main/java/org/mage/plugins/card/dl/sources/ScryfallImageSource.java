package org.mage.plugins.card.dl.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.mage.plugins.card.images.CardDownloadData;

/**
 * @author Quercitron, JayDi85
 *
 */
public enum ScryfallImageSource implements CardImageSource {

    instance;

    private final Set<String> supportedSets;

    ScryfallImageSource() {
        supportedSets = new LinkedHashSet<>();
        // supportedSets.add("PTC"); //
        supportedSets.add("LEA");
        supportedSets.add("LEB");
        supportedSets.add("2ED");
        supportedSets.add("ARN");
        supportedSets.add("ATQ");
        supportedSets.add("3ED");
        supportedSets.add("LEG");
        supportedSets.add("DRK");
        supportedSets.add("FEM");
        supportedSets.add("4ED");
        supportedSets.add("ICE");
        supportedSets.add("CHR");
        supportedSets.add("HML");
        supportedSets.add("ALL");
        supportedSets.add("MIR");
        supportedSets.add("VIS");
        supportedSets.add("5ED");
        supportedSets.add("POR");
        supportedSets.add("WTH");
        supportedSets.add("TMP");
        supportedSets.add("STH");
        supportedSets.add("EXO");
        supportedSets.add("P02");
        supportedSets.add("UGL");
        supportedSets.add("USG");
        supportedSets.add("DD3DVD");
        supportedSets.add("DD3EVG");
        supportedSets.add("DD3GVL");
        supportedSets.add("DD3JVC");

        supportedSets.add("ULG");
        supportedSets.add("6ED");
        supportedSets.add("UDS");
        supportedSets.add("PTK");
        supportedSets.add("S99");
        supportedSets.add("MMQ");
        // supportedSets.add("BRB");Battle Royale Box Set
        supportedSets.add("NEM");
        supportedSets.add("S00");
        supportedSets.add("PCY");
        supportedSets.add("INV");
        // supportedSets.add("BTD"); // Beatdown Boxset
        supportedSets.add("PLS");
        supportedSets.add("7ED");
        supportedSets.add("APC");
        supportedSets.add("ODY");
        // supportedSets.add("DKM"); // Deckmasters 2001
        supportedSets.add("TOR");
        supportedSets.add("JUD");
        supportedSets.add("ONS");
        supportedSets.add("LGN");
        supportedSets.add("SCG");
        supportedSets.add("8ED");
        supportedSets.add("MRD");
        supportedSets.add("DST");
        supportedSets.add("5DN");
        supportedSets.add("CHK");
        supportedSets.add("UNH");
        supportedSets.add("BOK");
        supportedSets.add("SOK");
        supportedSets.add("9ED");
        supportedSets.add("RAV");
        supportedSets.add("GPT");
        supportedSets.add("DIS");
        supportedSets.add("CSP");
        supportedSets.add("TSP");
        supportedSets.add("TSB");
        supportedSets.add("PLC");
        supportedSets.add("FUT");
        supportedSets.add("10E");
        supportedSets.add("MED");
        supportedSets.add("LRW");
        supportedSets.add("EVG");
        supportedSets.add("MOR");
        supportedSets.add("SHM");
        supportedSets.add("EVE");
        supportedSets.add("DRB");
        supportedSets.add("ME2");
        supportedSets.add("ALA");
        supportedSets.add("DD2");
        supportedSets.add("CON");
        supportedSets.add("DDC");
        supportedSets.add("ARB");
        supportedSets.add("M10");
        // supportedSets.add("TD0"); // Magic Online Deck Series
        supportedSets.add("V09");
        supportedSets.add("HOP");
        supportedSets.add("ME3");
        supportedSets.add("ZEN");
        supportedSets.add("DDD");
        supportedSets.add("H09");
        supportedSets.add("WWK");
        supportedSets.add("DDE");
        supportedSets.add("ROE");
        supportedSets.add("DPA");
        supportedSets.add("ARC");
        supportedSets.add("M11");
        supportedSets.add("V10");
        supportedSets.add("DDF");
        supportedSets.add("SOM");
        // supportedSets.add("TD0"); // Commander Theme Decks
        supportedSets.add("PD2");
        supportedSets.add("ME4");
        supportedSets.add("MBS");
        supportedSets.add("DDG");
        supportedSets.add("NPH");
        supportedSets.add("CMD");
        supportedSets.add("M12");
        supportedSets.add("V11");
        supportedSets.add("DDH");
        supportedSets.add("ISD");
        supportedSets.add("PD3");
        supportedSets.add("DKA");
        supportedSets.add("DDI");
        supportedSets.add("AVR");
        supportedSets.add("PC2");
        supportedSets.add("M13");
        supportedSets.add("V12");
        supportedSets.add("DDJ");
        supportedSets.add("RTR");
        supportedSets.add("CM1");
        // supportedSets.add("TD2"); // Duel Decks: Mirrodin Pure vs. New Phyrexia
        supportedSets.add("GTC");
        supportedSets.add("DDK");
        supportedSets.add("DGM");
        supportedSets.add("MMA");
        supportedSets.add("M14");
        supportedSets.add("V13");
        supportedSets.add("DDL");
        supportedSets.add("THS");
        supportedSets.add("C13");
        supportedSets.add("BNG");
        supportedSets.add("DDM");
        supportedSets.add("JOU");
        // supportedSets.add("MD1"); // Modern Event Deck
        supportedSets.add("CNS");
        supportedSets.add("VMA");
        supportedSets.add("M15");
        supportedSets.add("V14");
        supportedSets.add("DDN");
        supportedSets.add("KTK");
        supportedSets.add("C14");
        // supportedSets.add("DD3"); // Duel Decks Anthology
        supportedSets.add("FRF");
        supportedSets.add("DDO");
        supportedSets.add("DTK");
        supportedSets.add("TPR");
        supportedSets.add("MM2");
        supportedSets.add("ORI");
        supportedSets.add("V15");
        supportedSets.add("DDP");
        supportedSets.add("BFZ");
        supportedSets.add("EXP");
        supportedSets.add("C15");
        // supportedSets.add("PZ1"); // Legendary Cube
        supportedSets.add("OGW");
        supportedSets.add("DDQ");
        supportedSets.add("W16");
        supportedSets.add("SOI");
        supportedSets.add("EMA");
        supportedSets.add("EMN");
        supportedSets.add("V16");
        supportedSets.add("CN2");
        supportedSets.add("DDR");
        supportedSets.add("KLD");
        supportedSets.add("MPS");
        // supportedSets.add("PZ2");
        supportedSets.add("C16");
        supportedSets.add("PCA");
        supportedSets.add("AER");
        supportedSets.add("MM3");
        supportedSets.add("DDS");
        supportedSets.add("W17");
        supportedSets.add("AKH");
        supportedSets.add("MPS");
        supportedSets.add("CMA");
        supportedSets.add("E01");
        supportedSets.add("HOU");
        supportedSets.add("C17");
        supportedSets.add("XLN");
        supportedSets.add("DDT");
        supportedSets.add("IMA");
        supportedSets.add("E02");
        supportedSets.add("V17");
        supportedSets.add("UST");
        supportedSets.add("RIX");
        supportedSets.add("WMCQ");
        supportedSets.add("PPRO");
        supportedSets.add("A25");
        supportedSets.add("DOM");
//        supportedSets.add("M19");

    }

    @Override
    public String generateURL(CardDownloadData card) throws Exception {

        // special card number like "103a" already compatible
        if (card.isCollectorIdWithStr()) {
            return "https://img.scryfall.com/cards/large/en/" + formatSetName(card.getSet()) + "/"
                    + card.getCollectorId() + ".jpg";
        }

        // double faced cards do not supporte by API (need direct link for img)
        // example: https://img.scryfall.com/cards/large/en/xln/173b.jpg
        if (card.isTwoFacedCard()) {
            return "https://img.scryfall.com/cards/large/en/" + formatSetName(card.getSet()) + "/"
                    + card.getCollectorId() + (card.isSecondSide() ? "b" : "a") + ".jpg";
        }

        // basic cards by api call (redirect to img link)
        // example: https://api.scryfall.com/cards/xln/121?format=image
        return "https://api.scryfall.com/cards/" + formatSetName(card.getSet()) + "/"
                + card.getCollectorId() + "?format=image";
    }

    @Override
    public String generateTokenUrl(CardDownloadData card) throws Exception {
        return null;
    }

    @Override
    public String getNextHttpImageUrl() {
        return null;
    }

    @Override
    public String getFileForHttpImage(String httpImageUrl) {
        return null;
    }

    @Override
    public String getSourceName() {
        return "scryfall.com";
    }

    @Override
    public float getAverageSize() {
        return 90;
    }

    @Override
    public int getTotalImages() {
        return -1;
    }

    @Override
    public boolean isTokenSource() {
        return false;
    }

    @Override
    public void doPause(String httpImageUrl) {

    }

    private String formatSetName(String setName) {
        if (setNameReplacement.containsKey(setName)) {
            setName = setNameReplacement.get(setName);
        }
        return setName.toLowerCase(Locale.ENGLISH);
    }

    private static final Map<String, String> setNameReplacement = new HashMap<String, String>() {
        {
            put("DD3GVL", "gvl");
            put("DD3JVC", "jvc");
            put("DD3DVD", "dvd");
            put("DD3EVG", "evg");
            put("MPS-AKH", "mp2");
            put("MBP", "pmei");
            put("WMCQ", "pwcq");
        }
    };

    @Override
    public ArrayList<String> getSupportedSets() {
        ArrayList<String> supportedSetsCopy = new ArrayList<>();
        supportedSetsCopy.addAll(supportedSets);
        return supportedSetsCopy;
    }
}
