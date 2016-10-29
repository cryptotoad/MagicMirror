/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magicmirror.ui;

import java.util.logging.Level;
import mirror.mirror.JarHandler;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import javassist.ClassPath;
import javassist.URLClassPath;
/**
 *
 * @author Machiavelli
 */
    public class UIFunctions {
        private static Method[] methods = null;
        private static Field[] fields = null;
        
    public static Method getMethod(String s) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(s)) 
            {
                return methods[i];
            }
        }
        return null;
   }

    public void startClient(String cPath, String mC) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            try {
                File cFile = new File(cPath);
                
                //add out classpath, and save it so we can reference it later
                ClassPath cp = mirror.MagicMirror.reflectingPool.appendClassPath(cPath); 
                CtClass mainClass = mirror.MagicMirror.reflectingPool.get(mC);
                CtMethod main = mainClass.getDeclaredMethod("main");
                
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
                Class c = cl.loadClass(mC);
                
                //Let's finally get our main method ready to go
                methods = c.getMethods();
                fields = c.getFields();
                Method invokeMe = getMethod("main");
                invokeMe.invoke(c, new Object[] { new String[] { "a", "a" } });
                
                
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
