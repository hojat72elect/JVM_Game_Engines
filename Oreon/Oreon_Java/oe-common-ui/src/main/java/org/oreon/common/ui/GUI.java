package org.oreon.common.ui;

import lombok.Getter;
import org.oreon.core.scenegraph.RenderList;

import java.util.ArrayList;

public abstract class GUI {

    @Getter
    private final ArrayList<UIScreen> screens = new ArrayList<UIScreen>();


    public void update() {

        screens.forEach(screen -> screen.update());
    }

    public void render() {

        screens.forEach(screen -> screen.render());
    }

    public void record(RenderList renderList) {

        screens.forEach(screen -> screen.record(renderList));
    }

    public void shutdown() {

        screens.forEach(screen -> screen.shutdown());
    }

}
