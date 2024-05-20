import java.io.File;
import java.util.Scanner;

public class LinkerLoader {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        
        System.out.print("Enter available memory size in KB: ");
        int memorySizeKB = scanner.nextInt();
        scanner.nextLine(); 
        
       
        long fileSize1KB = getFileSize("file1.txt");
        long fileSize2KB = getFileSize("file2.txt");
        System.out.println("Size of file1 " +" " + fileSize1KB+"kb"+  "\n Size of file2" + " " + fileSize2KB +"kb");
        
        
        if (fileSize1KB == -1 || fileSize2KB == -1) {
            return;
        }
       
        if (memorySizeKB < fileSize1KB) {
            System.out.println("File 1 cannot be loaded. Insufficient memory.");
            return;
        }
        if (memorySizeKB < fileSize1KB + fileSize2KB) {
            System.out.println("File 1 loaded but File 2 cannot be loaded. Insufficient memory.");
            return;
        }
        
     
        System.out.println("File 1 loaded from location 0");
        System.out.println("File 1 ends at " + fileSize1KB + " KB");
        System.out.println("File 2 loaded from location " + (fileSize1KB + 1) + " KB");
        System.out.println("File 2 ends at " + (fileSize1KB + fileSize2KB) + " KB");
    }

    private static long getFileSize(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return file.length();
        } else {
            System.out.println("File " + filename + " does not exist.");
            return -1;
        }
    }
}
