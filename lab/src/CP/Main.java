package CP;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("lab/sample.txt"));
            String line = reader.readLine();
            Map<String, String> subexpr_table = new HashMap<>();

            while (line != null) {
                if (line.contains("=") && !line.contains("==")) {
                    String[] Assignexpr = line.strip().split("=");
                    String variable = Assignexpr[0].trim();
                    if (Assignexpr[0].contains(":")) {
                        subexpr_table = new HashMap<>();
                        String[] lhs = Assignexpr[0].replace(" ", "").split(":");
                        variable = lhs[1];
                    }
                    String[] var_list = Pattern.compile("\\+|-|\\*|/|%|>|<|>=|<=").split(Assignexpr[1]);
                    if (var_list.length == 1) {
                        int found = 0;
                        String temp = "";

                        for (Map.Entry<String, String> entry : subexpr_table.entrySet()) {
                            String[] value = entry.getValue().split(",");
                            for (String j : value) {
                                if (variable.equals(j)) {
                                    found = 1;
                                    temp = entry.getKey();
                                    break;
                                }
                            }
                        }
                        if (found == 1) {
                            subexpr_table.remove(temp);
                        }
                        System.out.println(line);
                    }
                    if (var_list.length == 2) {
                        String[] tup = new String[3];
                        String op1 = var_list[0];
                        String op2 = var_list[1];
                        String op = "";
                        int flag = 0;
                        String[] ops = {"+", "-", "*", "/", "<", ">", "<=", ">=", "%"};
                        String[] opsSymbols = {"\\+", "-", "\\*", "/", "<", ">", "<=", ">=", "%"};
                        for (int i = 0; i < ops.length; i++) {
                            if (line.contains(ops[i])) {
                                op = opsSymbols[i];
                                break;
                            }
                        }
                        tup[0] = op;
                        tup[1] = op1;
                        tup[2] = op2;
                        for (Map.Entry<String, String> entry : subexpr_table.entrySet()) {
                            String[] value = entry.getValue().split(",");
                            if (tup[0].equals(value[0]) && tup[1].equals(value[1]) && tup[2].equals(value[2])) {
                                Assignexpr[1] = entry.getKey();
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            subexpr_table.put(variable, String.join(",", tup));
                        }
                        System.out.println(Assignexpr[0] + "=" + Assignexpr[1]);
                    }
                } else if (line.contains("==")) {
                    String[] spl = line.strip().split("==");
                    if (spl[0].contains("=")) {
                        String[] Assignexpr = spl[0].split("=");
                        String variable = Assignexpr[0].trim();
                        if (Assignexpr[0].contains(":")) {
                            String[] lhs = Assignexpr[0].replace(" ", "").split(":");
                            variable = lhs[1];
                        }
                        String op1 = Assignexpr[1];
                        String op2 = spl[1];
                        String op = "==";
                        int flag = 0;
                        String[] tup = new String[3];
                        tup[0] = op;
                        tup[1] = op1;
                        tup[2] = op2;
                        String rhs = Assignexpr[1] + "==" + spl[1];
                        for (Map.Entry<String, String> entry : subexpr_table.entrySet()) {
                            String[] value = entry.getValue().split(",");
                            if (tup[0].equals(value[0]) && tup[1].equals(value[1]) && tup[2].equals(value[2])) {
                                rhs = entry.getKey();
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            subexpr_table.put(variable, String.join(",", tup));
                        }
                        System.out.println(Assignexpr[0] + "=" + rhs);
                    }
                } else {
                    System.out.println(line);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
