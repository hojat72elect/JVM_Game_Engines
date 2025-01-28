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

import java.io.InputStream;
import java.io.Reader;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.btree.annotation.TaskConstraint;
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
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeReader;
import com.badlogic.gdx.ai.btree.utils.DistributionAdapters;
import com.badlogic.gdx.ai.utils.random.Distribution;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 *
 * @author MeFisto94
 * @param <E>
 */
public class BTreeParser<E> {
    public static final int DEBUG_NONE = 0;
    public static final int DEBUG_LOW = 1;
    public static final int DEBUG_HIGH = 2;

    private static final String TAG = "BTreeParser";

    public int debugLevel;
    public DistributionAdapters distributionAdapters;

    private DefaultBehaviorTreeReader<E> btReader;
    protected BTreeDataObject dataObj;

    public BTreeParser (BTreeDataObject dataObj) {
            this(dataObj, DEBUG_NONE);
    }

    public BTreeParser (BTreeDataObject dataObj, DistributionAdapters distributionAdapters) {
            this(dataObj, distributionAdapters, DEBUG_NONE);
    }

    public BTreeParser (BTreeDataObject dataObj, int debugLevel) {
            this(dataObj, new DistributionAdapters(), debugLevel);
    }

    public BTreeParser (BTreeDataObject dataObj, DistributionAdapters distributionAdapters, int debugLevel) {
            this(dataObj, distributionAdapters, debugLevel, null);
    }

    public BTreeParser (BTreeDataObject dataObj, DistributionAdapters distributionAdapters, int debugLevel, DefaultBehaviorTreeReader<E> reader) {
            this.distributionAdapters = distributionAdapters;
            this.debugLevel = debugLevel;
            btReader = reader == null ? new DefaultBehaviorTreeReader<>() : reader;
            btReader.setParser(this);
            this.dataObj = dataObj;
    }

    /** Parses the given string.
     * @param string the string to parse
     * @param object the blackboard object. It can be {@code null}.
     * @return the behavior tree
     * @throws SerializationException if the string cannot be successfully parsed. */
    public BehaviorTree<E> parse (String string, E object) {
            btReader.parse(string);
            return createBehaviorTree(btReader.root, object);
    }

    /** Parses the given input stream.
     * @param input the input stream to parse
     * @param object the blackboard object. It can be {@code null}.
     * @return the behavior tree
     * @throws SerializationException if the input stream cannot be successfully parsed. */
    public BehaviorTree<E> parse (InputStream input, E object) {
            btReader.parse(input);
            return createBehaviorTree(btReader.root, object);
    }

    /** Parses the given file.
     * @param file the file to parse
     * @param object the blackboard object. It can be {@code null}.
     * @return the behavior tree
     * @throws SerializationException if the file cannot be successfully parsed. */
    public BehaviorTree<E> parse (FileHandle file, E object) {
            btReader.parse(file);
            return createBehaviorTree(btReader.root, object);
    }

    /** Parses the given reader.
     * @param reader the reader to parse
     * @param object the blackboard object. It can be {@code null}.
     * @return the behavior tree
     * @throws SerializationException if the reader cannot be successfully parsed. */
    public BehaviorTree<E> parse (Reader reader, E object) {
            btReader.parse(reader);
            return createBehaviorTree(btReader.root, object);
    }

    protected BehaviorTree<E> createBehaviorTree (Task<E> root, E object) {
            if (debugLevel > BTreeParser.DEBUG_LOW) printTree(root, 0);
            return new BehaviorTree<>(root, object);
    }

    protected static <E> void printTree (Task<E> task, int indent) {
            for (int i = 0; i < indent; i++)
                    System.out.print(' ');
            if (task.getGuard() != null) {
                    System.out.println("Guard");
                    indent = indent + 2;
                    printTree(task.getGuard(), indent);
                    for (int i = 0; i < indent; i++)
                            System.out.print(' ');
            }
            System.out.println(task.getClass().getSimpleName() + "[" + task.toString() + "]");
            for (int i = 0; i < task.getChildCount(); i++) {
                    printTree(task.getChild(i), indent + 2);
            }
    }

    public static class DefaultBehaviorTreeReader<E> extends BehaviorTreeReader {
        private static final ObjectMap<String, String> DEFAULT_IMPORTS = new ObjectMap<String, String>();
        static {
                Class<?>[] classes = new Class<?>[] {// @off - disable libgdx formatter
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
                }; // @on - enable libgdx formatter
                for (Class<?> c : classes) {
                        String fqcn = c.getName();
                        String cn = c.getSimpleName();
                        String alias = Character.toLowerCase(cn.charAt(0)) + (cn.length() > 1 ? cn.substring(1) : "");
                        DEFAULT_IMPORTS.put(alias, fqcn);
                }
        }

        enum Statement {
                Import("import") {
                        @Override
                        protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
                        }

                        @Override
                        protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
                                if (!(value instanceof String)) reader.throwAttributeTypeException(this.name, name, "String");
                                reader.addImport(name, (String)value);
                                return true;
                        }

                        @Override
                        protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
                        }
                },
                Subtree("subtree") {
                        @Override
                        protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
                        }

                        @Override
                        protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
                                if (!name.equals("name")) reader.throwAttributeNameException(this.name, name, "name");
                                if (!(value instanceof String)) reader.throwAttributeTypeException(this.name, name, "String");
                                if ("".equals(value)) throw new GdxRuntimeException(this.name + ": the name connot be empty");
                                if (reader.subtreeName != null)
                                        throw new GdxRuntimeException(this.name + ": the name has been already specified");
                                reader.subtreeName = (String)value;
                                return true;
                        }

                        @Override
                        protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
                                if (reader.subtreeName == null)
                                        throw new GdxRuntimeException(this.name + ": the name has not been specified");
                                reader.switchToNewTree(reader.subtreeName);
                                reader.subtreeName = null;
                        }
                },
                Root("root") {
                        @Override
                        protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
                                reader.subtreeName = ""; // the root tree has empty name
                        }

                        @Override
                        protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
                                reader.throwAttributeTypeException(this.name, name, null);
                                return true;
                        }

                        @Override
                        protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
                                reader.switchToNewTree(reader.subtreeName);
                                reader.subtreeName = null;
                        }
                },
                TreeTask(null) {
                        @Override
                        protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
                                // Root tree is the default one
                                if (reader.currentTree == null) {
                                        reader.switchToNewTree("");
                                        reader.subtreeName = null;
                                }

                                reader.openTask(name, isGuard);
                        }

                        @Override
                        protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
                                StackedTask<E> stackedTask = reader.getCurrentTask();
                                AttrInfo ai = stackedTask.metadata.attributes.get(name);
                                if (ai == null) {
                                    return false;
                                }
                                boolean isNew = reader.encounteredAttributes.add(name);
                                if (!isNew) throw reader.stackedTaskException(stackedTask, "attribute '" + name + "' specified more than once");
                                Field attributeField = reader.getField(stackedTask.task.getClass(), ai.fieldName);
                                reader.setField(attributeField, stackedTask.task, value);
                                return true;
                        }

                        @Override
                        protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
                                if (!reader.isSubtreeRef) {
                                        reader.checkRequiredAttributes(reader.getCurrentTask());
                                        reader.encounteredAttributes.clear();
                                }
                        }
                };

                String name;

                Statement(String name) {
                        this.name = name;
                }

                protected abstract <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard);
                protected abstract <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value);
                protected abstract <E> void exit (DefaultBehaviorTreeReader<E> reader);

        } 

        protected BTreeParser<E> btParser;

        ObjectMap<Class<?>, Metadata> metadataCache = new ObjectMap<>();

        Task<E> root;
        String subtreeName;
        Statement statement;
        private int indent;

        public DefaultBehaviorTreeReader () {
                this(false);
        }

        public DefaultBehaviorTreeReader (boolean reportsComments) {
                super(reportsComments);
        }

        public BTreeParser<E> getParser () {
                return btParser;
        }

        public void setParser (BTreeParser<E> parser) {
                this.btParser = parser;
        }

        @Override
        public void parse (char[] data, int offset, int length) {
                debug = btParser.debugLevel > BTreeParser.DEBUG_NONE;
                root = null;
                clear();
                super.parse(data, offset, length);

                // Pop all task from the stack and check their minimum number of children
                popAndCheckMinChildren(0);

                Subtree<E> rootTree = subtrees.get("");
                if (rootTree == null) throw new GdxRuntimeException("Missing root tree");
                root = rootTree.rootTask;
                if (root == null) throw new GdxRuntimeException("The tree must have at least the root task");

                clear();
        }

        @Override
        protected void startLine (int indent) {
                if (btParser.debugLevel > BTreeParser.DEBUG_LOW)
                        GdxAI.getLogger().debug(TAG, lineNumber + ": <" + indent + ">");
                this.indent = indent;
        }

        private Statement checkStatement (String name) {
                if (name.equals(Statement.Import.name)) return Statement.Import;
                if (name.equals(Statement.Subtree.name)) return Statement.Subtree;
                if (name.equals(Statement.Root.name)) return Statement.Root;
                return Statement.TreeTask;
        }

        @Override
        protected void startStatement (String name, boolean isSubtreeReference, boolean isGuard) {
                if (btParser.debugLevel > BTreeParser.DEBUG_LOW)
                        GdxAI.getLogger().debug(TAG, (isGuard? " guard" : " task") + " name '" + name + "'");

                this.isSubtreeRef = isSubtreeReference;

                this.statement = isSubtreeReference ? Statement.TreeTask : checkStatement(name);
                if (isGuard) {
                        if (statement != Statement.TreeTask)
                                throw new GdxRuntimeException(name + ": only tree's tasks can be guarded");
                }

                statement.enter(this, name, isGuard);
        }

        @Override
        protected void attribute (String name, Object value) {
                if (btParser.debugLevel > BTreeParser.DEBUG_LOW)
                        GdxAI.getLogger().debug(TAG, lineNumber + ": attribute '" + name + " : " + value + "'");

                boolean validAttribute = statement.attribute(this, name, value);
                // Unfortunately since we operate on dummy tasks, we can't verify these
                if (getCurrentTask() != null && !validAttribute) {
                        if (statement == Statement.TreeTask) {
                                throw stackedTaskException(getCurrentTask(), "unknown attribute '" + name + "'");
                        } else {
                                throw new GdxRuntimeException(statement.name + ": unknown attribute '" + name + "'");
                        }
                }
        }

        private Field getField (Class<?> clazz, String name) {
                try {
                        return ClassReflection.getField(clazz, name);
                } catch (ReflectionException e) {
                        throw new GdxRuntimeException(e);
                }
        }

        private void setField (Field field, Task<E> task, Object value) {
                field.setAccessible(true);
                Object valueObject = castValue(field, value);
                if (valueObject == null) throwAttributeTypeException(getCurrentTask().name, field.getName(), field.getType().getSimpleName());
                try {
                        field.set(task, valueObject);
                } catch (ReflectionException e) {
                        throw new GdxRuntimeException(e);
                }
        }

        /**
         * Convert serialized value to java value.
         * Parsed value must be assignable to field argument.
         * Subclasses may override this method to parse unsupported types.
         * @param field task attribute field
         * @param value unparsed value (can be Number, String or Boolean)
         * @return parsed value or null if field type is not supported.
         */
        protected Object castValue (Field field, Object value) {
                Class<?> type = field.getType();
                Object ret = null;
                if (value instanceof Number) {
                        Number numberValue = (Number)value;
                        if (type == int.class || type == Integer.class)
                                ret = numberValue.intValue();
                        else if (type == float.class || type == Float.class)
                                ret = numberValue.floatValue();
                        else if (type == long.class || type == Long.class)
                                ret = numberValue.longValue();
                        else if (type == double.class || type == Double.class)
                                ret = numberValue.doubleValue();
                        else if (type == short.class || type == Short.class)
                                ret = numberValue.shortValue();
                        else if (type == byte.class || type == Byte.class)
                                ret = numberValue.byteValue();
                        else if (ClassReflection.isAssignableFrom(Distribution.class, type)) {
                                @SuppressWarnings("unchecked")
                                Class<Distribution> distributionType = (Class<Distribution>)type;
                                ret = btParser.distributionAdapters.toDistribution("constant," + numberValue, distributionType);
                        }
                } else if (value instanceof Boolean) {
                        if (type == boolean.class || type == Boolean.class) ret = value;
                } else if (value instanceof String) {
                        String stringValue = (String)value;
                        if (type == String.class)
                                ret = value;
                        else if (type == char.class || type == Character.class) {
                                if (stringValue.length() != 1) throw new GdxRuntimeException("Invalid character '" + value + "'");
                                ret = Character.valueOf(stringValue.charAt(0));
                        } else if (ClassReflection.isAssignableFrom(Distribution.class, type)) {
                                @SuppressWarnings("unchecked")
                                Class<Distribution> distributionType = (Class<Distribution>)type;
                                ret = btParser.distributionAdapters.toDistribution(stringValue, distributionType);
                        } else if (ClassReflection.isAssignableFrom(Enum.class, type)) {
                                Enum<?>[] constants = (Enum<?>[])type.getEnumConstants();
                                for (int i = 0, n = constants.length; i < n; i++) {
                                        Enum<?> e = constants[i];
                                        if (e.name().equalsIgnoreCase(stringValue)) {
                                                ret = e;
                                                break;
                                        }
                                }
                        }
                }
                return ret;
        }

        private void throwAttributeNameException (String statement, String name, String expectedName) {
                String expected = " no attribute expected";
                if (expectedName != null)
                        expected = "expected '" + expectedName + "' instead";
                throw new GdxRuntimeException(statement + ": attribute '" + name + "' unknown; " + expected);
        }

        private void throwAttributeTypeException (String statement, String name, String expectedType) {
                throw new GdxRuntimeException(statement + ": attribute '" + name + "' must be of type " + expectedType);
        }

        @Override
        protected void endLine () {
        }

        @Override
        protected void endStatement () {
                statement.exit(this);
        }

        private void openTask (String name, boolean isGuard) {
            Task<E> task;
            if (isSubtreeRef) {
                    task = subtreeRootTaskInstance(name);
            } else {
                    String className = getImport(name);
                    if (className == null) className = name;
                    Class clazz = null;
                    
                    try {
                        clazz = ClassReflection.forName(className);
                    } catch (ReflectionException refl) { }
                    
                    for (ClassLoader cl: getParser().dataObj.manager.getClassLoaders()) {
                        if (clazz == null) {
                            try {
                                clazz = cl.loadClass(className);
                            } catch (ClassNotFoundException cnf) {}
                        }
                    }
                    
                    if (clazz == null) {
                        throw new GdxRuntimeException("Task " + className + "'s class could not be found, be sure to add it to your project.");
                    } else {
                        try {
                            @SuppressWarnings("unchecked")
                            Task<E> tmpTask = (Task<E>)ClassReflection.newInstance(clazz);
                            task = tmpTask;
                        } catch (ReflectionException refl) {
                            throw new GdxRuntimeException("Task " + className + "'s class could not be found, be sure to add it to your project.");
                        }
                    }
            }

            if (!currentTree.inited()) {
                    initCurrentTree(task, indent);
                    indent = 0;
            } else if (!isGuard) {
                    StackedTask<E> stackedTask = getPrevTask();

                    indent -= currentTreeStartIndent;
                    if (stackedTask.task == currentTree.rootTask) {
                            step = indent;
                    }
                    if (indent > currentDepth) {
                            stack.add(stackedTask); // push
                    } else if (indent <= currentDepth) {
                            // Pop tasks from the stack based on indentation
                            // and check their minimum number of children
                            int i = (currentDepth - indent) / step;
                            popAndCheckMinChildren(stack.size - i);
                    }

                    // Check the max number of children of the parent
                    StackedTask<E> stackedParent = stack.peek();
                    int maxChildren = stackedParent.metadata.maxChildren;
                    if (stackedParent.task.getChildCount() >= maxChildren)
                            throw stackedTaskException(stackedParent, "max number of children exceeded ("
                                    + (stackedParent.task.getChildCount() + 1) + " > " + maxChildren + ")");

                    // Add child task to the parent
                    stackedParent.task.addChild(task);
            }
            updateCurrentTask(createStackedTask(name, task), indent, isGuard);
        }

        private StackedTask<E> createStackedTask (String name, Task<E> task) {
                Metadata metadata = findMetadata(task.getClass());
                if (metadata == null)
                        throw new GdxRuntimeException(name + ": @TaskConstraint annotation not found in '" + task.getClass().getSimpleName()
                                + "' class hierarchy");
                return new StackedTask<>(lineNumber, name, task, metadata);
        }

        private Metadata findMetadata (Class<?> clazz) {
                Metadata metadata = metadataCache.get(clazz);
                if (metadata == null) {
                        Annotation tca = ClassReflection.getAnnotation(clazz, TaskConstraint.class);
                        if (tca != null) {
                                TaskConstraint taskConstraint = tca.getAnnotation(TaskConstraint.class);
                                ObjectMap<String, AttrInfo> taskAttributes = new ObjectMap<>();
                                Field[] fields = ClassReflection.getFields(clazz);
                                for (Field f : fields) {
                                        Annotation a = f.getDeclaredAnnotation(TaskAttribute.class);
                                        if (a != null) {
                                                AttrInfo ai = new AttrInfo(f.getName(), a.getAnnotation(TaskAttribute.class));
                                                taskAttributes.put(ai.name, ai);
                                        }
                                }
                                metadata = new Metadata(taskConstraint.minChildren(), taskConstraint.maxChildren(), taskAttributes);
                                metadataCache.put(clazz, metadata);
                        }
                }
                return metadata;
        }

        protected static class StackedTask<E> {
                public int lineNumber;
                public String name;
                public Task<E> task;
                public Metadata metadata;

                StackedTask (int lineNumber, String name, Task<E> task, Metadata metadata) {
                        this.lineNumber = lineNumber;
                        this.name = name;
                        this.task = task;
                        this.metadata = metadata;
                }
        }

        private static class Metadata {
                int minChildren;
                int maxChildren;
                ObjectMap<String, AttrInfo> attributes;

                /** Creates a {@code Metadata} for a task accepting from {@code minChildren} to {@code maxChildren} children and the given
                 * attributes.
                 * @param minChildren the minimum number of children (defaults to 0 if negative)
                 * @param maxChildren the maximum number of children (defaults to {@link Integer.MAX_VALUE} if negative)
                 * @param attributes the attributes */
                Metadata (int minChildren, int maxChildren, ObjectMap<String, AttrInfo> attributes) {
                        this.minChildren = minChildren < 0 ? 0 : minChildren;
                        this.maxChildren = maxChildren < 0 ? Integer.MAX_VALUE : maxChildren;
                        this.attributes = attributes;
                }
        }

        private static class AttrInfo {
                String name;
                String fieldName;
                boolean required;

                AttrInfo (String fieldName, TaskAttribute annotation) {
                        this(annotation.name(), fieldName, annotation.required());
                }

                AttrInfo (String name, String fieldName, boolean required) {
                        this.name = name == null || name.length() == 0 ? fieldName : name;
                        this.fieldName = fieldName;
                        this.required = required;
                }
        }

        protected static class Subtree<E> {
                String name;  // root tree must have no name
                Task<E> rootTask;
                int referenceCount;

                Subtree() {
                        this(null);
                }

                Subtree(String name) {
                        this.name = name;
                        this.rootTask = null;
                        this.referenceCount = 0;
                }

                public void init(Task<E> rootTask) {
                        this.rootTask = rootTask;
                }

                public boolean inited() {
                        return rootTask != null;
                }

                public boolean isRootTree() {
                        return name == null || "".equals(name);
                }

                public Task<E> rootTaskInstance () {
                        if (referenceCount++ == 0) {
                                return rootTask;
                        }
                        return rootTask.cloneTask();
                }
        }

        ObjectMap<String, String> userImports = new ObjectMap<>();

        ObjectMap<String, Subtree<E>> subtrees = new ObjectMap<>();
        Subtree<E> currentTree;

        int currentTreeStartIndent;
        int currentDepth;
        int step;
        boolean isSubtreeRef;
        protected StackedTask<E> prevTask;
        protected StackedTask<E> guardChain;
        protected Array<StackedTask<E>> stack = new Array<>();
        ObjectSet<String> encounteredAttributes = new ObjectSet<>();			
        boolean isGuard;

        StackedTask<E> getLastStackedTask() {
                return stack.peek();
        }

        StackedTask<E> getPrevTask() {
                return prevTask;
        }

        StackedTask<E> getCurrentTask() {
                return isGuard? guardChain : prevTask;
        }

        void updateCurrentTask(StackedTask<E> stackedTask, int indent, boolean isGuard) {
                this.isGuard = isGuard;
                stackedTask.task.setGuard(guardChain == null ? null : guardChain.task);
                if (isGuard) {
                        guardChain = stackedTask;
                }
                else {
                        prevTask = stackedTask;
                        guardChain = null;
                        currentDepth = indent;
                }
        }

        void clear() {
                prevTask = null;
                guardChain = null;
                currentTree = null;
                userImports.clear();
                subtrees.clear();
                stack.clear();
                encounteredAttributes.clear();
        }

        //
        // Subtree
        //

        void switchToNewTree(String name) {
                // Pop all task from the stack and check their minimum number of children
                popAndCheckMinChildren(0);

                this.currentTree = new Subtree<>(name);
                Subtree<E> oldTree = subtrees.put(name, currentTree);
                if (oldTree != null)
                        throw new GdxRuntimeException("A subtree named '" + name + "' is already defined");
        }

        void initCurrentTree(Task<E> rootTask, int startIndent) {
                currentDepth = -1;
                step = 1;
                currentTreeStartIndent = startIndent;
                this.currentTree.init(rootTask);
                prevTask = null;
        }

        Task<E> subtreeRootTaskInstance(String name) {
                Subtree<E> tree = subtrees.get(name);
                if (tree == null)
                        throw new GdxRuntimeException("Undefined subtree with name '" + name + "'");
                return tree.rootTaskInstance();
        }

        //
        // Import
        //

        void addImport (String alias, String task) {
                if (task == null) throw new GdxRuntimeException("import: missing task class name.");
                if (alias == null) {
                        Class<?> clazz = null;
                        try {
                                clazz = ClassReflection.forName(task);
                        } catch (ReflectionException e) {
                                throw new GdxRuntimeException("import: class not found '" + task + "'");
                        }
                        alias = clazz.getSimpleName();
                }
                String className = getImport(alias);
                if (className != null) throw new GdxRuntimeException("import: alias '" + alias + "' previously defined already.");
                userImports.put(alias, task);
        }

        String getImport (String as) {
                String className = DEFAULT_IMPORTS.get(as);
                return className != null ? className : userImports.get(as);
        }

        //
        // Integrity checks
        //

        private void popAndCheckMinChildren (int upToFloor) {
                // Check the minimum number of children in prevTask
                if (prevTask != null) checkMinChildren(prevTask);

                // Check the minimum number of children while popping up to the specified floor
                while (stack.size > upToFloor) {
                        StackedTask<E> stackedTask = stack.pop();
                        checkMinChildren(stackedTask);
                }
        }

        private void checkMinChildren (StackedTask<E> stackedTask) {
                // Check the minimum number of children
                int minChildren = stackedTask.metadata.minChildren;
                if (stackedTask.task.getChildCount() < minChildren)
                        throw stackedTaskException(stackedTask, "not enough children (" + stackedTask.task.getChildCount() + " < " + minChildren
                                + ")");
        }

        private void checkRequiredAttributes (StackedTask<E> stackedTask) {
                // Check the minimum number of children
                Entries<String, AttrInfo> entries = stackedTask.metadata.attributes.iterator();
                while (entries.hasNext()) {
                        Entry<String, AttrInfo> entry = entries.next();
                        if (entry.value.required && !encounteredAttributes.contains(entry.key))
                                throw stackedTaskException(stackedTask, "missing required attribute '" + entry.key + "'");
                }
        }

        private GdxRuntimeException stackedTaskException(StackedTask<E> stackedTask, String message) {
                return new GdxRuntimeException(stackedTask.name + " at line " + stackedTask.lineNumber + ": " + message);
        }
    }
}
