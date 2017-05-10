/*
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
        System.out.println("\nTesting GitHub another test");
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter source location : ");
        String source = scanner.next();
        FileSystem fileSystem = FileSystems.getDefault();
        if (Files.isDirectory(fileSystem.getPath(source))) {
            try {
                System.out.println("Enter target location : ");
                String target = scanner.next();
                scanner.nextLine();
                System.out.println("Enter the names of file you wish to move or enter full path of csv file containing filenames (Separate them with ,)  : ");
                String fileNames = scanner.nextLine();
                String[] files = fileNames.split(",");
                for (int i = 0; i < files.length; i++) {
                    files[i] = files[i].trim();
                    if (fileExists(files, files[i])) {
                        System.err.println("You have provided duplicate filenames. Please provide unique filenames!");
                        System.exit(0);
                    }
                }
                if (files.length > 0) {
                    List<String> filesFound = new ArrayList<String>();
                    List<String> filesNotFound = new ArrayList<String>();
                    for (String fileName : files) {
                        if (!fileName.equals("*") && !fileName.equals("*.*") && !fileName.startsWith("*.") && !fileName.endsWith(".*") && fileSystem.getPath(fileName).isAbsolute()) {
                            System.out.println("Please wait..Reading filenames from " + fileName + " file!");
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
                                String allFiles = "", line = null;
                                while ((line = reader.readLine()) != null) {
                                    allFiles += line;
                                }
                                if (allFiles == "") {
                                    System.err.println("You haven't specified any filenames in " + fileName + " file!");
                                    break;
                                }
                                String[] filenames = allFiles.split(",");
                                for (int i = 0; i < filenames.length; i++) {
                                    filenames[i] = filenames[i].trim();
                                    if (fileAlreadyExists(files, filenames[i])) {
                                        System.err.println("You have provided duplicate filenames in " + fileName + " file. Please provide unique filenames!");
                                        System.exit(0);
                                    }
                                    if (Files.isRegularFile(fileSystem.getPath(source + "\\" + filenames[i])))  filesFound.add(filenames[i]);
                                    else    filesNotFound.add(filenames[i]);
                                }
                            } catch (IOException e) {
                                System.err.println("Some file error occurred while reading " + fileName + " file!");
                                e.printStackTrace();
                            }
                        } else if (fileName.equals("*") || fileName.equals("*.*")) {
                            System.out.println("Please wait..Moving all files to the target location!");
                            Stream<Path> allFiles = Files.list(fileSystem.getPath(source));
                            Iterator<Path> iterator = allFiles.iterator();
                            while (iterator.hasNext()) {
                                String path = iterator.next().toString();
                                String file = path.substring(path.lastIndexOf("\\") + 1, path.length());
                                if (filesFound.indexOf(file) == -1)   filesFound.add(file);
                            }
                        } else if (fileName.startsWith("*.") || fileName.endsWith(".*")) {
                            Stream<Path> allFiles = Files.list(fileSystem.getPath(source));
                            Iterator<Path> iterator = allFiles.iterator();
                            while (iterator.hasNext()) {
                                String path = iterator.next().toString();
                                if (fileName.startsWith("*.")) {
                                    if (path.endsWith(fileName.substring(fileName.lastIndexOf("."), fileName.length()))) {
                                        path = path.substring(path.lastIndexOf("\\") + 1, path.length());
                                        if (filesFound.indexOf(path) == -1)   filesFound.add(path);
                                    }
                                } else if (fileName.endsWith(".*")) {
                                    path = path.substring(path.lastIndexOf("\\") + 1, path.length());
                                    if (path.startsWith(fileName.substring(0, fileName.lastIndexOf("."))))
                                        if (filesFound.indexOf(path) == -1)   filesFound.add(path);
                                }
                            }
                        } else {
                            if (fileExists(files, fileName)) {
                                System.err.println("You have provided duplicate filenames. Please provide unique filenames!");
                                System.exit(0);
                            }
                            if (Files.isRegularFile(fileSystem.getPath(source + "\\" + fileName)))  filesFound.add(fileName);
                            else    filesNotFound.add(fileName);
                        }
                    }
                    if (filesNotFound.size() > 0) {
                        if (filesNotFound.size() > 1) {
                            System.out.println("\nFollowing files do not exist -");
                            for (String file : filesNotFound) System.out.println(file);
                        } else  System.err.println("\nFile " + filesNotFound.get(0) + " does not exist!");
                        if (filesFound.size() > 0) {
                            System.out.println("\nDo you want to move the other files? Type yes to continue or no to abort.");
                            String decision = scanner.next();
                            if (decision.equals("") || decision.equalsIgnoreCase("no")) System.exit(0);
                        } else  System.exit(0);
                    }
                    if (filesFound.size() > 0) {
                        moveFiles(fileSystem, source, target, filesFound);
                        System.out.println("\nAll files moved to the target location!");
                    }
                    else {
                        System.err.println("These files are not present in source location!");
                        System.exit(0);
                    }
                } else {
                    System.err.println("You haven't specified any file!");
                    System.exit(0);
                }
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
                else    System.err.println("Failed to move the file " + fileName);
            else    System.err.println("File " + fileName + " does not exist!");
    }
    private static boolean fileExists(String[] files, String fileName) {
        int count = 0;
        for (String file : files)   if (file.equalsIgnoreCase(fileName))    count++;
        if (count > 1)  return true;
        return false;
    }
    private static boolean fileAlreadyExists(String[] files, String fileName) {
        for (String file : files)   if (file.equalsIgnoreCase(fileName))    return true;
        return false;
    }
}*/
