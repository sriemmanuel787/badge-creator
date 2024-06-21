package nvtspl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

import com.spire.pdf.*;

class Main {
    public static void main(String[] args) throws IOException {
        File base = new File("base.svg");
        Scanner open = new Scanner(base);
        String[] template = new String[439];
        for (int i = 0; i < template.length; i++) {
            template[i] = open.nextLine();
        }
        open.close();
        System.out.println("template loaded");

        File registrations = new File("IYC_24_Main_Registration_Responses_-_Region_3.csv");
        Scanner responses = new Scanner(registrations);
        responses.nextLine(); // i gotta advance this past the header
        ArrayList<String[]> resStore = new ArrayList<String[]>();
        while (responses.hasNext()) {
            resStore.add(Arrays.copyOfRange(responses.nextLine().split(","), 0, 11));
        }

        File[] tickets = new File("sample").listFiles();
        for (int i = 0; i < tickets.length; i++) {
            PdfDocument temp = new PdfDocument();
            temp.loadFromFile(tickets[i].getAbsolutePath());
            temp.saveToFile("/workspaces/badge-creator/sample/svg-temp" + (i + 1) + ".svg", FileFormat.SVG);
            temp.close();
            tickets[i].delete();
        }
        tickets = new File("sample").listFiles();

        for (int i = 0; i < tickets.length; i++) {
            Scanner tix = new Scanner(tickets[i]);
            ArrayList<String> ticketText = new ArrayList<String>();
            while (tix.hasNext()) {
                ticketText.add(tix.nextLine());
            }
            String[] badge = template.clone();
            String firstName = "";
            String lastName = "";
            String region = "";
            String size = "";
            String age = "";
            String confirm = "";
            String[] barcode = new String[47];
            String qrcode = "";
            try {
                confirm = ticketText.get(77).split(">")[1].split("<")[0];
                for (int j = 0; j < barcode.length; j++)
                    barcode[j] = ticketText.get(j + 30);
                qrcode = "xlink:href=\"" + ticketText.get(81).strip().split("\"")[5] + "\"";
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("error!" + i);
            }

            // Determine the age group, region, shirt size and name of the badge holder
            try {
                for (int j = 0; j < resStore.size(); j++) {
                    if (resStore.get(j)[2].strip().equals(confirm)) {
                        age = resStore.get(j)[10].strip();
                        firstName = resStore.get(j)[3].strip();
                        lastName = resStore.get(j)[4].strip();
                        region = resStore.get(j)[0].strip().substring(0, 2);
                        size = resStore.get(j)[8].strip();
                    }
                }
            } catch (Exception e) {
                continue;
            }
            tickets[i].delete();
            barcode[0] = "<g transform=\"translate (-58.3, 16.7) scale(3.565217391304348)\">";
            barcode[46] = "</g>";

            badge[467] = qrcode;
            badge[464] = "id=\"tspan2652\">" + region + "</tspan></text>";
            badge[412] = "style=\"stroke-width:0\">" + size + "</tspan></text><text";
            badge[305] = "style=\"stroke-width:0\">" + firstName + "</tspan></text><g";
            badge[294] = "style=\"font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Montserrat;-inkscape-font-specification:'Montserrat Bold';stroke-width:0\">"
                    + lastName + "</tspan></text><text";
            for (int j = 0; j < barcode.length; j++) {
                badge[j + 221] = barcode[j];
            }
            badge[220] = "style=\"stroke-width:0\">" + confirm
                    + "</tspan></text>";

            File save = new File("completed/" + age + "/badge" + (i + 1) + ".svg");
            FileWriter write = new FileWriter(save);
            // Age groups: 7-11, 12-18, 19-24, 25-34, 35-44, 45+
            for (int k = 0; k < badge.length; k++) {
                write.write(badge[k] + "\n");
            }
            write.close();
        }
    }
}