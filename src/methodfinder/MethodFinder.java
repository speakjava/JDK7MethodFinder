/**
 * JDK8 library analyser project
 * 
 * Copyright © 2014, Oracle and/or its affiliates. All rights reserved.
 */
package methodfinder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Find all the classes and methods in the rt.jar file and write these to a
 * file.
 *
 * @author Simon Ritter (@speakjava)
 */
public class MethodFinder {
  private static final String outputFile = "/tmp/jdk7-classes.txt";

  /**
   * Constructor
   *
   * @param rtDotJarFile The path of the rt.jar file
   * @throws IOException If the file can't be read
   */
  public MethodFinder(String rtDotJarFile) throws IOException {
    ArrayList<String> classList = new ArrayList<>();
    JarFile rtJar = new JarFile(rtDotJarFile);
    Enumeration<JarEntry> classes = rtJar.entries();

    /**
     * Start by looking at each file in the rt.jar file and find classes that
     * are part of the java, javax or org packages.
     */
    while (classes.hasMoreElements()) {
      JarEntry libClass = classes.nextElement();
      String className = libClass.getName();

      /**
       * Ignore inner classes and convert from a path to a class reference
       */
      if (className.endsWith(".class")
          && (className.startsWith("java") || className.startsWith("org"))
          && !className.contains("$")) {
        className = className.replace(".class", "");
        className = className.replace("/", ".");
        classList.add(className);
      }
    }

    int methodCount = 0;

    try (PrintStream output
        = new PrintStream(new FileOutputStream(outputFile))) {
      /**
       * Take the list and use reflection to look at each class and find
       * all the methods that are available and the parameters that they take.
       */
      for (String c : classList) {
        /* Get a class reference for this type so we can use reflection */
        try {
          Class type = Class.forName(c);
          Method[] methods = type.getDeclaredMethods();
          
          /**
           * Make sure we save the name of the class, even if there are
           * no methods
           */
          if (methods.length == 0)
            output.println(type.getName());

          /* Record details of any public methods */
          for (Method m : methods) {
            if (Modifier.isPublic(m.getModifiers())) {
              methodCount++;
              StringBuilder methodString = new StringBuilder();
              methodString.append(type.getName());
              methodString.append(",");
              methodString.append(m.getName());
              Class[] params = m.getParameterTypes();

              /* Record the parameter types */
              for (Class p : params) {
                methodString.append(",");
                methodString.append(p.getName());
              }
              
              output.println(methodString);
            }
          }
        } catch (ClassNotFoundException cnfe) {
          System.out.println("Class not found: " + c);
        }
      }
    }
    
    System.out.println("Found " + classList.size() + 
        " classes, with " + methodCount + " public methods");
  }
}