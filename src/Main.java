import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

enum FileType {
    txt, pdf, doc, xls, ppt, jpg, png, gif, mp3, mp4, other
}

// PrintJob.java
class PrintJob {
    private String fileName;
    private FileType fileType;

    public PrintJob(String fileName, FileType fileType) throws TypeNotSupportedException {
        this.fileName = fileName;
        this.fileType = fileType;
        checkFileType(); // Check if the file type is supported
    }

    private void checkFileType() throws TypeNotSupportedException {
        // List of supported file types (example)
        FileType[] supportedTypes = { FileType.txt, FileType.pdf, FileType.doc, FileType.xls, FileType.ppt,
                FileType.jpg, FileType.png };

        // Check if the file type is supported
        for (FileType type : supportedTypes) {
            if (type == fileType) {
                return; // File type is supported
            }
        }

        // If the file type is not supported, throw an exception
        throw new TypeNotSupportedException("File type not supported: " + fileType);
    }

    // Getters and other methods...
    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }
}

// TypeNotSupportedException.java
class TypeNotSupportedException extends Exception {
    public TypeNotSupportedException(String message) {
        super(message);
    }
}

// Computer.java
class Computer extends Thread {
    private SharedQueue queue;

    public Computer(SharedQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        // Simulating the creation of 50 print jobs by computers
        try {
            for (int i = 0; i < 50; i++) {
                try {// Get a random file type
                    FileType fileType = FileType.values()[(int) (Math.random() * FileType.values().length)];
                    // Create a print job with a random file name and the random file type
                    String fileName = "file" + System.currentTimeMillis();
                    PrintJob job = new PrintJob(fileName, fileType);

                    // Add the print job to the shared queue
                    queue.add(job);
                }
                // Handling the exception if the file type is not supported
                catch (TypeNotSupportedException e) {
                    System.out.println(Thread.currentThread().getName() + " Error: " + e.getMessage()
                            + ". Try with a different file type.");
                } finally {
                    // Wait for a random time below 3000 miliseconds before creating the next print
                    // job
                    Thread.sleep((int) (Math.random() * 2300));
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Computer thread interrupted.");
        }
    }
}

// Printer.java
class Printer extends Thread {
    private SharedQueue queue;

    public Printer(SharedQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                queue.remove();
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            System.out.println("Printer thread interrupted.");
        }
    }
}

// SharedQueue.java
class SharedQueue {
    private final int maxSize;
    private final LinkedList<PrintJob> queue;

    public SharedQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedList<>();
    }

    public synchronized void add(PrintJob job) throws InterruptedException {
        while (queue.size() == maxSize) {
            System.out.println("Queue is full. " + Thread.currentThread().getName() + " is waiting...");
            wait(); // Wait if the queue is full
        }
        queue.add(job);
        System.out.println(Thread.currentThread().getName() + " Added " + job.getFileName() + '.'
                + job.getFileType().toString() + " to the queue.");
        notifyAll(); // Notify waiting threads
    }

    public synchronized void remove() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // Wait if the queue is empty
        }
        PrintJob job = queue.removeFirst();
        System.out.println(Thread.currentThread().getName() + " Removed " + job.getFileName() + '.'
                + job.getFileType().toString() + " from the queue.");
        notifyAll(); // Notify waiting threads
        return;
    }
}

// TextFile.java(test file contains file name and a file type)
class TextFile {
    private String fileType;
    private String fileName;

    public TextFile(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        if (fileType.equals("txt")) {
            return FileType.txt;
        } else if (fileType.equals("pdf")) {
            return FileType.pdf;
        } else if (fileType.equals("doc")) {
            return FileType.doc;
        } else if (fileType.equals("xls")) {
            return FileType.xls;
        } else if (fileType.equals("ppt")) {
            return FileType.ppt;
        } else if (fileType.equals("jpg")) {
            return FileType.jpg;
        } else if (fileType.equals("png")) {
            return FileType.png;
        } else {
            return FileType.other;
        }
    }
}

class ComputerOnline {

    private SharedQueue queue;

    public ComputerOnline(SharedQueue queue) {
        this.queue = queue;
    }

    public void run() {
        // Simulating the creation of 50 print jobs by using TextFile
        for (int i = 0; i < 10; i++) {
            String directoryPath = "D:\\University Projects\\Programming Constructions\\ShineComputers\\OnlineJobs\\";

            try {
                String fileName = "i" + ".txt"; // Generate file name based on loop variable i
                String filePath = directoryPath + fileName;
                TextFile textFile = Main.readAFile(filePath);

                // Create a print job with the file name and file type from the TextFile
                PrintJob job = new PrintJob(textFile.getFileName(), textFile.getFileType());

                // Add the print job to the shared queue
                queue.add(job);

            }
            // Handling the exception if the file type is not supported
            catch (TypeNotSupportedException e) {
                System.out.println(Thread.currentThread().getName() + " Error: " + e.getMessage()
                        + ". Try with a different file type.");
            } catch (Exception e) {
                System.out.println("Error reading the file: " + e.getMessage());
            }
        }
    }

}

// Main.java
public class Main {

    // readAFile method
    public static TextFile readAFile(String filePath) throws Exception {

        FileReader file = new FileReader(filePath);
        Scanner scanner = new Scanner(file);
        String ContentName = scanner.nextLine();
        String ContentType = scanner.nextLine();
        TextFile textFile = new TextFile(ContentName, ContentType);
        scanner.close();
        return textFile;

    }

    public static void main(String[] args) {
        SharedQueue queue = new SharedQueue(5);

        // Create and start 3 Computer threads
        Computer computer1 = new Computer(queue);
        Computer computer2 = new Computer(queue);
        Computer computer3 = new Computer(queue);
        computer1.start();
        computer2.start();
        computer3.start();
        computer1.setName("Computer 1");
        computer2.setName("Computer 2");
        computer3.setName("Computer 3");

        // Create and start 2 Printer threads
        Printer printer1 = new Printer(queue);
        Printer printer2 = new Printer(queue);
        printer1.start();
        printer2.start();
        printer1.setName("Printer 1");
        printer2.setName("Printer 2");

        // Wait for the Printer threads to finish
        try {
            printer1.join();
            printer2.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
        }
    }

    // ReadAFile method

}
