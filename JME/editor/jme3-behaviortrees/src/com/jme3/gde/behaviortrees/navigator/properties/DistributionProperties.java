/*
 *  Copyright (c) 2009-2018 jMonkeyEngine
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
package com.jme3.gde.behaviortrees.navigator.properties;

import com.badlogic.gdx.ai.utils.random.ConstantDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.ConstantLongDistribution;
import com.badlogic.gdx.ai.utils.random.Distribution;
import com.badlogic.gdx.ai.utils.random.DoubleDistribution;
import com.badlogic.gdx.ai.utils.random.FloatDistribution;
import com.badlogic.gdx.ai.utils.random.GaussianDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.GaussianFloatDistribution;
import com.badlogic.gdx.ai.utils.random.IntegerDistribution;
import com.badlogic.gdx.ai.utils.random.LongDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularFloatDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.TriangularLongDistribution;
import com.badlogic.gdx.ai.utils.random.UniformDoubleDistribution;
import com.badlogic.gdx.ai.utils.random.UniformFloatDistribution;
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution;
import com.badlogic.gdx.ai.utils.random.UniformLongDistribution;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * DistributionProperties is the JPanel displaying the Properties of the LibGDX
 * AI Distributions.
 * @author MeFisto94
 */
public class DistributionProperties extends JPanel {

    protected DistributionEditor editor;
    protected DistProperties restriction;
    protected DistProperties actual;
    protected AttrInfoProperty<Distribution> prop;
    protected PropertyEnv env;
    
    /**
     * Creates new form DistributionProperties
     */
    public DistributionProperties(PropertyEnv env, AttrInfoProperty<Distribution> prop, DistributionEditor editor) {
        this.env = env;
        this.prop = prop;
        this.editor = editor;
        //(Distribution)editor.getValue(); This is the actual value
        // could editor.getValue() be null? @TODO: TEST
        
        /* Problem: Upon first invocation the surrounding modal dialog takes
         * a size. If you have a distribution which is not Triangular, you
         * lack the space for all 3 labels. Unfortunately dynamic resizing
         * the dialog doesn't really work and it would be a bad UX anyway.
         * That's why we set a size here programatically
         */
        setSize(431, 176);
        setPreferredSize(new Dimension(431, 176));
        
        initComponents();
        // Add Input Validation (couldn't be supported through the GUI Builder)
        field1.getDocument().addDocumentListener(new DocListener());
        field2.getDocument().addDocumentListener(new DocListener());
        field3.getDocument().addDocumentListener(new DocListener());
        
        fromDistribution((Distribution)editor.getValue());
        
    }
    
    protected void updateLabelTexts(DistProperties props) {
        switch(props.type) {
            case CONSTANT:
                lblField1.setText("Value: ");
                break;
                
            case GAUSSIAN:
                lblField1.setText("Mean: ");
                lblField2.setText("Standard Deviation: ");
                break;
                
            case TRIANGULAR:
                lblField1.setText("Low: ");
                lblField2.setText("High: ");
                lblField3.setText("Mode: ");
                break;
                
            case UNIFORM:
                lblField1.setText("Low: ");
                lblField2.setText("High: ");
                break;
        }
    }
    
    public void fromDistribution(Distribution dist) {
        // Find out which distributions are possible for this field
        restriction = fromClass(prop.aInfo.f.getType());
        actual = fromClass(dist.getClass());
        
        
        // Only allow the right Distributions based on the restriction
        jComboBox1.setModel(new DefaultComboBoxModel<>(fromRestriction(restriction)));
        
        // Select currently active dist
        DistributionWrapper[] actualDist = fromRestriction(actual);

        if (actual.value == null || actual.type == null || actualDist.length != 1) {
            throw new IllegalStateException("Failed to determine the currently active distribution");
        }
        
        for (int i = 0; i < jComboBox1.getItemCount(); i++) {
            if (jComboBox1.getItemAt(i).getClazz().equals(actualDist[0].getClazz())) {
                jComboBox1.setSelectedIndex(i);
                break;
            }
        }
        
        updateLabelTexts(actual);
        
        // Hide the inappropriate labels
        jComboBox1ItemStateChanged(new ItemEvent(jComboBox1, jComboBox1.getSelectedIndex(), 
                jComboBox1.getSelectedItem(), ItemEvent.SELECTED));
        
        Class c = dist.getClass();
        if (c.equals(ConstantFloatDistribution.class)) {
            field1.setText(String.valueOf(((ConstantFloatDistribution)dist).getValue()));
        } else if (c.equals(ConstantDoubleDistribution.class)) {
            field1.setText(String.valueOf(((ConstantDoubleDistribution)dist).getValue()));
        } else if (c.equals(ConstantIntegerDistribution.class)) {
            field1.setText(String.valueOf(((ConstantIntegerDistribution)dist).getValue()));
        } else if (c.equals(ConstantLongDistribution.class)) {
            field1.setText(String.valueOf(((ConstantLongDistribution)dist).getValue()));
        } else if (c.equals(GaussianDoubleDistribution.class)) {
            field1.setText(String.valueOf(((GaussianDoubleDistribution)dist).getMean()));
            field2.setText(String.valueOf(((GaussianDoubleDistribution)dist).getStandardDeviation()));
        } else if (c.equals(GaussianFloatDistribution.class)) {
            field1.setText(String.valueOf(((GaussianFloatDistribution)dist).getMean()));
            field2.setText(String.valueOf(((GaussianFloatDistribution)dist).getStandardDeviation()));
        } else if (c.equals(TriangularFloatDistribution.class)) {
            field1.setText(String.valueOf(((TriangularFloatDistribution)dist).getLow()));
            field2.setText(String.valueOf(((TriangularFloatDistribution)dist).getHigh()));
            field3.setText(String.valueOf(((TriangularFloatDistribution)dist).getMode()));
        } else if (c.equals(TriangularDoubleDistribution.class)) {    
            field1.setText(String.valueOf(((TriangularDoubleDistribution)dist).getLow()));
            field2.setText(String.valueOf(((TriangularDoubleDistribution)dist).getHigh()));
            field3.setText(String.valueOf(((TriangularDoubleDistribution)dist).getMode()));
        } else if (c.equals(TriangularIntegerDistribution.class)) {
            field1.setText(String.valueOf(((TriangularIntegerDistribution)dist).getLow()));
            field2.setText(String.valueOf(((TriangularIntegerDistribution)dist).getHigh()));
            field3.setText(String.valueOf(((TriangularIntegerDistribution)dist).getMode()));
        } else if (c.equals(TriangularLongDistribution.class)) {
            field1.setText(String.valueOf(((TriangularLongDistribution)dist).getLow()));
            field2.setText(String.valueOf(((TriangularLongDistribution)dist).getHigh()));
            field3.setText(String.valueOf(((TriangularLongDistribution)dist).getMode()));
        } else if (c.equals(UniformFloatDistribution.class)) {
            field1.setText(String.valueOf(((UniformFloatDistribution)dist).getLow()));
            field2.setText(String.valueOf(((UniformFloatDistribution)dist).getHigh()));
        } else if (c.equals(UniformDoubleDistribution.class)) {
            field1.setText(String.valueOf(((UniformDoubleDistribution)dist).getLow()));
            field2.setText(String.valueOf(((UniformDoubleDistribution)dist).getHigh()));
        } else if (c.equals(UniformIntegerDistribution.class)) {
            field1.setText(String.valueOf(((UniformIntegerDistribution)dist).getLow()));
            field2.setText(String.valueOf(((UniformIntegerDistribution)dist).getHigh()));
        } else if (c.equals(UniformLongDistribution.class)) {
            field1.setText(String.valueOf(((UniformLongDistribution)dist).getLow()));
            field2.setText(String.valueOf(((UniformLongDistribution)dist).getHigh()));
        } else {
            throw new IllegalStateException("Unknown Distribution, not supported");
        }
    }
    
    public Distribution toDistribution() {
        Class c = ((DistributionWrapper)jComboBox1.getSelectedItem()).getClazz();
        
        if (c.equals(ConstantFloatDistribution.class)) {
            return new ConstantFloatDistribution((Float)getValue(field1));
        } else if (c.equals(ConstantDoubleDistribution.class)) {
            return new ConstantDoubleDistribution((Double)getValue(field1));
        } else if (c.equals(ConstantIntegerDistribution.class)) {
            return new ConstantIntegerDistribution((Integer)getValue(field1));
        } else if (c.equals(ConstantLongDistribution.class)) {
            return new ConstantLongDistribution((Long)getValue(field1));
        } else if (c.equals(GaussianDoubleDistribution.class)) {
            return new GaussianDoubleDistribution((Double)getValue(field1), (Double)getValue(field2));
        } else if (c.equals(GaussianFloatDistribution.class)) {
            return new GaussianFloatDistribution((Float)getValue(field1), (Float)getValue(field2));
        } else if (c.equals(TriangularFloatDistribution.class)) {
            return new TriangularFloatDistribution((Float)getValue(field1), (Float)getValue(field2), (Float)getValue(field3));
        } else if (c.equals(TriangularDoubleDistribution.class)) {
            return new TriangularDoubleDistribution((Double)getValue(field1), (Double)getValue(field2), (Double)getValue(field3));
        } else if (c.equals(TriangularIntegerDistribution.class)) {
            return new TriangularIntegerDistribution((Integer)getValue(field1), (Integer)getValue(field2), (Integer)getValue(field3));
        } else if (c.equals(TriangularLongDistribution.class)) {
            return new TriangularLongDistribution((Long)getValue(field1), (Long)getValue(field2), (Long)getValue(field3));
        } else if (c.equals(UniformFloatDistribution.class)) {
            return new UniformFloatDistribution((Float)getValue(field1), (Float)getValue(field2));
        } else if (c.equals(UniformDoubleDistribution.class)) {
            return new UniformDoubleDistribution((Double)getValue(field1), (Double)getValue(field2));
        } else if (c.equals(UniformIntegerDistribution.class)) {
            return new UniformIntegerDistribution((Integer)getValue(field1), (Integer)getValue(field2));
        } else if (c.equals(UniformLongDistribution.class)) {
            return new UniformLongDistribution((Long)getValue(field1), (Long)getValue(field2));
        } else {
            throw new IllegalStateException("Unknown Distribution, not supported");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        field1 = new javax.swing.JTextField();
        field2 = new javax.swing.JTextField();
        field3 = new javax.swing.JTextField();
        lblField1 = new javax.swing.JLabel();
        lblField2 = new javax.swing.JLabel();
        lblField3 = new javax.swing.JLabel();

        jComboBox1.setName("distributionBox"); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.jLabel1.text")); // NOI18N

        field1.setText(org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.field1.text")); // NOI18N

        field2.setText(org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.field2.text")); // NOI18N

        field3.setText(org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.field3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblField1, org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.lblField1.text")); // NOI18N
        lblField1.setName("lblField1"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblField2, org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.lblField2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblField3, org.openide.util.NbBundle.getMessage(DistributionProperties.class, "DistributionProperties.lblField3.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(lblField1)
                    .addComponent(lblField2)
                    .addComponent(lblField3))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(field3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(field2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(field1)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(field1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblField1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(field2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblField2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(field3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblField3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        //@TODO Validators for the fields, labels for the files
        DistProperties props = fromClass(((DistributionWrapper)evt.getItem()).getClazz());
        updateLabelTexts(props);
        switch (props.type) {
            case CONSTANT:
                lblField1.setVisible(true);
                field1.setVisible(true);
                lblField2.setVisible(false);
                field2.setVisible(false);
                lblField3.setVisible(false);
                field3.setVisible(false);
                break;
                
            case TRIANGULAR:
                lblField1.setVisible(true);
                field1.setVisible(true);
                lblField2.setVisible(true);
                field2.setVisible(true);
                lblField3.setVisible(true);
                field3.setVisible(true);                
                break;
                
            case UNIFORM:
            case GAUSSIAN:
                lblField1.setVisible(true);
                field1.setVisible(true);
                lblField2.setVisible(true);
                field2.setVisible(true);
                lblField3.setVisible(false);
                field3.setVisible(false);
                break;
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    /**
     * Returns the value of this TextField parsed as Type given by the restriction.
     * Returns null when conversation fails.
     * @param field The relevant text field
     * @return a Float, Double, Long or Integer 
     */
    protected Object getValue(JTextField field) {
        switch (restriction.value) {
            case DOUBLE:
                try {
                    return Double.parseDouble(field.getText());
                } catch (NumberFormatException nfe) {
                    return null;
                }

            case FLOAT:
                try {
                    return Float.parseFloat(field.getText());
                } catch (NumberFormatException nfe) {
                    return null;
                }

            case INTEGER:
                try {
                    return Integer.parseInt(field.getText());
                } catch (NumberFormatException nfe) {
                    return null;
                }

            case LONG:
                try {
                    return Long.parseLong(field.getText());
                } catch (NumberFormatException nfe) {
                    return null;
                }
        }
        
        return null;
    }
    
    protected void validateProps() {
        env.setState(PropertyEnv.STATE_VALID);
        
        for (JTextField field: new JTextField[] {field1, field2, field3}) {
            if (field.isVisible()) {
                Object o = getValue(field);
                if (o == null) {
                    env.setState(PropertyEnv.STATE_INVALID);
                    return;
                }
            }
        }
        
        editor.setValue(toDistribution());
    }
        
    protected static enum DistType {
        CONSTANT,
        GAUSSIAN, // Only Float and Double
        TRIANGULAR,
        UNIFORM
    }
    
    protected static enum DistValue {
        FLOAT,
        DOUBLE,
        INTEGER,
        LONG
    }
    
    protected static class DistProperties {
        public DistType type;
        public DistValue value;
    }
    
    protected static class DistributionWrapper {
        private final Class clazz;

        public DistributionWrapper(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            String str = clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Distribution"));
            for (int i = 1; i < str.length(); i++) {
                if (Character.isUpperCase(str.codePointAt(i))) {
                    return str.substring(0, i) + " " + str.substring(i, str.length());
                }
            }
            
            // No second word found to space
            return str;
        }

        public Class getClazz() {
            return clazz;
        }
        
        public Distribution newInstance() {
            try {
                return (Distribution)clazz.newInstance();
            } catch (Exception e) {
                return null;
            }
        }
    }
    
    protected static DistProperties fromClass(Class c) {
        DistProperties props = new DistProperties();

        // Find the value
        if (FloatDistribution.class.isAssignableFrom(c)) {
            props.value = DistValue.FLOAT;
        } else if (DoubleDistribution.class.isAssignableFrom(c)) {
            props.value = DistValue.DOUBLE;
        } else if (IntegerDistribution.class.isAssignableFrom(c)) {
            props.value = DistValue.INTEGER;
        } else if (LongDistribution.class.isAssignableFrom(c)) {
            props.value = DistValue.LONG;
        }

        // Find the dist, Value is also set as ConstantX extends X.
        // but there are cases where the actual class really is FloatDistribution
        if (ConstantFloatDistribution.class.isAssignableFrom(c)   || 
            ConstantDoubleDistribution.class.isAssignableFrom(c)  ||
            ConstantIntegerDistribution.class.isAssignableFrom(c) ||
            ConstantLongDistribution.class.isAssignableFrom(c)) {
            props.type = DistType.CONSTANT;
        } else if (GaussianFloatDistribution.class.isAssignableFrom(c)   || 
                   GaussianDoubleDistribution.class.isAssignableFrom(c)) {
            props.type = DistType.GAUSSIAN;
        } else if (TriangularFloatDistribution.class.isAssignableFrom(c)   || 
                   TriangularDoubleDistribution.class.isAssignableFrom(c)  ||
                   TriangularIntegerDistribution.class.isAssignableFrom(c) ||
                   TriangularLongDistribution.class.isAssignableFrom(c)) {
            props.type = DistType.TRIANGULAR;
        } else if (UniformFloatDistribution.class.isAssignableFrom(c)   || 
                   UniformDoubleDistribution.class.isAssignableFrom(c)  ||
                   UniformIntegerDistribution.class.isAssignableFrom(c) ||
                   UniformLongDistribution.class.isAssignableFrom(c)) {
            props.type = DistType.UNIFORM;
        }

        return props;
    }
    
    protected static DistributionWrapper[] fromRestriction(DistProperties props) {
        if (props.value != null) {
            if (props.type == null) {
                switch (props.value) {
                    case FLOAT:
                        return new DistributionWrapper[] { 
                            new DistributionWrapper(ConstantFloatDistribution.class),
                            new DistributionWrapper(GaussianFloatDistribution.class),
                            new DistributionWrapper(TriangularFloatDistribution.class),
                            new DistributionWrapper(UniformFloatDistribution.class) 
                        };
                    case DOUBLE:
                        return new DistributionWrapper[] { 
                            new DistributionWrapper(ConstantDoubleDistribution.class),
                            new DistributionWrapper(GaussianDoubleDistribution.class),
                            new DistributionWrapper(TriangularDoubleDistribution.class),
                            new DistributionWrapper(UniformDoubleDistribution.class) 
                        };
                    case INTEGER:
                        return new DistributionWrapper[] { 
                            new DistributionWrapper(ConstantIntegerDistribution.class),
                            new DistributionWrapper(TriangularIntegerDistribution.class),
                            new DistributionWrapper(UniformIntegerDistribution.class)
                        };
                    case LONG:
                        return new DistributionWrapper[] { 
                            new DistributionWrapper(ConstantLongDistribution.class),
                            new DistributionWrapper(TriangularLongDistribution.class),
                            new DistributionWrapper(UniformLongDistribution.class)
                        };
                }
            } else {
                switch (props.value) {
                    case FLOAT:
                        switch (props.type) {
                            case CONSTANT:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(ConstantFloatDistribution.class)
                                };
                            case GAUSSIAN:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(GaussianFloatDistribution.class)
                                };
                            case TRIANGULAR:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(TriangularFloatDistribution.class)
                                };
                            case UNIFORM:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(UniformFloatDistribution.class)
                                };                                
                        }
                        break;
                        
                    case DOUBLE:
                        switch (props.type) {
                            case CONSTANT:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(ConstantDoubleDistribution.class)
                                };
                            case GAUSSIAN:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(GaussianDoubleDistribution.class)
                                };
                            case TRIANGULAR:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(TriangularDoubleDistribution.class)
                                };
                            case UNIFORM:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(UniformDoubleDistribution.class)
                                };                                
                        }
                        break;
                        
                    case INTEGER:
                        switch (props.type) {
                            case CONSTANT:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(ConstantIntegerDistribution.class)
                                };
                            case TRIANGULAR:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(TriangularIntegerDistribution.class)
                                };
                            case UNIFORM:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(UniformIntegerDistribution.class)
                                };                                
                        }
                        break;
                        
                    case LONG:
                        switch (props.type) {
                            case CONSTANT:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(ConstantLongDistribution.class)
                                };
                            case TRIANGULAR:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(TriangularLongDistribution.class)
                                };
                            case UNIFORM:
                                return new DistributionWrapper[] {
                                    new DistributionWrapper(UniformLongDistribution.class)
                                };                                
                        }
                        break;
                        
                }
            }
        }
        
        // else everything is possible
        return new DistributionWrapper[] {
            new DistributionWrapper(ConstantFloatDistribution.class),
            new DistributionWrapper(GaussianFloatDistribution.class),
            new DistributionWrapper(TriangularFloatDistribution.class),
            new DistributionWrapper(UniformFloatDistribution.class),
            new DistributionWrapper(ConstantDoubleDistribution.class),
            new DistributionWrapper(GaussianDoubleDistribution.class),
            new DistributionWrapper(TriangularDoubleDistribution.class),
            new DistributionWrapper(UniformDoubleDistribution.class),
            new DistributionWrapper(ConstantIntegerDistribution.class),
            new DistributionWrapper(TriangularIntegerDistribution.class),
            new DistributionWrapper(UniformIntegerDistribution.class),
            new DistributionWrapper(ConstantLongDistribution.class),
            new DistributionWrapper(TriangularLongDistribution.class),
            new DistributionWrapper(UniformLongDistribution.class)
        };
    }
    
    private final class DocListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            validateProps();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validateProps();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            validateProps();
        }
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField field1;
    private javax.swing.JTextField field2;
    private javax.swing.JTextField field3;
    private javax.swing.JComboBox<DistributionWrapper> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblField1;
    private javax.swing.JLabel lblField2;
    private javax.swing.JLabel lblField3;
    // End of variables declaration//GEN-END:variables
}
