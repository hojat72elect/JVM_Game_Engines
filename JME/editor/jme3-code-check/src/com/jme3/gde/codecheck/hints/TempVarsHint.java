package com.jme3.gde.codecheck.hints;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.JavaFix;
import org.netbeans.spi.java.hints.TriggerTreeKind;
import org.openide.util.NbBundle;

@Hint(id = "#TempVarsHint.id", displayName = "#TempVarsHint.display-name",
        description = "#TempVarsHint.description", severity = Severity.WARNING,
        category = "general")
@NbBundle.Messages({
    "TempVarsHint.display-name=TempVars might not be released",
    "TempVarsHint.id=TempVars release check",
    "TempVarsHint.description=Checks for calls TempVars.get() and search for correspondinng release() call",
    "TempVarsHint.fix-text=Add a release() call at the end of the method",})
public class TempVarsHint {

    @TriggerTreeKind(Tree.Kind.METHOD)
    public static List<ErrorDescription> hint(HintContext ctx) {
        MethodTree mt = (MethodTree) ctx.getPath().getLeaf();
        CompilationInfo info = ctx.getInfo();

        // Get the list of unreleased temp variables inside the method body
        Collection<VarsPosition> vars = getUnreleasedTempVars(mt, info);
        if (!vars.isEmpty()) {
            List<ErrorDescription> list = new ArrayList<>(vars.size());

            for (VarsPosition curVar : vars) {
                Fix fix = new TempVarsHint.FixImpl(ctx.getInfo(), ctx.getPath(), curVar).toEditorFix();
                list.add(ErrorDescriptionFactory.forSpan(
                        ctx,
                        curVar.start, curVar.end,
                        Bundle.TempVarsHint_display_name(),
                        fix));
            }

            return list;
        }

        return null;
    }

    private static List<VarsPosition> getUnreleasedTempVars(MethodTree mt, CompilationInfo info) {
        if (mt.getBody() != null) {
            List<VarsPosition> vars = null;
            for (StatementTree t : mt.getBody().getStatements()) {

                if (t.getKind().equals(Tree.Kind.VARIABLE)) {
                    Element el = info.getTrees().getElement(info.getTrees().getPath(info.getCompilationUnit(), t));
                    String realTypeName = el.asType().toString();

                    // Check that the variable type is TempVars
                    if ("com.jme3.util.TempVars".equals(realTypeName)) {

                        SourcePositions sp = info.getTrees().getSourcePositions();
                        int start = (int) sp.getStartPosition(info.getCompilationUnit(), t);
                        int end = (int) sp.getEndPosition(info.getCompilationUnit(), t);
                        String variableName = el.getSimpleName().toString();
                        if (vars == null) {
                            vars = new ArrayList<>();
                        }
                        vars.add(new VarsPosition(variableName, start, end));
                        // System.err.println("TempVars.get() at " + start + " " + end+" for variable "+el.getSimpleName().toString());
                    }

                }
                if (vars != null && !vars.isEmpty() && t.getKind().equals(Tree.Kind.EXPRESSION_STATEMENT)) {
                    ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) t;
                    if (expressionStatementTree.getExpression().getKind().equals(Tree.Kind.METHOD_INVOCATION)) {
                        MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionStatementTree.getExpression();

                        // See that the method is called on a member
                        if (methodInvocationTree.getMethodSelect().getKind().equals(Tree.Kind.MEMBER_SELECT)) {
                            MemberSelectTree memberSelectTree = (MemberSelectTree) methodInvocationTree.getMethodSelect();
                            Element el = info.getTrees().getElement(info.getTrees().getPath(info.getCompilationUnit(), methodInvocationTree));

                            // Check that the method being called is "release"
                            if ("release".equals(el.getSimpleName().toString())) {
                                String variableName = memberSelectTree.getExpression().toString();
                                for (Iterator<VarsPosition> it = vars.iterator(); it.hasNext();) {
                                    VarsPosition curVar = it.next();
                                    if (variableName.equals(curVar.varName)) {
                                        //prepare selection for removing
                                        it.remove();

                                        //SourcePositions sp = info.getTrees().getSourcePositions();
                                        //int start = (int) sp.getStartPosition(info.getCompilationUnit(), t);
                                        //int end = (int) sp.getEndPosition(info.getCompilationUnit(), t);
                                        //System.err.println(curVar.varName + ".release() at " + start + " " + end);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return (vars == null ? Collections.emptyList() : vars);
        }

        return Collections.emptyList();
    }

    private static final class VarsPosition {

        final String varName;
        final int start;
        final int end;

        public VarsPosition(String varName, int start, int end) {
            this.varName = varName;
            this.end = end;
            this.start = start;
        }

        @Override
        public String toString() {
            return varName;
        }

    }

    private static final class FixImpl extends JavaFix {

        private final VarsPosition variable;

        public FixImpl(CompilationInfo info, TreePath tp, VarsPosition variable) {
            super(info, tp);
            this.variable = variable;
        }

        @Override
        protected String getText() {
            return Bundle.TempVarsHint_fix_text();
        }

        @Override
        protected void performRewrite(JavaFix.TransformationContext tc) throws Exception {
            WorkingCopy wc = tc.getWorkingCopy();
            TreePath tp = tc.getPath();
            final BlockTree oldBody = ((MethodTree) tp.getLeaf()).getBody();
            if (oldBody == null) {
                return;
            }
            TreeMaker make = wc.getTreeMaker();
            List<? extends StatementTree> statements = oldBody.getStatements();
            List<StatementTree> newStatements = new ArrayList<>(statements.size() + 1);
            newStatements.addAll(statements);
            newStatements.add(make.ExpressionStatement(make.MethodInvocation(Collections.emptyList(), make.MemberSelect(make.Identifier(variable.varName), "release"), Collections.emptyList())));

            BlockTree newBlockTree = wc.getTreeMaker().Block(newStatements, oldBody.isStatic());
            wc.rewrite(oldBody, newBlockTree);
        }
    }
}
