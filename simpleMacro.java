import java.io.*;
import java.util.*;

public class simpleMacro {
    
    static Map<Integer, String> macroDefinitionTable = new HashMap<>();
    
    static Map<String, Integer> macroNameTable = new HashMap<>();
   
    static int mdtCounter = 1; // counter for MDT
    static int mntCounter = 1;

  
    public static void pass1(String[] input) {
        boolean inMacro = false;
        String currentMacro = "";
        for (String line : input) {
            if (line.startsWith("MACRO")) {
                inMacro = true;
                currentMacro = line.split(" ")[1]; // extracts the macro name
                macroNameTable.put(currentMacro, mdtCounter);
                continue;
            }
            if (inMacro && line.equals("MEND")) {
                inMacro = false;
                macroDefinitionTable.put(mdtCounter++, line);
                continue;
            }
            if (inMacro) {
                macroDefinitionTable.put(mdtCounter++, line);
            }
        }
    }

  
    public static void pass2(String inputFilename, String outputFilename) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
             PrintWriter writer = new PrintWriter(outputFilename)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (macroNameTable.containsKey(line.trim())) {
                    int mdtStartIndex = macroNameTable.get(line.trim());
                    for (int i = mdtStartIndex; i < macroDefinitionTable.size(); i++) {
                        String instruction = macroDefinitionTable.get(i);
                        writer.println(instruction);
                        if (instruction.equals("MEND")) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
    public static void printMDT() {
        
        System.out.println("Macro-Definition Table (MDT):");
        System.out.println("+------------------+-------------------+");
        System.out.println("| Line Number (MDT)|    Instruction    |");
        System.out.println("+------------------+-------------------+");
    
        for (Map.Entry<Integer, String> entry : macroDefinitionTable.entrySet()) {
            int lineNumber = entry.getKey();
            String instruction = entry.getValue();
            System.out.printf("| %-16d| %-17s|%n", lineNumber, instruction);
        }
        
      
        System.out.println("-------------------------------------");
    }
    
   
    public static void printMNT() {
      
        System.out.println("Macro-Name Table (MNT):");
        System.out.println("-------------------------------------");
        System.out.println("| Macro Name (MNT) |   MDT Start Index |");
        System.out.println("-------------------------------------");
        
     
        for (Map.Entry<String, Integer> entry : macroNameTable.entrySet()) {
            String macroName = entry.getKey();
            int mdtStartIndex = entry.getValue();
            System.out.printf("| %-16s| %-18d|%n", macroName, mdtStartIndex);
        }
        
      
        System.out.println("----------------------------------------");
    }

 
    public static void main(String[] args) {
       
        String[] input = readInputFromFile("input3.txt");

        pass1(input);
        String inputFilename = "input4.txt"; 
        String outputFilename = "output.txt"; 
        pass2(inputFilename, outputFilename);
        System.out.println("Output written to " + outputFilename);

        
        printMNT();
        printMDT();
    }

    
    public static String[] readInputFromFile(String filename) {
        List<String> inputList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                inputList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputList.toArray(new String[0]);
    }
}