import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

class Main {
    public static void main(String[] args) throws IOException {
        // barcode expanded by factor of 2.3794
        // QR code expanded by factor of 2.6785714285714285714285714285714
        File base = new File("base3.svg");
        Scanner open = new Scanner(base);
        String[] template = new String[439];
        for (int i = 0; i < template.length; i++) {
            template[i] = open.nextLine();
        }
        open.close();
        System.out.println("template loaded");
        File[] tickets = new File("C:\\Users\\King\\Documents\\Java projects\\badge-create\\sample").listFiles();
        for (int i = 0; i < tickets.length; i++) {
            Scanner tix = new Scanner(tickets[i]);
            ArrayList<String> ticketText = new ArrayList<String>();
            while (tix.hasNext()) {
                ticketText.add(tix.nextLine());
            }
            String[] badge = template.clone();
            String[] name = ticketText.get(90).strip().substring(31).split(" ");
            String firstName = "";
            String region = "";
            String size = "";
            String confirm = ticketText.get(46).strip().substring(30);
            String barcode = ticketText.get(40).strip().split("\"")[5];
            String qrcode = "xlink:href=\"" + ticketText.get(7).strip().split("\"")[7] + "\" /><style";

            // first name assembly, cuz i want to do it here
            for (int j = 0; j < name.length - 1; j++)
                firstName += name[j] + " ";

            badge[423] = qrcode;
            badge[422] = "<image transform=\"translate(874, 28)\"";
            badge[420] = "id=\"tspan2652\">" + region + "</tspan></text>";
            badge[412] = "style=\"stroke-width:0\">" + size + "</tspan></text><text";
            badge[261] = "style=\"stroke-width:0\">" + firstName.strip() + "</tspan></text><g";
            badge[250] = "style=\"font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Montserrat;-inkscape-font-specification:'Montserrat Bold';stroke-width:0\">" + name[name.length - 1] + "</tspan></text><text";
            badge[223] = "d=\"" + barcode + "\"";
            badge[220] = "style=\"stroke-width:0\">" + confirm + "</tspan></text><path transform=\"scale(2.3794) translate(-126,333)\"";
            File save = new File("C:\\Users\\King\\Documents\\Java projects\\badge-create\\completed\\badge" + (i + 1) + ".svg");
            FileWriter write = new FileWriter(save);
            for (int k = 0; k < badge.length; k++) {
                write.write(badge[k] + "\n");
            }
            write.close();
        }
    }
}