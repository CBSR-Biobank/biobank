package edu.ualberta.med.biobank.strfields;

public class StrFields {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.out.println("missing parameter");
            System.exit(-1);
        }

        try {
            DataModelExtractor.getInstance().getDataModel(argv[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
