package com.jme3.gde.codecheck.hints;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.java.hints.ConstraintVariableType;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.JavaFix;
import org.netbeans.spi.java.hints.TriggerPattern;
import org.netbeans.spi.java.hints.TriggerPatterns;
import org.openide.util.NbBundle;

@Hint(id = "#UpdateHint.id", displayName = "#UpdateHint.display-name",
        description = "#UpdateHint.description", severity = Severity.WARNING,
        category = "general")
@NbBundle.Messages({
    "UpdateHint.id=Update States / Bound",
    "UpdateHint.display-name=Updating is not needed in jME3, check your update order if you need to call this.",
    "UpdateHint.description=Checks for calls to updateGeometricState(), updateLogicalState() and updateModelBound().",
    "UpdateHint.fix-text=Remove this call"
})
public class UpdateHint {

     @TriggerPatterns({
         @TriggerPattern(value = "$type.updateGeometricState",
                 constraints=@ConstraintVariableType(variable="$type", type="com.jme3.scene.Spatial")),
         @TriggerPattern(value = "$type.updateLogicalState",
                 constraints=@ConstraintVariableType(variable="$type", type="com.jme3.scene.Spatial")),
         @TriggerPattern(value = "$type.updateModelBound",
                 constraints=@ConstraintVariableType(variable="$type", type="com.jme3.scene.Spatial"))
     })
     public static ErrorDescription hint(HintContext ctx) {
         Fix fix = new FixImpl(ctx.getInfo(), ctx.getPath()).toEditorFix();
         return ErrorDescriptionFactory.forName(
                 ctx,
                 ctx.getPath(),
                 Bundle.UpdateHint_display_name(),
                 fix);
     }

    private static final class FixImpl extends JavaFix {

        public FixImpl(CompilationInfo info, TreePath tp) {
            super(info, tp);
        }

        @Override
        protected String getText() {
            return Bundle.UpdateHint_fix_text();
        }

        @Override
        protected void performRewrite(TransformationContext tc) throws Exception {
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