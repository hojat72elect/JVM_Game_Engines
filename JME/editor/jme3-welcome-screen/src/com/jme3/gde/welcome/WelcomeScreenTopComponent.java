/*
 *  Copyright (c) 2009-2019 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.welcome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * The Window Component itself.
 * It will display the "Message of the Day"
 */
@ConvertAsProperties(
    dtd = "-//com.jme3.gde.welcome//WelcomeScreen//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "WelcomeScreenTopComponent",
    iconBase="com/jme3/gde/core/icons/65.png",
    persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "WelcomeScreenTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_WelcomeScreenAction",
        preferredID = "WelcomeScreenTopComponent")
public final class WelcomeScreenTopComponent extends TopComponent implements HyperlinkListener {
    private static Thread checkOpenThread;
    private static final Logger logger = Logger.getLogger(WelcomeScreenTopComponent.class.getName());
    private static final HelpCtx ctx = new HelpCtx("com.jme3.gde.core.about");
//    private final RssFeedParser parser = new RssFeedParser(org.openide.util.NbBundle.getMessage(WelcomeScreenTopComponent.class, "WelcomeScreenTopComponent.rss.link"));

    public WelcomeScreenTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(WelcomeScreenTopComponent.class, "CTL_WelcomeScreenTopComponent"));
        setToolTipText(NbBundle.getMessage(WelcomeScreenTopComponent.class, "HINT_WelcomeScreenTopComponent"));

        jScrollPane2.setOpaque(false);
        jScrollPane2.getViewport().setOpaque(false);
        // Not supported anymore, but maybe someone knows how to replace it (if necessary)
        //jEditorPane1.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, SwingUtilities2.AATextInfo.getAATextInfo(true));
        jEditorPane1.addHyperlinkListener(this);
    }

    public void loadPage() {
        try {
            do {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {}
            }
            while (checkOpenThread == null || checkOpenThread.isAlive()); /* Wait for first getModified (static executed from NB) to finish */
            
            final URL startUrl = URI.create(org.openide.util.NbBundle.getMessage(WelcomeScreenTopComponent.class, "WelcomeScreenTopComponent.http.link")).toURL();
            final long lastMod = getModified(startUrl);
            NbPreferences.forModule(getClass()).putLong("LAST_PAGE_UPDATE", lastMod);
            
            SwingUtilities.invokeLater( new Runnable() {
                @Override        
                public void run() {
                    try {
                        if (lastMod != 0)
                            jEditorPane1.setPage(startUrl);
                        else
                            jEditorPane1.setPage(URI.create(org.openide.util.NbBundle.getMessage(WelcomeScreenTopComponent.class, "WelcomeScreenTopComponent.local.link")).toURL());
                    } catch (IOException ex) {
                        logger.log(Level.INFO, "Loading welcome page from web failed", ex);
                        try {
                            jEditorPane1.setPage(URI.create(org.openide.util.NbBundle.getMessage(WelcomeScreenTopComponent.class, "WelcomeScreenTopComponent.local.link")).toURL());
                        } catch (IOException ex1) {
                            logger.log(Level.SEVERE, "Could not open local help page!", ex1);
                        }
                    }
                } 
            });
        }
        catch (MalformedURLException ex)
        {
            logger.log(Level.SEVERE, "Could not build URL since it's malformed!", ex);
        }
    }

    public static void checkOpen() {
        checkOpenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                checkOpen(0);
            }
        }, "WelcomeScreenCheckOpenThread");
        checkOpenThread.start();
    }

    public static void checkOpen(long lastMod) {
        try {
            long lastCheck = NbPreferences.forModule(WelcomeScreenTopComponent.class).getLong("LAST_PAGE_UPDATE", 0);
            URL startUrl = URI.create(org.openide.util.NbBundle.getMessage(WelcomeScreenTopComponent.class, "WelcomeScreenTopComponent.http.link")).toURL();
            if (lastMod == 0) {
                lastMod = getModified(startUrl);
            }
            logger.log(Level.INFO, "Checking page id {0} vs stored id {1}", new Object[]{lastMod, lastCheck});
            if (lastCheck != lastMod) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        WelcomeScreenTopComponent tc = (WelcomeScreenTopComponent) WindowManager.getDefault().findTopComponent("WelcomeScreenTopComponent");
                        if (tc != null) {
                            tc.open();
                            tc.requestActive();
                        } else {
                            logger.log(Level.WARNING, "Did not find Welcome Screen window");
                        }
                    }
                });
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent he) {
        if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                jEditorPane1.setPage(he.getURL());
            } catch (IOException ex) {
                logger.log(Level.INFO, "Loading page failed", ex);
                try {
                    logger.log(Level.WARNING, "Could not open web page!");
                    URL startUrl = URI.create(org.openide.util.NbBundle.getMessage(WelcomeScreenTopComponent.class, "WelcomeScreenTopComponent.local.link")).toURL();
                    jEditorPane1.setPage(startUrl);
                } catch (IOException ex1) {
                    logger.log(Level.SEVERE, "Could not open local help page!", ex1);
                }
            }
        }
    }

    private static long getModified(URL url) {
        try {
            URLConnection conn = url.openConnection();
            long lastMod = conn.getLastModified();
            if (lastMod != 0) {
                logger.log(Level.INFO, "Found getLastModified of {0}", lastMod);
                return lastMod;
            } else {
                logger.log(Level.INFO, "Returning hash code of content", lastMod);
                String content = getContent(conn);
                return content.hashCode();
            }
        } catch (IOException ex) {
            logger.log(Level.INFO, "Loading welcome page modified date from web failed", ex);
        }
        return 0;
    }

    private static String getContent(URLConnection connection) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(
                    connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException ex) {
            logger.log(Level.INFO, "Reading welcome page content from web failed!");
            logger.log(Level.INFO, ex.getMessage());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                logger.log(Level.INFO, "Closing reader for welcome page content from web failed", ex);
            }
        }
        return "";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new GradPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setBackground(java.awt.Color.white);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setOpaque(false);

        jEditorPane1.setEditable(false);
        jEditorPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jEditorPane1.setContentType("text/html"); // NOI18N
        jEditorPane1.setCaretColor(new java.awt.Color(255, 255, 255));
        jEditorPane1.setOpaque(false);
        jScrollPane2.setViewportView(jEditorPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadPage();
            }
        }, "WelcomeScreenOpenedThread").start();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return ctx;
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
