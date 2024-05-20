import java.io.*;
import java.util.*;

class MacroExpander {
    static Map<String, Integer> MNT = new HashMap<>();
    static Map<Integer, String> MDT = new HashMap<>();
    static Map<String, List<String>> ALA = new HashMap<>();

    private static List<String> readLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void processMacroDefinitions(List<String> macroDefinitionLines) {
        int lineIndex = 0;
        while (lineIndex < macroDefinitionLines.size()) {
            String line = macroDefinitionLines.get(lineIndex++);
            if (line.startsWith("macro")) {
                String macroName = line.substring(6).trim();
                int definitionIndex = MDT.size() + 1;
                MNT.put(macroName, definitionIndex);

                List<String> arguments = new ArrayList<>();
                String[] argTokens = line.substring(6).trim().split(" ");
                for (int i = 1; i < argTokens.length; i++) {
                    arguments.add(argTokens[i]);
                }
                ALA.put(macroName, arguments);

                line = macroDefinitionLines.get(lineIndex++);
                while (!line.startsWith("mend")) {
                    MDT.put(definitionIndex++, line);
                    line = macroDefinitionLines.get(lineIndex++);
                }
                MDT.put(definitionIndex++, line);
            }
        }
    }

    private static void processCode(List<String> codeLines, FileWriter outputFile) throws IOException {
        for (String line : codeLines) {
            String[] tokens = line.split(" ", 2);
            String macroName = tokens[0];
            if (MNT.containsKey(macroName)) {
                int macroIndex = MNT.get(macroName);
                List<String> arguments = ALA.get(macroName);
                for (int i = macroIndex; ; i++) {
                    String expandedLine = MDT.get(i);
                    if (expandedLine.startsWith("mend")) break;
                    for (int j = 0; j < arguments.size(); j++) {
                        expandedLine = expandedLine.replace("&" + arguments.get(j), tokens[1].split(",")[j]);
                    }
                    outputFile.write(expandedLine + "\n");
                }
            } else {
                outputFile.write(line + "\n");
            }
        }
    }

    private static void printALA(FileWriter outputFile) throws IOException {
        outputFile.write("\t\t\t\tArgument List Array\n");
        outputFile.write("macroName\tformalParameter\tpositionalParameter\n");
        for (Map.Entry<String, List<String>> entry : ALA.entrySet()) {
            String macroName = entry.getKey();
            List<String> arguments = entry.getValue();
            for (int i = 0; i < arguments.size(); i++) {
                outputFile.write(String.format("%s\t\t\t%s\t\t\t\t#%d\n", macroName, arguments.get(i), i + 1));
            }
        }
    }

    public static void main(String[] args) {
        String inputFilePath = "input2.txt";
        String outputFilePath = "output2.txt";

        try (FileWriter outputFile = new FileWriter(outputFilePath)) {
            List<String> inputLines = readLinesFromFile(inputFilePath);

            processMacroDefinitions(inputLines);
            processCode(inputLines, outputFile);
            printALA(outputFile);

            System.out.println("Expanded code written to " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}