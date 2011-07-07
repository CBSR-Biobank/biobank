package edu.ualberta.med.biobank.tools.pnumberprob;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.OptionException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class PnumberProb {

    private static String USAGE = "Usage: pnumberprob PNUMBER1 PNUMBER2 ... PNUMBERn";

    private boolean verbose = false;

    private static Map<String, Double> pnumberProbMap;
    static {
        Map<String, Double> aMap = new HashMap<String, Double>();
        aMap.put("1111", 0.1284599006);
        aMap.put("1115", 0.0007097232);
        aMap.put("1151", 0.2079488999);
        aMap.put("1155", 0.0070972321);
        aMap.put("1191", 0.1163946061);
        aMap.put("1195", 0.0021291696);
        aMap.put("1511", 0.1930447126);
        aMap.put("1515", 0.003548616);
        aMap.put("1551", 0.1845280341);
        aMap.put("1555", 0.0021291696);
        aMap.put("1591", 0.0014194464);
        aMap.put("1911", 0.1071682044);
        aMap.put("1915", 0.0007097232);
        aMap.put("5111", 0.0085166785);
        aMap.put("5151", 0.0056777857);
        aMap.put("5191", 0.0049680625);
        aMap.put("5511", 0.0092264017);
        aMap.put("5551", 0.0099361249);
        aMap.put("5911", 0.0056777857);
        aMap.put("9551", 0.0007097232);

        pnumberProbMap = Collections.unmodifiableMap(aMap);
    }

    public PnumberProb(String argv[]) {

        CmdLineParser parser = new CmdLineParser();
        Option verboseOpt = parser.addBooleanOption('v', "verbose");

        try {
            parser.parse(argv);
        } catch (OptionException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Boolean boleanOptVal = (Boolean) parser.getOptionValue(verboseOpt);
        if (boleanOptVal != null) {
            verbose = boleanOptVal.booleanValue();
        }

        String[] args = parser.getRemainingArgs();
        if (args.length < 1) {
            System.out.println("Error: invalid arguments\n" + USAGE);
            System.exit(-1);
        }

        for (String pnumber : args) {

            String pnumberRegEx = pnumber.replace("x", ".");
            Pattern pattern = Pattern.compile(pnumberRegEx);

            Map<Double, String> results = new TreeMap<Double, String>();
            for (String key : pnumberProbMap.keySet()) {
                if (pattern.matcher(key).matches()) {
                    results.put(pnumberProbMap.get(key), key);
                }
            }

            if (results.isEmpty()) {
                System.out.println("no results for this patient number");
                System.exit(-1);
            }

            System.out.println("the probabilities for " + pnumber
                + " are (starting at lowest):");
            for (Double key : results.keySet()) {
                System.out.println("\t" + results.get(key) + "\t" + key);
            }
            System.out.println();
        }
    }

    public static void main(String argv[]) throws Exception {
        new PnumberProb(argv);
    }
}
