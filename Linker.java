import java.io.*;
import java.util.*;

class Variable {
    String name;
    String dataType;
    int size;

    Variable(String name, String dataType, int size) {
        this.name = name;
        this.dataType = dataType;
        this.size = size;
    }
}

class SymbolEntry {
    String name;
    String type;
    int size;
    int address;

    public SymbolEntry(String name, String type, int size, int address) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-20s %-10s %-10s", name, type, address, size);
    }
}

class SymbolTable {
    private SymbolEntry[] entries;
    private int count;
    private int lastAddress;

    public SymbolTable(int capacity, int startAddress) {
        entries = new SymbolEntry[capacity];
        count = 0;
        lastAddress = startAddress;
    }

    public void addToSymbolTable(String[] declarations) {
        for (String declaration : declarations) {
            String[] parts = declaration.trim().split("\\s+|,");
            if (parts.length >= 2) {
                String type = parts[0];
                for (int i = 1; i < parts.length; i++) {
                    String variable = parts[i].replace(";", "").trim();
                    int size = (type.equals("int")) ? 4 : ((type.equals("float")) ? 4 : ((type.equals("char")) ? 1 : 0));

                    entries[count] = new SymbolEntry(variable, type, size, lastAddress);
                    count++;
                    lastAddress += size;
                }
            } else {
                System.out.println("Invalid declaration: " + declaration);
            }
        }
    }

    public void displaySymbolTable(String title) {
        System.out.println(title);
        System.out.println("Symbol Table:");
        System.out.printf("%-20s %-20s %-10s %-10s\n", "Name", "Type", "Address", "Size");
        System.out.println("");
        for (int i = 0; i < count; i++) {
            System.out.println(entries[i]);
        }
        System.out.println();
    }

    public int getLastAddress() {
        return lastAddress;
    }

    public SymbolEntry[] getEntries() {
        return entries;
    }
}

public class Linker {
    public static void main(String[] args) {
        String inputFile1 = "file1.txt";
        String inputFile2 = "file2.txt";

        try {
            Map<String, Variable> externVariables1 = readExternVariables(inputFile1);
            Map<String, Variable> externVariables2 = readExternVariables(inputFile2);

            printExternVariableTable(externVariables1, "Extern Variable Table for File 1:");
            printExternVariableTable(externVariables2, "Extern Variable Table for File 2:");

            SymbolTable symbolTable1 = new SymbolTable(100, 0);
            int startAddressForTable2 = 100;
            SymbolTable symbolTable2 = new SymbolTable(100, startAddressForTable2);

            printSymbolTable(inputFile1, symbolTable1, "Symbol Table for File 1:");
            printSymbolTable(inputFile2, symbolTable2, "Symbol Table for File 2:");

            printCombinedExternTable(symbolTable1, symbolTable2, externVariables1, externVariables2);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static Map<String, Variable> readExternVariables(String inputFile) throws IOException {
        Map<String, Variable> externVariables = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("extern:")) {
                    line = line.substring(line.indexOf(":") + 1).trim();
                    String[] tokens = line.split(",");
                    for (String token : tokens) {
                        token = token.trim().replace(";", "");
                        String[] parts = token.split("\\s+");
                        if (parts.length == 2) {
                            externVariables.put(parts[1], new Variable(parts[1], parts[0], getSize(parts[0])));
                        } else {
                            System.err.println("Invalid extern declaration: " + token);
                        }
                    }
                }
            }
        }
        return externVariables;
    }

    public static void printExternVariableTable(Map<String, Variable> externVariables, String title) {
        System.out.println(title);
        System.out.printf("%-15s %-15s %-15s%n", "Variable", "Data Type", "Size");
        for (Variable variable : externVariables.values()) {
            System.out.printf("%-15s %-15s %-15d%n", variable.name, variable.dataType, variable.size);
        }
        System.out.println();
    }

    public static void printSymbolTable(String inputFile, SymbolTable symbolTable, String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("extern:") && !line.contains("main(){")) {
                    symbolTable.addToSymbolTable(line.split(";"));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
        symbolTable.displaySymbolTable(title);
    }

    public static void printCombinedExternTable(SymbolTable symbolTable1, SymbolTable symbolTable2,
                                                Map<String, Variable> externVariables1, Map<String, Variable> externVariables2) {
        System.out.println("Combined Extern Table:");
        System.out.printf("%-15s %-15s %-15s %-15s%n", "Variable (File 1)", "Variable (File 2)", "Address", "");

       
        Map<String, Integer> combinedExternTable = new HashMap<>();

       
        for (Map.Entry<String, Variable> entry : externVariables1.entrySet()) {
            String variable1 = entry.getKey();
            Variable dataType1 = entry.getValue();
            for (SymbolEntry symbolEntry : symbolTable2.getEntries()) {
                if (symbolEntry != null && symbolEntry.name.equals(variable1)) {
                    combinedExternTable.put(variable1, symbolEntry.address);
                    break;
                }
            }
        }

        
        for (Map.Entry<String, Variable> entry : externVariables2.entrySet()) {
            String variable2 = entry.getKey();
            Variable dataType2 = entry.getValue();
            for (SymbolEntry symbolEntry : symbolTable1.getEntries()) {
                if (symbolEntry != null && symbolEntry.name.equals(variable2)) {
                    combinedExternTable.put(variable2, symbolEntry.address);
                    break;
                }
            }
        }

       
        for (Map.Entry<String, Integer> entry : combinedExternTable.entrySet()) {
            String variable = entry.getKey();
            int address = entry.getValue();
            System.out.printf("%-15s %-15s %-15d %-15s%n", variable, "", address, "");
        }
    }

    public static int getSize(String dataType) {
        switch (dataType) {
            case "int":
                return 4;
            case "float":
                return 8;
            
            default:
                return 0;
        }
    }
}
