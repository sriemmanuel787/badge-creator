package nvtspl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.spire.pdf.*;

class Main1 {
    public static void main(String[] args) throws IOException {
        File[] oldBadges = new File("onsite").listFiles();
        for (int i = 0; i < oldBadges.length; i++) {
            oldBadges[i].delete();
        }
        File base = new File("base.svg");
        Scanner open = new Scanner(base);
        String[] template = new String[483];
        for (int i = 0; i < template.length; i++) {
            template[i] = open.nextLine();
        }
        open.close();
        System.out.println("template loaded");

        File[] tickets = new File("sample").listFiles();
        for (int i = 0; i < tickets.length; i++) {
            PdfDocument temp = new PdfDocument();
            temp.loadFromFile(tickets[i].getAbsolutePath());
            temp.saveToFile("sample/svg-temp" + (i + 1) + ".svg", FileFormat.SVG);
            temp.close();
            tickets[i].delete();
        }
        tickets = new File("sample").listFiles();

        for (int i = 0; i < tickets.length; i++) {
            System.out.println(i);
            Scanner tix = new Scanner(tickets[i]);
            ArrayList<String> ticketText = new ArrayList<String>();
            while (tix.hasNext()) {
                ticketText.add(tix.nextLine());
            }
            tix.close();
            String[] badge = template.clone();
            String firstName = "";
            String lastName = "";
            String region = "";
            String confirm = "";
            String[] barcode = new String[47];
            String qrcode = "";
            try {
                confirm = ticketText.get(77).split(">")[1].split("<")[0];
                for (int j = 0; j < barcode.length; j++)
                    barcode[j] = ticketText.get(j + 30);
                qrcode = "xlink:href=\"" + ticketText.get(81).strip().split("\"")[5] + "\"";
                String[] name = ticketText.get(102).split(">")[1].split("<")[0].split(" ");
                firstName = name[0];
                for (int j = 1; j < name.length; j++)
                    lastName += name[j] + " ";
                lastName = lastName.trim();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("error!" + i);
            }
            tickets[i].delete();
            barcode[0] = "<g transform=\"scale(4.768534177) translate(-63.1, 165.9)\">";
            barcode[46] = "</g>";
            
            badge[467] = qrcode;
            badge[464] = "id=\"tspan2652\">" + region + "</tspan></text>";
            badge[305] = "style=\"stroke-width:0\">" + firstName + "</tspan></text><g";
            badge[294] = "style=\"font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Montserrat;-inkscape-font-specification:'Montserrat Bold';stroke-width:0\">"
                    + lastName + "</tspan></text><text";
            for (int j = 0; j < barcode.length; j++) {
                badge[j + 221] = barcode[j];
            }
            badge[220] = "style=\"stroke-width:0\">" + confirm
                    + "</tspan></text>";

            File save = new File("onsite/badge" + (i + 1) + ".svg");
            FileWriter write = new FileWriter(save);
            // Age groups: 7-11, 12-18, 19-24, 25-34, 35-44, 45+
            for (int k = 0; k < badge.length; k++) {
                write.write(badge[k] + "\n");
            }
            write.close();
        }
    }
}