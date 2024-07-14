package nvtspl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Arrays;

import com.spire.pdf.*;

class Main {
    public static String[] REGION = { "Canada", "Caribbean", "Region 1", "Region 2", "Region 3", "Region 4" };

    public static void main(String[] args) throws IOException {
        /*
         * File[][] files = { new File("completed/7-11").listFiles(), new
         * File("completed/12-18").listFiles(),
         * new File("completed/19-24").listFiles(), new
         * File("completed/25-34").listFiles(),
         * new File("completed/35-44").listFiles(), new
         * File("completed/45-Above").listFiles() };
         * for (int i = 0; i < files.length; i++) {
         * for (int j = 0; j < files[i].length; j++) {
         * files[i][j].delete();
         * }
         * }
         */
        File base = new File("base.svg");
        Scanner open = new Scanner(base);
        String[] template = new String[483];
        for (int i = 0; i < template.length; i++) {
            template[i] = open.nextLine();
        }
        open.close();
        System.out.println("template loaded");

        File registrations = new File("sheet.csv");
        Scanner responses = new Scanner(registrations);
        responses.nextLine(); // i gotta advance this past the header
        ArrayList<String[]> resStore = new ArrayList<String[]>();
        while (responses.hasNext()) {
            resStore.add(Arrays.copyOfRange(responses.nextLine().split(","), 0, 11));
        }

        File[] tickets = new File("sample").listFiles();
        File[][] ticketsNew = { new File("sample/Canada").listFiles(), new File("sample/Caribbean").listFiles(),
                new File("sample/Region 1").listFiles(), new File("sample/Region 2").listFiles(),
                new File("sample/Region 3").listFiles(), new File("sample/Region 4").listFiles() };
        for (int i = 0; i < ticketsNew.length; i++) {
            System.out.println(REGION[i] + " working...");
            for (int j = 0; j < ticketsNew[i].length; j++) {
                System.out.println(REGION[i] + " batch " + (j + 1) + " working...");
                File[] currentTicketBatch = ticketsNew[i][j].listFiles();
                for (int k = 0; k < currentTicketBatch.length; k++) {
                    String[] directory = currentTicketBatch[k].getAbsolutePath().split(Pattern.quote("\\"));
                    directory[directory.length - 1] = "svg-temp" + (k + 1) + ".svg";
                    PdfDocument temp = new PdfDocument();
                    try {
                        temp.loadFromFile(currentTicketBatch[k].getAbsolutePath());
                    } catch (Exception e) {
                        System.out.println("failed to load " + currentTicketBatch[k].getAbsolutePath());
                    }
                    temp.saveToFile(String.join("\\", directory), FileFormat.SVG);
                    temp.close();
                    currentTicketBatch[k].delete();
                }
            }
            ticketsNew[i] = new File("sample/" + REGION[i]).listFiles();
        }

        for (int i = 0; i < ticketsNew.length; i++) {
            for (int j = 0; j < ticketsNew[i].length; j++) {
                File[] currentBadgeBatch = ticketsNew[i][j].listFiles();
                for (int k = 0; k < currentBadgeBatch.length; k++) {
                    Scanner tix = new Scanner(currentBadgeBatch[k]);
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
                        for (int l = 0; l < barcode.length; l++)
                            barcode[l] = ticketText.get(l + 30);
                        qrcode = "xlink:href=\"" + ticketText.get(81).strip().split("\"")[5] + "\"";
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Error: " + currentBadgeBatch[k].getAbsolutePath());
                    }

                    // Determine the age group, region, shirt size and name of the badge holder
                    try {
                        for (int l = 0; l < resStore.size(); l++) {
                            if (resStore.get(l)[2].strip().equals(confirm)) {
                                age = resStore.get(l)[10].strip();
                                firstName = resStore.get(l)[3].strip();
                                lastName = resStore.get(l)[4].strip();
                                region = resStore.get(l)[0].strip().substring(0, 2);
                                size = resStore.get(l)[8].strip();
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                    currentBadgeBatch[k].delete();
                    barcode[0] = "<g transform=\"scale(4.768534177) translate(-63.1, 165.9)\">";
                    barcode[46] = "</g>";

                    badge[467] = qrcode;
                    badge[464] = "id=\"tspan2652\">" + region + "</tspan></text>";
                    badge[456] = "style=\"stroke-width:0\">" + size + "</tspan></text><text";
                    badge[305] = "style=\"stroke-width:0\">" + firstName + "</tspan></text><g";
                    badge[294] = "style=\"font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Montserrat;-inkscape-font-specification:'Montserrat Bold';stroke-width:0\">"
                            + lastName + "</tspan></text><text";
                    for (int l = 0; l < barcode.length; l++) {
                        badge[l + 221] = barcode[l];
                    }
                    badge[220] = "style=\"stroke-width:0\">" + confirm
                            + "</tspan></text>";

                    String[] existingDir = currentBadgeBatch[i].getAbsolutePath().split(Pattern.quote("\\"));
                    String[] newDir = new String[existingDir.length + 1];
                    System.arraycopy(existingDir, 0, newDir, 0, existingDir.length);
                    newDir[6] = "completed";
                    newDir[9] = age;
                    newDir[10] = "badge" + (k + 1) + ".svg";
                    File save = new File(String.join("\\", Arrays.copyOfRange(newDir, 6, newDir.length)));
                    PrintStream write;
                    try {
                        write = new PrintStream(save);

                        // Age groups: 7-11, 12-18, 19-24, 25-34, 35-44, 45+
                        for (int l = 0; l < badge.length; l++) {
                            write.print(badge[l] + "\n");
                        }
                        write.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("failed to create " + String.join("\\", Arrays.copyOfRange(newDir, 6, newDir.length)));
                    }

                }
            }
        }

        /*
         * for (int i = 0; i < tickets.length; i++) {
         * Scanner tix = new Scanner(tickets[i]);
         * ArrayList<String> ticketText = new ArrayList<String>();
         * while (tix.hasNext()) {
         * ticketText.add(tix.nextLine());
         * }
         * String[] badge = template.clone();
         * String firstName = "";
         * String lastName = "";
         * String region = "";
         * String size = "";
         * String age = "";
         * String confirm = "";
         * String[] barcode = new String[47];
         * String qrcode = "";
         * try {
         * confirm = ticketText.get(77).split(">")[1].split("<")[0];
         * for (int j = 0; j < barcode.length; j++)
         * barcode[j] = ticketText.get(j + 30);
         * qrcode = "xlink:href=\"" + ticketText.get(81).strip().split("\"")[5] + "\"";
         * } catch (ArrayIndexOutOfBoundsException e) {
         * System.out.println("error!" + i);
         * }
         * 
         * // Determine the age group, region, shirt size and name of the badge holder
         * try {
         * for (int j = 0; j < resStore.size(); j++) {
         * if (resStore.get(j)[2].strip().equals(confirm)) {
         * age = resStore.get(j)[10].strip();
         * firstName = resStore.get(j)[3].strip();
         * lastName = resStore.get(j)[4].strip();
         * region = resStore.get(j)[0].strip().substring(0, 2);
         * size = resStore.get(j)[8].strip();
         * }
         * }
         * } catch (Exception e) {
         * continue;
         * }
         * tickets[i].delete();
         * barcode[0] = "<g transform=\"scale(4.768534177) translate(-63.1, 165.9)\">";
         * barcode[46] = "</g>";
         * 
         * badge[467] = qrcode;
         * badge[464] = "id=\"tspan2652\">" + region + "</tspan></text>";
         * badge[456] = "style=\"stroke-width:0\">" + size + "</tspan></text><text";
         * badge[305] = "style=\"stroke-width:0\">" + firstName + "</tspan></text><g";
         * badge[294] =
         * "style=\"font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Montserrat;-inkscape-font-specification:'Montserrat Bold';stroke-width:0\">"
         * + lastName + "</tspan></text><text";
         * for (int j = 0; j < barcode.length; j++) {
         * badge[j + 221] = barcode[j];
         * }
         * badge[220] = "style=\"stroke-width:0\">" + confirm
         * + "</tspan></text>";
         * 
         * File save = new File("completed/" + age + "/badge" + (i + 1) + ".svg");
         * FileWriter write = new FileWriter(save);
         * // Age groups: 7-11, 12-18, 19-24, 25-34, 35-44, 45+
         * for (int k = 0; k < badge.length; k++) {
         * write.write(badge[k] + "\n");
         * }
         * write.close();
         * }
         */
    }
}