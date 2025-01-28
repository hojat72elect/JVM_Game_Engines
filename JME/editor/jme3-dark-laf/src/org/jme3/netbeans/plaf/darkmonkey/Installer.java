/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jme3.netbeans.plaf.darkmonkey;

import javax.swing.UIManager;
import org.netbeans.modules.editor.settings.storage.api.EditorSettings;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    private static boolean isFirstInstallation = false;
    
    @Override
    public void restored() {
        DarkMonkeyLookAndFeel darkMonkeyLaf = new DarkMonkeyLookAndFeel();
        UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo(
                darkMonkeyLaf.getName(),
                DarkMonkeyLookAndFeel.class.getName()));
        UIManager.put("Nb.DarkMonkeyLFCustoms", new DarkMonkeyLFCustoms(darkMonkeyLaf));

        String[] fontsToLoad = {
            "fonts/DejaVuSans.ttf",
            "fonts/DejaVuSans-Bold.ttf",
            "fonts/DejaVuSans-Oblique.ttf",
            "fonts/DejaVuSans-BoldOblique.ttf",
            "fonts/DejaVuSansCondensed.ttf",
            "fonts/DejaVuSansCondensed-Bold.ttf",
            "fonts/DejaVuSansCondensed-Oblique.ttf",
            "fonts/DejaVuSansCondensed-BoldOblique.ttf",
            "fonts/DejaVuSansMono.ttf",
            "fonts/DejaVuSansMono-Bold.ttf",
            "fonts/DejaVuSansMono-Oblique.ttf",
            "fonts/DejaVuSansMono-BoldOblique.ttf"
        };
        DMUtils.loadFontsFromJar(this, fontsToLoad);
        
        if(isFirstInstallation) {
            EditorSettings setting = org.netbeans.modules.editor.settings.storage.api.EditorSettings.getDefault();
            setting.setCurrentFontColorProfile("Dark Monkey");
        }
    }

    @Override
    public void validate() throws IllegalStateException {

        String LaF = NbPreferences.root().node("laf").get("laf", null);
        if (LaF == null) {
            /* Did the user already set a LaF? */
            NbPreferences.root().node("laf").put("laf", "com.formdev.flatlaf.FlatDarkLaf"); // Set Flatlaf Dark as default LaF
            isFirstInstallation = true;
        }
    }

}
