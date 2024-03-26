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
        File base = new File("base.svg");
        Scanner open = new Scanner(base);
        String[] template = new String[439];
        for (int i = 0; i < template.length; i++) {
            template[i] = open.nextLine();
        }
        open.close();
        System.out.println("template loaded");
        
        File registrations = new File("C:\\Users\\King\\Documents\\Java projects\\badge-creator\\IYC_24_Main_Registration_Responses_-_Region_3.csv");
        Scanner responses = new Scanner(registrations);
        responses.nextLine(); // i gotta advance this past the header
        ArrayList<String[]> resStore = new ArrayList<String[]>();
        while(responses.hasNext()) {
            resStore.add(Arrays.copyOfRange(responses.nextLine().split(","), 0, 11));
        }

        File[] tickets = new File("C:\\Users\\King\\Documents\\Java projects\\badge-creator\\sample").listFiles();
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
            String barcode = "";
            String qrcode = "";
            if (!ticketText.get(46).equals("		</text>")) {
                confirm = ticketText.get(46).strip().substring(30);
                barcode = ticketText.get(40).strip().split("\"")[5];
                qrcode = "xlink:href=\"" + ticketText.get(7).strip().split("\"")[7] + "\" /><style";
            } else {
                confirm = ticketText.get(48).strip().substring(30);
                barcode = ticketText.get(42).strip().split("\"")[5];
                qrcode = "xlink:href=\"" + ticketText.get(7).strip().split("\"")[7] + "\" /><style";
            }
            

            // Determine the age group, region, shirt size and name of the badge holder
            for(int j = 0; j < resStore.size(); j++) {
                if(resStore.get(j)[2].strip().equals(confirm)) {
                    age = resStore.get(j)[10].strip();
                    firstName = resStore.get(j)[3].strip();
                    lastName = resStore.get(j)[4].strip();
                    region = resStore.get(j)[0].strip().substring(0,2);
                    size = resStore.get(j)[8].strip();
                }
            }

            badge[423] = qrcode;
            badge[422] = "<image transform=\"translate(874, 28)\"";
            badge[420] = "id=\"tspan2652\">" + region + "</tspan></text>";
            badge[412] = "style=\"stroke-width:0\">" + size + "</tspan></text><text";
            badge[261] = "style=\"stroke-width:0\">" + firstName + "</tspan></text><g";
            badge[250] = "style=\"font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Montserrat;-inkscape-font-specification:'Montserrat Bold';stroke-width:0\">" + lastName + "</tspan></text><text";
            badge[223] = "d=\"" + barcode + "\"";
            badge[220] = "style=\"stroke-width:0\">" + confirm + "</tspan></text><path transform=\"scale(2.3794) translate(-126,333)\"";

            File save = new File("C:\\Users\\King\\Documents\\Java projects\\badge-creator\\completed\\" + age + "\\badge" + (i + 1) + ".svg");
            FileWriter write = new FileWriter(save);
            // Age groups: 7-11, 12-18, 19-24, 25-34, 35-44, 45+
            for (int k = 0; k < badge.length; k++) {
                write.write(badge[k] + "\n");
            }
            write.close();
        }
    }
}