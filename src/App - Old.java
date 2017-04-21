package com.zensar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter source location : ");
        String source = scanner.next();
        FileSystem fileSystem = FileSystems.getDefault();
        if (Files.isDirectory(fileSystem.getPath(source))) {
            try {
                System.out.println("Enter target location : ");
                String target = scanner.next();
                scanner.nextLine();
                System.out.println("Enter the names of file you wish to move (Separate filenames with ,) or enter full path of csv file : ");
                String fileNames = scanner.nextLine();
                if (!fileNames.equals("*") && !fileNames.equals("*.*") && !fileNames.startsWith("*.") && !fileNames.endsWith(".*") && fileSystem.getPath(fileNames).isAbsolute()) {
                    System.out.println("Please wait..Reading filenames from " + fileNames + " file!");
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(new File(fileNames)));
                        String allFiles = "", line=null;
                        while ((line = reader.readLine()) != null) {
                            allFiles += line;
                        }
                        if (allFiles == "") {
                            System.err.println("You haven't specified any filenames in " + fileNames + " file!");
                            System.exit(0);
                        }
                        String[] files = allFiles.split(",");
                        for (int i = 0; i < files.length; i++) {
                            files[i] = files[i].trim();
                            if (fileExists(files, files[i])) {
                                System.err.println("You have provided duplicate filenames in " + fileNames + " file. Please provide unique filenames!");
                                System.exit(0);
                            }
                        }
                        List<String> filesFound = new ArrayList<String>();
                        for (int i = 0; i < files.length; i++) {
                            files[i] = files[i].trim();
                            filesFound.add(files[i]);
                        }
                        if (filesFound.size() > 0) moveFiles(fileSystem, source, target, filesFound);
                        else {
                            System.err.println("You haven't specified any filenames in " + fileNames + " file!");
                            System.exit(0);
                        }
                    } catch (IOException e) {
                        System.err.println("Some file error occurred!");
                        e.printStackTrace();
                    }
                }
                else {
                    String[] files = fileNames.split(",");
                    for (int i = 0; i < files.length; i++) {
                        files[i] = files[i].trim();
                        if (fileExists(files, files[i])) {
                            System.err.println("You have provided duplicate filenames. Please provide unique filenames!");
                            System.exit(0);
                        }
                    }
                    List<String> filesFound = new ArrayList<String>();
                    if (fileNames.equals("*") || fileNames.equals("*.*")) {
                        System.out.println("Please wait..Moving all files to the target location!");
                        Stream<Path> allFiles = Files.list(fileSystem.getPath(source));
                        Iterator<Path> iterator = allFiles.iterator();
                        while (iterator.hasNext()) {
                            String path = iterator.next().toString();
                            filesFound.add(path.substring(path.lastIndexOf("\\") + 1, path.length()));
                        }
                        if (filesFound.size() > 0) moveFiles(fileSystem, source, target, filesFound);
                        else {
                            System.err.println("There are no files in source location!");
                            System.exit(0);
                        }
                    } else if (fileNames.startsWith("*.") || fileNames.endsWith(".*")) {
                        Stream<Path> allFiles = Files.list(fileSystem.getPath(source));
                        Iterator<Path> iterator = allFiles.iterator();
                        while (iterator.hasNext()) {
                            String path = iterator.next().toString();
                            if (fileNames.startsWith("*.")) {
                                if (path.endsWith(fileNames.substring(fileNames.lastIndexOf("."), fileNames.length())))
                                    filesFound.add(path.substring(path.lastIndexOf("\\") + 1, path.length()));
                            } else if (fileNames.endsWith(".*")) {
                                path = path.substring(path.lastIndexOf("\\") + 1, path.length());
                                if (path.startsWith(fileNames.substring(0, fileNames.lastIndexOf("."))))
                                    filesFound.add(path);
                            }
                        }
                        if (filesFound.size() > 0) moveFiles(fileSystem, source, target, filesFound);
                        else {
                            System.err.println("There are no files in source location!");
                            System.exit(0);
                        }
                    } else if (files.length > 0) {
                        List<String> filesNotFound = new ArrayList<String>();
                        for (String fileName : files)
                            if (Files.isRegularFile(fileSystem.getPath(source + "\\" + fileName)))
                                filesFound.add(fileName);
                            else filesNotFound.add(fileName);
                        if (filesNotFound.size() > 0) {
                            System.err.println("\nFollowing files do not exist -");
                            for (String file : filesNotFound) System.out.println(file);
                            if (filesFound.size() > 0) {
                                System.out.println("\nDo you want to move the other files? Type yes to continue or no to abort.");
                                String decision = scanner.next();
                                if (decision.equals("") || decision.equalsIgnoreCase("no")) System.exit(0);
                            } else System.exit(0);
                        }
                        if (filesFound.size() > 0) moveFiles(fileSystem, source, target, filesFound);
                        else {
                            System.err.println("These files are not present in source location!");
                            System.exit(0);
                        }
                    } else {
                        System.err.println("You haven't specified any file!");
                        System.exit(0);
                    }
                }
                System.out.println("\nAll files moved to target location!");
            } catch (IOException e) {
                System.err.println("Some file error occurred!");
                e.printStackTrace();
            }
        }
        else    System.err.println("Source location does not exist!");
        scanner.close();
        scanner = null;
    }
    private static void moveFiles(FileSystem fileSystem, String source, String target, List<String> allFiles) throws IOException {
        if (!Files.isDirectory(fileSystem.getPath(target)))     Files.createDirectories(fileSystem.getPath(target));
        for (String fileName : allFiles)
            if (Files.exists(fileSystem.getPath(source + "\\" + fileName)))
                if (Files.exists(Files.move(fileSystem.getPath(source + "\\" + fileName), fileSystem.getPath(target + "\\" + fileName))))
                    System.out.println("File " + fileName +" moved successfully to target location!");
                else System.err.println("Failed to move the file " + fileName);
            else    System.err.println("File " + fileName + " does not exist!");
    }
    private static boolean fileExists(String[] files, String fileName) {
        int count = 0;
        for (String file : files)   if (file.equalsIgnoreCase(fileName))    count++;
        if (count > 1)  return true;
        return false;
    }
}