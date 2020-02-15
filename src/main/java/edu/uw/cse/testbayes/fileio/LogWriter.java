package edu.uw.cse.testbayes.fileio;

import edu.uw.cse.testbayes.utils.FileNameUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Writes Logs from the Test Run
 */
public class LogWriter {

    /**
     * Filename in which the data from the current run are saved
     */
    private static String filename;

    /**
     * Returns the Absolute Path of the file where the given data from the test run is stored
     *
     * @param testData  a map with test names mapping to the time taken to run the test. Time taken to be negative
     *                  if test fails
     * @return  Absolute Path of file with log or null if @testData is empty
     * @throws IOException
     */
    public static String write(Map<String, Double> testData) throws IOException {
        if(testData.size() == 0) {
            return null;
        }

        File file = getFile();
        PrintStream printStream = new PrintStream(file);
        for(String methodName : testData.keySet()) {
            printStream.print(methodName.replaceAll(" ", "%") + "," + testData.get(methodName) + " ");
        }
        printStream.println();
        printStream.close();
        return file.getAbsolutePath();
    }

    /**
     * Returns the Absolute Path of the file where the given data from the test run is stored
     *
     * @param methodName name of the method that was tested
     * @param num duration of the test run
     * @return Absolute Path of file with log or null if @testData is empty
     * @throws IOException
     */
    public static String write(String methodName, double num) throws IOException {
        File file = getFile();
        Scanner scanner = new Scanner(file);
        String s = "";
        if(scanner.hasNextLine()) {
            s = scanner.nextLine();
        }
        PrintStream printStream = new PrintStream(file);
        printStream.print(s + methodName.replaceAll(" ", "%") + "," + num + " ");
        printStream.close();
        scanner.close();
        return file.getAbsolutePath();
    }

    /**
     * Gets the File in which the logs are to be written
     *
     * @return File Object representing File in which the logs are to be written
     * @throws IOException
     */
    private static File getFile() throws IOException {
        String directoryName = FileNameUtils.getDirectoryName();
        File directory = new File(directoryName);
        if(!directory.exists()) {
            directory.mkdir();
        }
        if(filename == null) {
            filename = FileNameUtils.createFileName();
        }
        File file = new File(filename);
        file.createNewFile();
        return file;
    }

    /**
     * Forces a new file to be created for the next set of data points to be written to the file system
     */
    public static void forceNewFile() throws InterruptedException {
        filename = null;
        TimeUnit.SECONDS.sleep(1);
    }
  
}
