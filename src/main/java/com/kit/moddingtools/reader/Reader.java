package com.kit.moddingtools.reader;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Reader {
  /**
   * Scan a file and return the lines.
   * 
   * @param path
   *             The path to the file.
   * @return
   *         The lines of the file.
   */
  public static String[] read(String path) {
    List<String> lines = new ArrayList<String>();

    try {
      BufferedReader reader = new BufferedReader(new java.io.FileReader(path));
      String line = null;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return lines.toArray(new String[0]);
  }

  /**
   * Get the paths of all files in a directory.
   * 
   * @param path
   *             The path to the directory.
   * @return
   *         The paths of all files in the directory.
   */
  public static List<String> listFiles(String path) {
    List<String> files = new ArrayList<String>();

    java.io.File folder = new java.io.File(path);
    java.io.File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        files.add(listOfFiles[i].getPath());
      }
    }

    return files;
  }

  /**
   * Get the paths of all sub-directories in a directory.
   * 
   * @param path
   *             The path to the directory.
   * @return
   *         The paths of all directories in the directory.
   */
  public static List<String> listSubdirectories(String path, String... exclude) {
    List<String> directories = new ArrayList<String>();

    java.io.File folder = new java.io.File(path);
    java.io.File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isDirectory()) {
        directories.add(listOfFiles[i].getPath());

        List<String> subDirectories = listSubdirectories(listOfFiles[i].getPath());
        if (subDirectories.size() > 0) {
          directories.addAll(listSubdirectories(listOfFiles[i].getPath()));
        }
      }
    }

    return directories;
  }
}
