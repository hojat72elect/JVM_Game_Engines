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
package com.jme3.gde.behaviortrees;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector;
import com.badlogic.gdx.ai.btree.branch.Parallel;
import com.badlogic.gdx.ai.btree.branch.RandomSelector;
import com.badlogic.gdx.ai.btree.branch.RandomSequence;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail;
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed;
import com.badlogic.gdx.ai.btree.decorator.Include;
import com.badlogic.gdx.ai.btree.decorator.Invert;
import com.badlogic.gdx.ai.btree.decorator.Random;
import com.badlogic.gdx.ai.btree.decorator.Repeat;
import com.badlogic.gdx.ai.btree.decorator.SemaphoreGuard;
import com.badlogic.gdx.ai.btree.decorator.UntilFail;
import com.badlogic.gdx.ai.btree.decorator.UntilSuccess;
import com.badlogic.gdx.ai.btree.leaf.Failure;
import com.badlogic.gdx.ai.btree.leaf.Success;
import com.badlogic.gdx.ai.btree.leaf.Wait;
import com.badlogic.gdx.ai.utils.random.Distribution;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.jme3.gde.behaviortrees.BTreeExporterUtils.AttrInfo;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Exports BehaviorTrees to .btree files.<br>
 * It is specially geared towards DummyTasks as used in the SDK Editor.<br>
 * It even supports using imports to decrease filesize and increase human readability
 * @TODO: /NOTE: Get rid of the usage of DummyTasks as we cannot guess the parameters upon import,
 * so we force the user to make the tasks known to the engine
 * @author MeFisto94
 */
public class BTreeExporter {
    
    protected HashMap<String, String> classNameToFQNMap;
    protected HashMap<String, String> fqnToImport;
    protected PrintWriter w;
    private final Class<?>[] builtinClasses = new Class<?>[] {
        AlwaysFail.class,
        AlwaysSucceed.class,
        DynamicGuardSelector.class,
        Failure.class,
        Include.class,
        Invert.class,
        Parallel.class,
        Random.class,
        RandomSelector.class,
        RandomSequence.class,
        Repeat.class,
        Selector.class,
        SemaphoreGuard.class,
        Sequence.class,
        Success.class,
        UntilFail.class,
        UntilSuccess.class,
        Wait.class
    };	
    
    private BTreeExporter(OutputStream out) {
        classNameToFQNMap = new HashMap<>();
        fqnToImport = new HashMap<>();
        w = new PrintWriter(out, true);
    }
        
    public static void exportTree(OutputStream out, BehaviorTree tree) {
        if (tree == null) {
            return;
        }
        
        BTreeExporter bt = new BTreeExporter(out);
        
        /* First we fill the ClassName Map and if we see that a key already 
         * exists, we add a number to it and strip the TASK suffix.
         * Also to not have thousands of reverse lookups we populate fqn with
         * the opposite.
        */
        bt.handleImports(tree);
        bt.writeImports();
        bt.w.println("root");
        for (int i = 0; i < tree.getChildCount(); i++) {
            bt.exportTask(tree.getChild(i), 1);
        }
    }
    
    protected void handleImports(Task task) {
        if (isCustomTask(task)) {
            String c = getCanonical(task);
            String s = getSimpleName(task);
            if (!fqnToImport.containsKey(c)) {
                if (!classNameToFQNMap.containsKey(s)) {
                    classNameToFQNMap.put(s, c);
                    fqnToImport.put(c, s);
                } else { // Fix Name Clash
                    boolean success = false;
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (!classNameToFQNMap.containsKey(s + i)) {
                            classNameToFQNMap.put(s + i, c);
                            fqnToImport.put(c, s);
                            success = true;
                            break;
                        }
                    }
                    
                    if (!success) {
                        throw new IllegalStateException("Failed to solve the name clash at import handling");
                    }
                }
            }
        }
        
        for (int i = 0; i < task.getChildCount(); i++) {
            handleImports(task.getChild(i));
        }
    }
    
    protected void writeImports() {
        classNameToFQNMap.forEach((k, v) -> w.println("import " + k + ":\"" + v + "\""));
    }
    
    protected void exportTask(Task task, int ident) {
        String guard = "";
        
        if (task.getGuard() != null) {
            guard = "(" + extractTask(task.getGuard()) + ") ";
        }
        w.println(ident(ident) + guard + extractTask(task)); // No params so far
        
        for (int i = 0; i < task.getChildCount(); i++) {
            exportTask(task.getChild(i), ident + 1);
        }
    }
    
    protected String extractTask(Task task) {
        return extractTaskName(task) + extractAttributes(task);
    }

    protected String extractAttributes(Task task) {
        ObjectMap<String, AttrInfo> attrs = BTreeExporterUtils.findMetadata(task.getClass());

        if (attrs.size == 0) {
            return "";
        }

        return " " + StreamSupport.stream(attrs.spliterator(), false)
            .map(entry -> {
                try {
                    return entry.key + ":" + attrVal(task, entry.value);
                } catch (ReflectionException refl) {
                    return "";                        
                }
            }).collect(Collectors.joining(" "));
    }
    
    private String attrVal(Task t, AttrInfo ai) throws ReflectionException {
        Field f = ai.f;
        Class type = ai.f.getType();
        if (String.class.isAssignableFrom(type)) {
            return "\"" + (String)f.get(t) + "\"";
        } else if (Enum.class.isAssignableFrom(type)) {
            return "\"" + f.get(t).toString().toLowerCase() + "\"";
        } else if (Distribution.class.isAssignableFrom(type)) {
            return "\"" + DistributionStringConverter.fromDistribution((Distribution)f.get(t)) + "\"";
        }

        return String.valueOf(f.get(t));
    }
    
    protected String extractTaskName(Task task) throws IllegalStateException {
        if (isCustomTask(task)) {
            String c = getCanonical(task);
            if (fqnToImport.containsKey(c)) {
                return fqnToImport.get(c);
            } else {
                throw new IllegalStateException("Could not find import for class " + c);
            }
        } else if (isBuiltin(task.getClass())) {
            return builtinTask(task);
        } else {
            throw new IllegalStateException("Unknown Task");
        }
    }
    
    protected boolean isBuiltin(Class c) {        
        // We could also use assignableFrom, but it works without.
        return c.equals(BehaviorTree.class) || Arrays.stream(builtinClasses).anyMatch(b -> b.equals(c));
    }    
    
    protected boolean isCustomTask(Task t) {
        if (t.getClass().getCanonicalName().startsWith("com.badlogic.gdx.ai.btree")) {
            return false;
        } else if (isBuiltin(t.getClass())) {
            return false;
        } else {
            return true;
        }
    }
    
    protected String builtinTask(Task t) {
        if (t instanceof BehaviorTree) {
            BehaviorTree bt = (BehaviorTree)t; // @TODO: BTree References
            return "root";
        }
        
        // Code also taken from BehaviorTaskParser
        String cn = t.getClass().getSimpleName();
        return Character.toLowerCase(cn.charAt(0)) + (cn.length() > 1 ? cn.substring(1) : "");
    }
    
    /* Because our editor deals with DummyTask wrapping classes for unknown tasks
     * unwrap them here to export them properly again
     */
    protected String getCanonical(Task t) {
        return t.getClass().getCanonicalName();
    }
    
    protected String getSimpleName(Task t) {
        return t.getClass().getSimpleName();
    }
    
    protected String ident(int ident) {
        StringBuilder sB = new StringBuilder();
        for (int i = 0; i < ident; i++) {
            sB.append("  "); // 2 spaces used in example files
        }        
        return sB.toString();
    }
}
