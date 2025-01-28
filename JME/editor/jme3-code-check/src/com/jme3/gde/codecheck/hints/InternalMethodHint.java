package com.jme3.gde.codecheck.hints;

import com.jme3.system.Annotations;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
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

@Hint(id = "#InternalMethodHint.id", displayName = "#InternalMethodHint.display-name",
        description = "#InternalMethodHint.description", severity = Severity.WARNING,
        category = "general")
@NbBundle.Messages({
    "InternalMethodHint.display-name=You should not call this method, its for internal use only!",
    "InternalMethodHint.id=Internal Methods",
    "InternalMethodHint.description=Checks for calls to internal methods.",
    "InternalMethodHint.fix-text=Remove this call"
})
public class InternalMethodHint {

    @TriggerTreeKind(Tree.Kind.METHOD_INVOCATION)
    public static ErrorDescription hint(HintContext ctx) {
        CompilationInfo info = ctx.getInfo();
        TreePath treePath = ctx.getPath();
        if (info.getTrees().getElement(treePath).getAnnotation(Annotations.Internal.class) != null) {
                Fix fix = new InternalMethodHint.FixImpl(ctx.getInfo(), ctx.getPath()).toEditorFix();
                return ErrorDescriptionFactory.forName(
                        ctx,
                        ctx.getPath(),
                        Bundle.InternalMethodHint_display_name(),
                    fix);
        }

        return null;
    }

    private static final class FixImpl extends JavaFix {

        public FixImpl(CompilationInfo info, TreePath tp) {
            super(info, tp);
        }

        @Override
        protected String getText() {
            return Bundle.InternalMethodHint_fix_text();
        }

        @Override
        protected void performRewrite(JavaFix.TransformationContext tc) throws Exception {
            WorkingCopy wc = tc.getWorkingCopy();
            TreePath statementPath = tc.getPath();
            TreePath blockPath = tc.getPath().getParentPath();
            while (!(blockPath.getLeaf() instanceof BlockTree)) {
                statementPath = blockPath;
                blockPath = blockPath.getParentPath();
                if (blockPath == null) {
                    return;
                }
            }
            BlockTree blockTree = (BlockTree) blockPath.getLeaf();
            List<? extends StatementTree> statements = blockTree.getStatements();
            List<StatementTree> newStatements = new ArrayList<>();
            for (StatementTree statement : statements) {
                if (statement != statementPath.getLeaf()) {
                    newStatements.add(statement);
                }
            }
            BlockTree newBlockTree = wc.getTreeMaker().Block(newStatements, blockTree.isStatic());
            wc.rewrite(blockTree, newBlockTree);
        }
    }
}
