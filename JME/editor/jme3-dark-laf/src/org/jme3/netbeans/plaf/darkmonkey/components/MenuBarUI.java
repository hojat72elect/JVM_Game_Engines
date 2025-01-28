/*package org.jme3.netbeans.plaf.darkmonkey.components;

import com.apple.laf.AquaMenuBarUI;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.*;
import org.jme3.netbeans.plaf.darkmonkey.DarkMonkeyLookAndFeel;

public class MenuBarUI extends AquaMenuBarUI {
  public static ComponentUI createUI( JComponent x) {
    MenuBarUI a = new MenuBarUI();
    a.ProcessAppleControl(null);
    return a;
  }
  
  // Thanks to bloodwalker for this
  private void ProcessAppleControl(JMenuBar j)
  {
   try {
        Class<?> clazz = Class.forName("com.apple.eawt.Application");
        Method method = clazz.getMethod("getApplication");
        Object obj = method.invoke(null);
        method = clazz.getMethod("setDefaultMenuBar", JMenuBar.class);
        method.invoke(obj, j); //Your custom MenuBar
        method = clazz.getMethod("enableSuddenTermination");
        method.invoke(obj);
   } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        Logger.getLogger(DarkMonkeyLookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
    }
}

  public void paint( Graphics g, JComponent c) {
    super.paint( g, c);
  }

    @Override
    public void update(Graphics g, JComponent c) {
        super.update(g, c);
        if (c instanceof JMenuBar) {
            ProcessAppleControl((JMenuBar)c); // For this to work, we HAVE TO extend AquaMenuBarUI
        }
    }
  
    /* Doesn't work since AquaMenuBarUI isn't found by the default Compiler
    @Override
    boolean setScreenMenuBar(final JFrame jframe) {
        return false;
    }*/
//}
