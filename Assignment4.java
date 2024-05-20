import java.util.*;
import java.io.*;

public class Assignment4 {
    public static void main(String[] args) {
        
        Map<String, String[]> motTable = new HashMap<>();
        motTable.put("ADD", new String[]{"1", "1"});
        motTable.put("SUB", new String[]{"2", "1"});
        motTable.put("MULT", new String[]{"3", "1"});
        motTable.put("JMP", new String[]{"4", "1"});
        motTable.put("JNEG", new String[]{"5", "1"});
        motTable.put("JPOS", new String[]{"6", "1"});
        motTable.put("JZ", new String[]{"7", "1"});
        motTable.put("LOAD", new String[]{"8", "1"});
        motTable.put("STORE", new String[]{"9", "1"});
        motTable.put("READ", new String[]{"10", "1"});
        motTable.put("WRITE", new String[]{"11", "1"});
        motTable.put("STOP", new String[]{"12", "1"});

     
        Map<String, Integer> pseudoOps = new HashMap<>();
        pseudoOps.put("DB", 1);
        pseudoOps.put("DW", 1);
        pseudoOps.put("ORG", 1);
        pseudoOps.put("ENDP", 0);
        pseudoOps.put("CONST", 1);
        pseudoOps.put("END", 0);

        
        List<Map<String, String>> literalTable = new ArrayList<>();

        
        Map<String, String> symbolTable = new HashMap<>();

       
        System.out.println("Machine Operation Table (MOT):");
        System.out.println("Mnemonic\tOpcode\tNumber of Operands");
        for (Map.Entry<String, String[]> entry : motTable.entrySet()) {
            String mnemonic = entry.getKey();
            String[] motInfo = entry.getValue();
            System.out.println(mnemonic + "\t\t" + motInfo[0] + "\t" + motInfo[1]);
        }
        System.out.println();

       
        System.out.println("Pseudo Operation Table (POT):");
        System.out.println("Pseudo Opcode\tNumber of Operands");
        for (Map.Entry<String, Integer> entry : pseudoOps.entrySet()) {
            String opcode = entry.getKey();
            int numOperands = entry.getValue();
            System.out.println(opcode + "\t\t\t" + numOperands);
        }
        System.out.println();

      
        try (BufferedReader reader = new BufferedReader(new FileReader("input7.txt"))) {
            String line;
            int locationCounter = 0;
            boolean stopEncountered = false;

        
            while ((line = reader.readLine()) != null) {
                Map<String, String> entry = new HashMap<>();
                String[] words = line.trim().split("\\s+");
                
                if (words.length == 3) {
                    entry.put("mnemonic", words[1]);
                    entry.put("label", words[0]);
                    entry.put("operand", words[2]);
                    entry.put("loop_counter", Integer.toString(locationCounter));
                    String opcode = motTable.containsKey(words[1]) ? motTable.get(words[1])[0] : "*";
                    entry.put("opcode", opcode); 
                    if (stopEncountered)
                        locationCounter += 1; 
                    else
                        locationCounter += 2; 
                } else if (words.length == 2) {
                    entry.put("mnemonic", words[0]);
                    entry.put("label", "");
                    entry.put("operand", words[1]);
                    entry.put("loop_counter", Integer.toString(locationCounter));
                    String opcode = motTable.containsKey(words[0]) ? motTable.get(words[0])[0] : "*";
                    entry.put("opcode", opcode);
                    if (stopEncountered)
                        locationCounter += 1; 
                    else
                        locationCounter += 2; 
                } else if (words.length == 1) {
                    entry.put("mnemonic", words[0]);
                    entry.put("label", "");
                    entry.put("operand", "");
                    entry.put("loop_counter", Integer.toString(locationCounter));
                    String opcode = motTable.containsKey(words[0]) ? motTable.get(words[0])[0] : "*";
                    entry.put("opcode", opcode);

                    if (words[0].equalsIgnoreCase("ENDP")) {
                        
                    }
                    else{
                    if (!motTable.containsKey(words[0]) ) {
                        locationCounter += 1;
                    } else if (words[0].equalsIgnoreCase("STOP")) {
                        locationCounter += 1;
                        stopEncountered = true; 
                    }  else {
                        if (stopEncountered){locationCounter += 1;}
                            
                        else if (words[0].equalsIgnoreCase("ENDP")) {
                                locationCounter-=1;
                            }
                        else{locationCounter += 2; }
                            
                    }
                }
                }
                
                
                if (!motTable.containsKey(words[0]) && !pseudoOps.containsKey(words[0]) && !words[0].equalsIgnoreCase("END") && !words[0].equalsIgnoreCase("ENDP")) {
                    symbolTable.put(words[0], entry.get("loop_counter"));
                }
                
                literalTable.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nLiteral Table (LT):");
        System.out.println("Mnemonic\tLabel\tOperand\tLoop Counter\tOpcode\tAddress");
        for (Map<String, String> entry : literalTable) {
            
            int length = entry.get("mnemonic").length() + entry.get("operand").length();
            System.out.println(entry.get("mnemonic") + "\t\t" +
                               entry.get("label") + "\t" +
                               entry.get("operand") + "\t" +
                               entry.get("loop_counter") + "\t\t" +
                               entry.get("opcode") + "\t" +
                               (symbolTable.containsKey(entry.get("operand")) ? symbolTable.get(entry.get("operand")) : ""));
        }

        for (Map<String, String> entry : literalTable) {
            String label = entry.get("label");
            if (!label.isEmpty()) {
                symbolTable.put(label, entry.get("loop_counter"));
            }
        }

        System.out.println("\nSymbol Table:");
        System.out.println("Mnemonic\tAddress");
        for (Map.Entry<String, String> symbol : symbolTable.entrySet()) {
            System.out.println(symbol.getKey() + "\t\t" + symbol.getValue());
        }
    }
}
