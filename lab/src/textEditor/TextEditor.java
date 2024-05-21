package textEditor;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class TextEditor implements ActionListener {
    JFrame window;
    JTextArea textArea;
    JScrollPane scrollPane;
    JMenuBar menuBar;
    JMenu menuFile,menuEdit,menuView;
    JMenuItem inew,iopen,isave,isaveas,iexit,ifontSize,ifontFamily,iUndo,iRedo;

    UndoManager um=new UndoManager();
    FunctionClass file=new FunctionClass(this);
    public static void main(String[] args) {
        new TextEditor();
    }

    public TextEditor(){
        createWindow();
        createTextArea();
        createMenuBar();
        addMenuItem();
        window.setVisible(true);
    }
    public void createWindow(){
        window=new JFrame("Notepad");
        window.setSize(800,600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void createTextArea(){
        textArea=new JTextArea();
        textArea.getDocument().addUndoableEditListener(
                e -> um.addEdit(e.getEdit())
        );
        scrollPane=new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        window.add(scrollPane);
    }
    public void createMenuBar(){
        menuBar=new JMenuBar();
        window.setJMenuBar(menuBar);

        menuFile=new JMenu("File");
        menuBar.add(menuFile);
        menuEdit=new JMenu("Edit");
        menuBar.add(menuEdit);
        menuView=new JMenu("View");
        menuBar.add(menuView);
    }
    public void addMenuItem(){
        inew=new JMenuItem("New");
        inew.addActionListener(this);
        inew.setActionCommand("New");

        iopen=new JMenuItem("Open");
        iopen.addActionListener(this);
        iopen.setActionCommand("Open");

        isave=new JMenuItem("Save");
        isave.addActionListener(this);
        isave.setActionCommand("Save");

        isaveas=new JMenuItem("Save As");
        isaveas.addActionListener(this);
        isaveas.setActionCommand("SaveAs");

        iexit=new JMenuItem("Exit");
        iexit.addActionListener(this);
        iexit.setActionCommand("Exit");

        ifontSize=new JMenuItem("Font Size");
        ifontSize.addActionListener(this);
        ifontSize.setActionCommand("FontSize");

        ifontFamily=new JMenuItem("Font Family");
        ifontFamily.addActionListener(this);
        ifontFamily.setActionCommand("FontFamily");

        iUndo=new JMenuItem("Undo");
        iUndo.addActionListener(this);
        iUndo.setActionCommand("undo");

        iRedo=new JMenuItem("Redo");
        iRedo.addActionListener(this);
        iRedo.setActionCommand("redo");

        menuFile.add(inew);
        menuFile.add(iopen);
        menuFile.add(isave);
        menuFile.add(isaveas);
        menuFile.add(iexit);
        menuView.add(ifontSize);
        menuView.add(ifontFamily);
        menuEdit.add(iUndo);
        menuEdit.add(iRedo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command=e.getActionCommand();
        switch (command){
            case "New":file.newFile();break;
            case "Open":file.loadFile();break;
            case "Save":file.save();break;
            case "SaveAs":file.saveAs();break;
            case "Exit":file.exit();break;
            case "FontSize":file.changeFontSize();break;
            case "FontFamily":file.changeFontFamily();break;
            case "undo":file.undo();break;
            case "redo":file.redo();break;
        }
    }
}

class FunctionClass{
    TextEditor textEditor;
    String fileName;
    String directory;
    FunctionClass(TextEditor textEditor){
        this.textEditor=textEditor;
    }
    public void newFile(){
        textEditor.textArea.setText("");
        textEditor.window.setTitle("New");
    }
    public void loadFile(){
        FileDialog fileDialog=new FileDialog(textEditor.window,"Open",FileDialog.LOAD);
        fileDialog.setVisible(true);
        if(fileDialog.getFile()!=null){
            fileName=fileDialog.getFile();
            directory=fileDialog.getDirectory();
            textEditor.window.setTitle(fileName);

        }
        System.out.println("file name "+fileName+"directory "+directory);
        try{
            BufferedReader br=new BufferedReader(new FileReader(directory+fileName));
            textEditor.textArea.setText("");
            String line;
            while((line=br.readLine())!=null){
                textEditor.textArea.append(line+"\n");
            }
            br.close();
        }
        catch (Exception e){
            System.out.println("File not Found");
        }
    }

    public void save(){
        if(fileName==null){
            saveAs();
        }else{
            try {
                FileWriter fw=new FileWriter(directory+fileName);
                fw.write(textEditor.textArea.getText());
                textEditor.window.setTitle(fileName);
                fw.close();
            }
            catch (Exception e){
                System.out.println("Something went wrong");
            }
        }
    }
    public void saveAs(){
        FileDialog fileDialog=new FileDialog(textEditor.window,"Save",FileDialog.SAVE);
        fileDialog.setVisible(true);
        if(fileDialog.getFile()!=null){
           fileName=fileDialog.getFile();
           directory=fileDialog.getDirectory();
           textEditor.window.setTitle(fileName);
        }
        try {
            FileWriter fw=new FileWriter(directory+fileName);
            fw.write(textEditor.textArea.getText());
            fw.close();

        }
        catch (Exception e){
            System.out.println("Something went wrong");
        }
    }

    public void exit(){
        System.exit(0);
    }
    public void changeFontSize() {
        String input =JOptionPane.showInputDialog(textEditor.window, "Enter Font Size:");
        try {
            int fontSize=Integer.parseInt(input);
            Font font =textEditor.textArea.getFont().deriveFont((float)fontSize);
            textEditor.textArea.setFont(font);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(textEditor.window, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void changeFontFamily() {
        String[] fonts =GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont =(String)JOptionPane.showInputDialog(textEditor.window, "Select Font Family:", "Font Family", JOptionPane.PLAIN_MESSAGE, null, fonts, fonts[2]);
        if (selectedFont !=null) {
            Font font =new Font(selectedFont, Font.PLAIN, textEditor.textArea.getFont().getSize());
            textEditor.textArea.setFont(font);
        }
    }

    public void undo(){
        textEditor.um.undo();
    }
    public void redo(){
        textEditor.um.redo();
    }
}