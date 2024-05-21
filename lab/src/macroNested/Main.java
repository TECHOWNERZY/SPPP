package macroNested;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Macro{
    int indexMNT=1;
    int mdt=25;

    ArrayList<NodeMNT> mntList=new ArrayList<>();
    ArrayList<NodeMDT> mdtList=new ArrayList<>();

    FileWriter fw;
    Macro(FileWriter fw){
        this.fw=fw;
    }
    class NodeMNT{
        int indexMNT;
        String name;
        int mdt;
        NodeMNT(String name,int indexMNT,int mdt){
            this.name=name;
            this.indexMNT=indexMNT;
            this.mdt=mdt;
        }
    }
    class NodeMDT{
        int mdt;
        String card;
        NodeMDT(String card,int mdt){
            this.card=card;
            this.mdt=mdt;
        }
    }
    public void insertMNT(String name){
        NodeMNT node=new NodeMNT(name,indexMNT,mdt);
        mntList.add(node);
        indexMNT++;
    }

    public void insertMDT(String card){
        NodeMDT node=new NodeMDT(card,mdt);
        mdtList.add(node);
        mdt++;
    }

    public void expandMacro(String name) throws IOException {
        int mdt=-1;
        for(NodeMNT i: mntList){
            if(i.name.equals(name)){
                mdt=i.mdt;
            }
        }
        if(mdt!=-1){
            boolean flag=false;
            for(NodeMDT j:mdtList){
                if(j.mdt==mdt || flag){
                    if(j.card.equals("mend")){
                        flag=false;
                    }
                    else{
                        flag=true;
                        expandMacro(j.card);
                    }
                }
            }
        }
        else{
            fw.write(name+"\n");
//            System.out.println(name);
        }
    }

}

public class Main {
    public static void main(String[] args) throws IOException {
        File file=new File("input3.txt");
        Scanner myReader=new Scanner(file);

        FileWriter fw=new FileWriter("output3.txt",true);
        Macro macro=new Macro(fw);
        while (myReader.hasNextLine()){
            String data=myReader.nextLine();
            if(data.startsWith("macro")){
                String[] tokens=data.split(" ");
                String macroName=tokens[1];
                macro.insertMNT(macroName.trim());
                while(!data.equals("mend")){
                    data= myReader.nextLine();
                    macro.insertMDT(data.trim());
                }
            }
            else if(data.startsWith(".code")){
                while(myReader.hasNextLine()) {
                    data= myReader.nextLine();
                    macro.expandMacro(data);
                }
            }
        }
        fw.close();     //output file is blank unless closed.

        System.out.println("\nMDT");
        System.out.println("index\tCard");
        for(Macro.NodeMDT i: macro.mdtList){
            System.out.println(i.mdt+"\t\t"+i.card);
        }
        System.out.println("\nMNT");
        System.out.println("index\tname\tmdtIndex");
        for(Macro.NodeMNT i: macro.mntList){
            System.out.println(i.indexMNT+"\t\t"+i.name+"\t\t"+i.mdt);
        }
    }
}
