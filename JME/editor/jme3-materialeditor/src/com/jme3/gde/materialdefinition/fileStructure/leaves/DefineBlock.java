package com.jme3.gde.materialdefinition.fileStructure.leaves;

import com.jme3.util.blockparser.Statement;
import java.util.Objects;

/**
 * Handles one row of Defines in a MaterialDef, ie ALBEDOMAP : AlbedoMap
 * @author rickard
 */
public final class DefineBlock extends LeafStatement {

    private String name;
    private String define;

    protected DefineBlock(int lineNumber, String line) {
        super(lineNumber, line);
    }

    public DefineBlock(Statement sta) {
        this(sta.getLineNumber(), sta.getLine());
        parse(sta);
        updateLine();
    }

    public DefineBlock(String name, String define) {
        super(0, "");
        this.name = name;
        this.define = define;
        updateLine();
    }

    private void updateLine() {
        this.line = String.format("%s : %s", name, define);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateLine();
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
        updateLine();
    }

    private void parse(Statement sta) {
        String[] values = sta.getLine().split(":");
        name = values[0].trim();
        define = values[1].trim();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.define);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DefineBlock)) {
            return false;
        }
        return ((DefineBlock) obj).getName().equals(name) && ((DefineBlock) obj).getDefine().equals(define);
    }

}
