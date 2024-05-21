package test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Macro{
    NodeMNT headMNT,tailMNT;
    int indexMNT=1;
    int mdt=21;
    NodeMDT headMDT,tailMDT;
    ArrayList<NodeMNT> listMNT=new ArrayList<>();
    ArrayList<NodeMDT> listMDT=new ArrayList<>();
    public void insertMNT(String name){
        NodeMNT node=new NodeMNT(indexMNT,name,mdt);
        listMNT.add(node);
        indexMNT++;
    }

    public void insertMDT(String card){
        NodeMDT node=new NodeMDT(mdt,card);
        listMDT.add(node);
        mdt++;
    }

    public void displayMNT(){
        NodeMNT temp=headMNT;
        System.out.println("MNT: ");
        System.out.println("index   name    MDT index");
        while(temp!=null){
            System.out.println(temp.index+"       "+temp.name+"         "+ temp.mdt);
            temp=temp.next;
        }
    }
    public void displayMDT(){
        NodeMDT temp=headMDT;
        System.out.println("MDT:");
        System.out.println("index   card");
        while(temp!=null){
            System.out.println(temp.mdt+"      "+temp.card);
            temp=temp.next;
        }
    }

    public void expandMacro(String name) throws IOException{
//        NodeMNT temp=headMNT;
        int index=-1;
        FileWriter fw=new FileWriter("output.txt",true);
        for(NodeMNT node: listMNT){
            if(node.name.equals(name)){
                index=node.mdt;
                break;
            }
        }
        if(index==-1){
            fw.write(name+"\n");
        }
        else{
            for(int i=0;i<listMDT.size();i++){
                if(listMDT.get(i).mdt==index){
                    while(!listMDT.get(i).card.equals("mend")){
                        fw.write(listMDT.get(i).card+"\n");
                        i++;
                    }
                }
            }
        }
        fw.close();

    }
    private class NodeMDT{
        int mdt;
        String card;
        NodeMDT next;
        NodeMDT(int mdt,String card){
            this.mdt=mdt;
            this.card=card;
        }
    }
    private class NodeMNT{
        String name;
        int index;
        int mdt;
        NodeMNT next;
        NodeMNT(int index,String name,int mdt){
            this.index=index;
            this.name=name;
            this.mdt=mdt;
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        File myObj = new File("lab/input.txt");
        Scanner myReader = new Scanner(myObj);
        Macro macro=new Macro();
        boolean flag=false;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if(data.startsWith("macro")){
                String[] m=data.split(" ");
                macro.insertMNT(m[1]);
                data=myReader.nextLine();
                while(!data.equals("mend")){
                     macro.insertMDT(data.trim());
                    data=myReader.nextLine();
                }
                macro.insertMDT(data.trim()); //mend
            }
            else if(data.startsWith(".code")){
                while(myReader.hasNextLine()) {
                    data = myReader.nextLine();
                    macro.expandMacro(data);
                }
            }
        }
        macro.displayMNT();
        macro.displayMDT();
        myReader.close();
    }
}
