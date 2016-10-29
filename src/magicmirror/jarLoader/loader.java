/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magicmirror.jarLoader;
import javassist.*;

/**
 *
 * @author Machiavelli
 */
public class loader {
    
    public void loadJarFromURL(String Host, String Path, String classPath, ClassPool pool) {
        ClassPath cp = new URLClassPath(Host, 80, Path, classPath);
        pool.insertClassPath(cp);
    }
    
    public void loadJarFromFile(String Host, String Path, String classPath, ClassPool pool) {
        ClassPath cp = new URLClassPath(Host, 80, Path, classPath);
        pool.insertClassPath(cp);
    }
}
