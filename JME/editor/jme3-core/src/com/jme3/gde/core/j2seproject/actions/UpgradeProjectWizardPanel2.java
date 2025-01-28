package com.jme3.gde.core.j2seproject.actions;

import com.jme3.gde.core.j2seproject.ProjectExtensionManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;

public class UpgradeProjectWizardPanel2 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    public UpgradeProjectWizardPanel2(Project context) {
        this.context = context;
    }
    
    /**
     * The reference to the project we work on.
     */
    private Project context;
    Thread thread = null;
    private boolean flatUpgrade;
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private UpgradeProjectVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public UpgradeProjectVisualPanel2 getComponent() {
        if (component == null) {
            component = new UpgradeProjectVisualPanel2(context);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void validate() throws WizardValidationException {
        if (thread == null || (thread.isAlive()))
            throw new WizardValidationException(null, " Wait for the process to finish!", null);
    }
    
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        Object o = wiz.getProperty("flatUpgrade");
        if (o != null) {
            flatUpgrade = (Boolean)o;
        } else {
            UpgradeProjectWizardAction.logger.log(Level.WARNING, "Could not read Settings for Panel 2. Got a null Property.");
        }
        
        thread = new Thread(new Runnable() { public void run() {doWork();}}, "Worker");
        thread.start(); // Evil hack, but Wizards seem to be design to usually execute code AFTER they have been closed...
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
    }
    
    public void doWork() {
        final UpgradeProjectVisualPanel2 comp = component;
        
        FileObject prProp = context.getProjectDirectory().getFileObject("nbproject/project.properties");
        if (prProp != null && prProp.isValid()) {
            FileLock lock = null;
            try {
                lock = prProp.lock();
                InputStream in = prProp.getInputStream();
                EditableProperties edProps = new EditableProperties(true);
                edProps.load(in);
                in.close();
                
                setProgress(20);
                boolean haveDeployment = false;
                
                if (flatUpgrade) {
                    edProps.setProperty("jme.project.version", "3.0");
                    setProgress(80);
                } else {
                    edProps.setProperty("jme.project.version", "3.1");
                    setProgress(40);
                    LinkedList<String> newClasspath = new LinkedList<String>(); /* We need this list so we can pass each new library as seperate String */
                    /* Only then the EditableProperties will correctly split them along the lines. */
                    boolean foundIt = false;
                    
                    for (String key : UpgradeProjectWizardAction.libraries.keySet()) { /* For each to-be-replaced-Library */
                        for (String cP : edProps.getProperty("javac.classpath").split(":")) { /* For each classpath entry */
                            if (cP.contains(key)) { /* This entry is our to-be-replaced */
                                foundIt = true;
                                newClasspath.addAll(Arrays.asList(UpgradeProjectWizardAction.libraries.get(key)));
                                appendLog("Found pattern \"" + key + "\"!");
                            }
                        }
                        if (!foundIt) {
                            appendLog("Missing(!) pattern \"" + key + "\". Did you edit the file manually?");
                        }
                        foundIt = false;
                    }
                    
                    setProgress(60);
                    
                    for (String cP : edProps.getProperty("javac.classpath").split(":")) {
                        if (!UpgradeProjectWizardAction.libraries.containsKey(cP)) {
                            appendLog("Found unknown classpath entry \"" + cP + "\". Passing through.");
                            newClasspath.add(cP);
                        }
                    }
                    
                    for (int i = 0; i < newClasspath.size(); i++) { /* add ":"'s, which are needed for netbeans/ant */
                        if (i != newClasspath.size() - 1) {
                            String s = newClasspath.get(i);
                            s += ":";
                            newClasspath.set(i, s);
                        }
                    }
                    
                    edProps.setProperty("javac.classpath", newClasspath.toArray(String[]::new));
                    setProgress(60);
                    
                    if (edProps.getProperty("launch4j.exe.enabled") != null) {
                        appendLog("Found Windows Deployment! Switching to newer system...");
                        if (edProps.getProperty("launch4j.exe.enabled").equals("true")) {
                            appendLog("Removing old ProjectExtension...");
                            ProjectExtensionManager pem = new ProjectExtensionManager("launch4j", "v1.4", new String[]{"jar", "-launch4j-exe"});
                            pem.setAntTaskLibrary("launch4j");
                            try {
                                pem.removeExtension(context);
                            } catch (Exception e) {} // When launch4j ant task lib is missing...
                            
                            FileObject fo = context.getProjectDirectory().getFileObject("resources/launch4j");
                            if (fo != null) {
                                appendLog("Deleting resources/launch4j");
                                fo.delete();
                            }
                            haveDeployment = true;
                        }
            
                        edProps.setProperty("windows-x64.app.enabled", edProps.getProperty("launch4j.exe.enabled"));
                        edProps.setProperty("windows-x86.app.enabled", edProps.getProperty("launch4j.exe.enabled"));
                        edProps.remove("launch4j.exe.enabled");
                    }
                    if (edProps.getProperty("linux.launcher.enabled") != null) {
                        appendLog("Found Linux Deployment! Switching to newer system...");
                        if (edProps.getProperty("linux.launcher.enabled").equals("true")) {
                            appendLog("Removing old ProjectExtension...");
                            ProjectExtensionManager pem = new ProjectExtensionManager("linuxlauncher", "v1.1", new String[]{"jar", "-linux-launcher"});
                            pem.removeExtension(context);
                            
                            haveDeployment = true;
                        }
                        edProps.setProperty("linux-x64.app.enabled", edProps.getProperty("linux.launcher.enabled"));
                        edProps.setProperty("linux-x86.app.enabled", edProps.getProperty("linux.launcher.enabled"));
                        edProps.remove("linux.launcher.enabled");
                    }
                    if (edProps.getProperty("mac.app.enabled") != null) {
                        appendLog("Found Mac Deployment! Switching to newer system...");
                        if (edProps.getProperty("mac.app.enabled").equals("true")) {
                            appendLog("Removing old ProjectExtension...");
                            ProjectExtensionManager pem = new ProjectExtensionManager("macapp", "v2.0", new String[]{"jar", "-mac-app"});
                            pem.removeExtension(context);
                            
                            FileObject fo = context.getProjectDirectory().getFileObject("resources/macapp");
                            
                            if (fo != null) {
                                appendLog("Deleting resources/macapp");
                                fo.delete();
                            }
                            
                            haveDeployment = true;
                        }
                        edProps.setProperty("macosx-x64.app.enabled", edProps.getProperty("mac.app.enabled"));// edProps.setProperty("linux-x86.app.enabled", edProps.getProperty("linux.launcher.enabled"));
                        edProps.remove("mac.app.enabled");
                    }
                    
                    setProgress(80);
                }
                
                OutputStream out = prProp.getOutputStream(lock);
                edProps.store(out);
                out.close();
                setProgress(90);
                
                if (haveDeployment) {
                    appendLog("\nIMPORTANT: Open the Project Properties -> Application -> Desktop and uncheck/check the options and press Apply, if you encounter errors during clean & build.");
                }
                
                setProgress(100);
                if (flatUpgrade)
                    appendLog("Chapeau! We are done :)\nNote: You can always re-run this Wizard, if you feel the need to Upgrade to 3.1");
                else
                    appendLog("\n\nChapeau! We are done :)\nNow that your project is 3.1 compatible, feel free to remove the NiftyGUI,\nBullet-Native, Terrain, etc. libraries to your liking, if you don't need them.\nThey were always automatically included in 3.0");
                
            } catch (Exception e) {
                appendLog("Error when trying to write project.properties. Exception: " + e.getMessage());
            } finally {
                if (lock != null) {
                    lock.releaseLock();
                }
                
            }
        } else {
            setProgress(0);
            appendLog("FATAL ERROR: Can't open nbproject/project.properties. The file is either inaccessible or not found.");
        }
    }
    
    private void appendLog(final String s) {
        final UpgradeProjectVisualPanel2 comp = component;
        SwingUtilities.invokeLater(new Runnable () {
            public void run() {
                comp.getTextPane().setText(comp.getTextPane().getText() + s + "\n");
            }
        });
    }
    
    private void setProgress(final int value) {
        final UpgradeProjectVisualPanel2 comp = component;
        SwingUtilities.invokeLater(new Runnable () {
            public void run() {
                comp.getProgressBar().setValue(value);
            }
        });
    
    }
}
