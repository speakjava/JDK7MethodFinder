/**
 * JDK8 library analyser project
 * 
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package methodfinder;

import java.io.IOException;

/**
 * Find and record all the classes and methods available in the JDK7 class
 * libraries.
 * 
 * @author simonri
 */
public class Main {
  /**
   * Main entry point
   * 
   * @param args the command line arguments
   * @throws IOException If a file can't be read or written to
   */
  public static void main(String[] args) throws IOException {
    /* Check that we have an argument telling us where the rt.jar file is */
    if (args.length < 1) {
      System.out.println("Usage: MethodFinder <rt.jar>");
      System.exit(1);
    }
    
    MethodFinder finder = new MethodFinder(args[0]);
  }
}