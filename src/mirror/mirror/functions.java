/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirror.mirror;

import java.util.logging.Level;
import mirror.mirror.JarHandler;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javassist.ClassPath;
import javassist.URLClassPath;
import magicmirror.ui.mainUI;
/**
 *
 * @author Machiavelli
 */
    public class functions 
    {

        private static Class mainClazz;
        private static Object mainInstance;
        private static Method[] methods = new Method[3000];
        private static Field[] fields = new Field[3000];
        private static List<Class> classes = new ArrayList<>();
        private static List<Field> instances = new ArrayList<>();

        public static Method[][] allMethods = new Method[1500][3000];
        public static Field[][] allFields = new Field[1500][3000];

        public static Method getMethod(String s) {
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(s)) 
                {
                    return methods[i];
                }
            }
            return null;
       }
        
        public static Field getField(String className, String fieldName) {
            int classId = getClassId(className);
             for (int i = 0; i < allFields[classId].length; i++) {
                if (allFields[classId][i].getName().equals(fieldName)) 
                {
                    return allFields[classId][i];
                }
            }
            return null;
        }
        
        public static Field getField(String FQDN) {
            String className = "";
            String[] parts = FQDN.split("\\.");
            String fieldName = parts[parts.length - 1];
            
            for(int i=0; i<parts.length-1;i++) {
                className += parts[i];
                System.out.println(className);
                if(i<parts.length-2) {
                    className += ".";
                }
            }
            
            int classId = getClassId(className);
             for (int i = 0; i < allFields[classId].length; i++) {
                if (allFields[classId][i].getName().equals(fieldName)) 
                {
                    return allFields[classId][i];
                }
            }
            return null;
        }

        public static Class getClass(String s) {
            for (int i = 0; i < classes.size(); i++) {
                if (classes.get(i).getName().equals(s)) 
                {
                    return classes.get(i);
                }
            }
            return null;
       }

        public static int getClassId(String s) {
            for (int i = 0; i < classes.size(); i++) {
                if (classes.get(i).getName().equals(s)) 
                {
                    return i;
                }
            }
            return -1;
       }
        
        public static void setIntField(String FQDN, int Value) {
            String className = "";
            String[] parts = FQDN.split("\\.");
            String fieldName = parts[parts.length - 1];
            
            for(int i=0; i<parts.length-1;i++) {
                className += parts[i];
                System.out.println(className);
                if(i<parts.length-2) {
                    className += ".";
                }
            }
            
            Class fieldContainer = classes.get(getClassId(className));
                 try {
                    Constructor[] constructors = fieldContainer.getDeclaredConstructors();
                        constructors[0].setAccessible(true);
                        try {
                            getField(className, fieldName).setAccessible(true);
                            getField(className, fieldName).set(mainInstance, (Object) Value);
                        } catch(Exception ex) {
                            getField(className, fieldName).setAccessible(true);
                            getField(className, fieldName).set(constructors[0].newInstance(), (Object) Value);
                        }
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            
        }
        
        public static void setStrField(String FQDN, String Value) {
            String className = "";
            String[] parts = FQDN.split("\\.");
            String fieldName = parts[parts.length - 1];
            
            for(int i=0; i<parts.length-1;i++) {
                className += parts[i];
                System.out.println(className);
                if(i<parts.length-2) {
                    className += ".";
                }
            }
            
            Class fieldContainer = classes.get(getClassId(className));
                 try {
                    Constructor[] constructors = fieldContainer.getDeclaredConstructors();
                        constructors[0].setAccessible(true);
                        try {
                            getField(className, fieldName).setAccessible(true);
                            getField(className, fieldName).set(mainInstance, (Object) Value);
                        } catch(Exception ex) {
                            getField(className, fieldName).setAccessible(true);
                            getField(className, fieldName).set(constructors[0].newInstance(), (Object) Value);
                        }
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            
        }
        
        /*
        Scanning Functions begin below
        */
        
        public static List<Field> scanInt(int scanVal) {
            List<Field> returnVal = new ArrayList<>();
            
            int index = 0;
            for (Field[] allField : allFields) {
                for (Field allField1 : allField) {
                    if(allField1 == null)
                       break;
                    
                    if(allField1.getType() == int.class) {
                        allField1.setAccessible(true);
                        try {
                        if(allField1.getInt(mainInstance) == scanVal) { //i get better results using newinstance, but it fucks the everything
                            returnVal.add(allField1);
                        } 
                        }catch (Exception ex) {
                            try {
                                Constructor[] constructors = allField1.getDeclaringClass().getDeclaredConstructors();
                                    int noArgs = -1;
                                    if(constructors.length > 0) {
                                        for(int ii = 0;  ii < constructors.length; ii++) {
                                            if(constructors[ii].getGenericParameterTypes().length == 0)
                                                noArgs = ii;
                                                break;
                                        }
                                        if(noArgs != -1) {
                                            constructors[noArgs].setAccessible(true);
                                            if(allField1.getInt(constructors[noArgs].newInstance()) == scanVal) { //i get better results using newinstance, but it fucks the everything
                                            returnVal.add(allField1);
                                            } 
                                        }
                                    }
                            } catch (java.lang.IllegalArgumentException ex2) {
                                System.out.println("Unable to process field " + allField1.getName() + " in class " + allField1.getDeclaringClass());
                                //ex2.printStackTrace();
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                        }
                    }
                }
                index++;
            }
            return returnVal;
        }
        
        public static List<Field> scanStr(String scanVal, int scanType) {
            
            List<Field> returnVal = new ArrayList<>();
            
            int index = 0;
            for (Field[] allField : allFields) {
                for (Field allField1 : allField) {
                    if(allField1 == null)
                       break;
                    
                    if(allField1.getType() == String.class) {
                        allField1.setAccessible(true);
                        try {
                        if(scanType != 1) {
                            if(allField1.get(mainInstance).toString().contains(scanVal)) { //i get better results using newinstance, but it fucks the everything
                                returnVal.add(allField1);
                            } 
                        } else {
                            if(allField1.get(mainInstance).toString().equalsIgnoreCase(scanVal)) { //i get better results using newinstance, but it fucks the everything
                                returnVal.add(allField1);
                            } 
                        }
                        }catch (Exception ex) {
                            //ex.printStackTrace();
                            try {
                                Constructor[] constructors = allField1.getDeclaringClass().getDeclaredConstructors();
                                if(constructors.length > 0) {
                                    int noArgs = -1;
                                    for(int ii = 0;  ii < constructors.length; ii++) {
                                        if(constructors[ii].getGenericParameterTypes().length == 0) {
                                            noArgs = ii;
                                            break;
                                        }
                                    }
                                    if(noArgs != -1) {
                                        constructors[noArgs].setAccessible(true);
                                        
                                        Object fieldVal1 = allField1.get(constructors[noArgs].newInstance());
                                        if(fieldVal1 == null) {
                                            break;
                                        }
                                        String fieldVal = fieldVal1.toString();
                                        if(scanType != 1) {
                                            if(fieldVal.equalsIgnoreCase(scanVal)) { //i get better results using newinstance, but it fucks the everything
                                                returnVal.add(allField1);
                                            } 
                                        } else {
                                            if(fieldVal.contains(scanVal)) { //i get better results using newinstance, but it fucks the everything
                                                returnVal.add(allField1);
                                            }    
                                        }
                                    }
                                }
                            } catch (java.lang.IllegalArgumentException ex2) {
                                System.out.println("Unable to process field " + allField1.getName() + " in class " + allField1.getDeclaringClass() + " Due to Illegal Argument Exception");
                                //ex2.printStackTrace();
                            } catch (Exception ex2) {
                                System.out.println("Unable to process field " + allField1.getName() + " in class " + allField1.getDeclaringClass() + " Due to an " + ex2.getLocalizedMessage());
                                ex2.printStackTrace();
                            }
                        }
                    }
                }
                index++;
            }
            //System.out.println(scanType);
            return returnVal;
        }
        
        public static List<Field> reScanInt(int scanVal, List<String> FQDN) 
        {
            List<Field> returnVal = new ArrayList<>();
            
            int index = 0;
            for (String targetField : FQDN) {
                try {
                        if(getField(targetField).getInt(mainInstance) == scanVal) { //i get better results using newinstance, but it fucks the everything
                            returnVal.add(getField(targetField));
                        } 
                        }catch (Exception ex) {
                            try {
                                Constructor[] constructors = getField(targetField).getDeclaringClass().getDeclaredConstructors();
                                if(constructors.length > 0) {
                                    constructors[0].setAccessible(true);
                                    if(getField(targetField).getInt(constructors[0].newInstance()) == scanVal) { //i get better results using newinstance, but it fucks the everything
                                        returnVal.add(getField(targetField));
                                    } 
                                }
                            } catch (java.lang.IllegalArgumentException ex2) {
                                System.out.println("Unable to reprocess field " + getField(targetField).getName() + " in class " + getField(targetField).getDeclaringClass());
                                //ex2.printStackTrace();
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }                   
                        }
            }
            
            return returnVal;
        }
        
        public static Class[] classListToArray(List<Class> Params) {
            Object[] intermediary = Params.toArray();
            Class[] returnClasses = new Class[intermediary.length];
            int i = 0;
            for(Object myClass : intermediary) {
                returnClasses[i] = (Class) myClass;
                i++;
            }
            i = 0;
            return returnClasses;
        }
        
        public static List<String> getClassNames(String pathToJar) throws FileNotFoundException, IOException {
            List<String> classNames = new ArrayList<String>();
            ZipInputStream zip = new ZipInputStream(new FileInputStream(pathToJar));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    // This ZipEntry represents a class. Now, what class does it represent?
                    String className = entry.getName().replace('/', '.'); // including ".class"
                    classNames.add(className.substring(0, className.length() - ".class".length()));
                }
            }
            return classNames;
        }
        public void startClient(String cPath, String mC, String mM) {
            try {
                startClient(cPath, mC, mM, null);
            } catch (IOException ex) {
                Logger.getLogger(functions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(functions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(functions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(functions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(functions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(functions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        public void startClient(String cPath, String mC, String mM, String cA) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
                try {
                    List<Object> argsOut = new ArrayList<Object>();
                    List<Class> varTypes = new ArrayList<Class>();
                    if(cA != null) {
                        String[] conArgs = cA.split(",");
                        for(String arg1 : conArgs) {
                            String[] args1 = arg1.split(":");
                            String type = args1[0];
                            String value = args1[1];
                            if(type.equalsIgnoreCase("int")) {
                                argsOut.add(Integer.parseInt(value));
                                varTypes.add(int.class);
                            }else if(type.equalsIgnoreCase("string")) {
                                argsOut.add(value);
                                varTypes.add(java.lang.String.class);
                            }else if(type.equalsIgnoreCase("long")) {
                                argsOut.add(Long.parseLong(value));
                                varTypes.add(long.class);
                            }else if(type.equalsIgnoreCase("boolean")) {
                                argsOut.add(Boolean.parseBoolean(value));
                                varTypes.add(boolean.class);
                            }
                        }
                    }
                    List<String> classNames = getClassNames(cPath);
                    System.out.println(classNames.toString());
                    File cFile = new File(cPath);

                    //add out classpath, and save it so we can reference it later
                    ClassPath cp = mirror.MagicMirror.reflectingPool.appendClassPath(cPath); 
                    CtClass mainClass = mirror.MagicMirror.reflectingPool.get(mC);
                    CtMethod main = mainClass.getDeclaredMethod(mM);

                    //code injection is literally this easy
                    //main.insertBefore("{ System.out.println(\"Code injection live!\"); }");

                    //get the modified class bytecode
                    byte[] b = mainClass.toBytecode();
                    //remove the jar from our classpath so we can modify it
                    mirror.MagicMirror.reflectingPool.removeClassPath(cp);

                    //Swap the class in the jar.
                    JarHandler jarHandler = new JarHandler();
                    jarHandler.replaceJarFile(cPath, b, mC.replace(".", "/") + ".class");

                    //Now, we want to start the client. Since we're injecting reflection we could simply start it,
                    //However, we'll reflect it here as well, since it's a faster way to approach scan-by-val.
                    URL[] urls = new URL[]{cFile.toURI().toURL()};
                    ClassLoader cl = new URLClassLoader(urls);
                    //now we want to load the classes from our name array into class objects to reflect, we don't want to just scan the main class!
                    for(int i=0; i<classNames.size(); i++) {
                        Method[] iMethods;
                        Class tempClass;
                        try{
                        //System.out.println(classNames.get(i));
                        tempClass = cl.loadClass(classNames.get(i));
                        classes.add(tempClass);
                        //instances.add(tempClass.newInstance());
                        
                        
                        iMethods = tempClass.getDeclaredMethods();
                        for(int j=0; j<iMethods.length; j++) {
                            allMethods[i][j] = iMethods[j];
                            allMethods[i][j].setAccessible(true); //duh
                        }
                        } catch(NoClassDefFoundError ex) {
                               System.out.println("Bad class!");
                               tempClass = null;
                        }
                        Field[] iFields;
                        if(tempClass != null){
                            try {
                            iFields = tempClass.getDeclaredFields();
                            for(int j=0; j<iFields.length; j++) {
                                //System.out.println(iFields[j].getName());
                                allFields[i][j] = iFields[j];
                                allFields[i][j].setAccessible(true); //duh
                            }
                            } catch(NoClassDefFoundError ex) {
                                System.out.println("Bad class!");
                            }
                        }
                        iFields = null;
                        iMethods = null;
                    }
                    //once we have all our classes loaded, we want to grab all the fields and methods to reference later.

                    /*for(int i=0; i<classes.size();i++) {
                        Method[] iMethods = classes.get(i).getDeclaredMethods();
                        for(int j=0; j<iMethods.length; i++) {
                            allMethods[i][j] = iMethods[j];
                            allMethods[i][j].setAccessible(true); //duh
                        }
                        iMethods = null;
                    }
                    for(int i=0; i<classes.size();i++) {
                        Field[] iFields = classes.get(i).getDeclaredFields();
                        for(int j=0; j<iFields.length; i++) {
                            allFields[i][j] = iFields[j];
                            allFields[i][j].setAccessible(true); //duh
                        }
                        iFields = null;
                    }*/

                    //Let's finally get our main method ready to go!
                    mainClazz = cl.loadClass(mC);
                    methods = mainClazz.getMethods();
                    fields = mainClazz.getFields();
                    
                    try {
                        mainInstance = mainClazz.newInstance();
                    } catch (Exception exodia) {
                        if(cA != null)
                            mainInstance = mainClazz.getConstructor(classListToArray(varTypes)).newInstance(argsOut.toArray());
                        else
                            System.out.println("Error: No-Args constructor not found! Please verify constructor arguments box is checked!");
                    }
                    
                    Method invokeMe = getMethod(mM);
                    invokeMe.setAccessible(true);
                    invokeMe.invoke(mainInstance, new Object[] { new String[] { null } });


                } catch (NotFoundException ex) {
                    Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CannotCompileException ex) {
                    Logger.getLogger(mainUI.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

}
