/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirror;

import magicmirror.ui.mainUI;
import javassist.*;

/**
 *
 * @author Machiavelli
 */
public class MagicMirror {
    public static ClassPool reflectingPool = ClassPool.getDefault(); // the global class pool.

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        mainUI.main(null); // load GUI
        
       
    }
    
}
