package org.oreon.common.ui;

import lombok.Getter;
import lombok.Setter;
import org.oreon.core.scenegraph.RenderList;

import java.util.ArrayList;
import java.util.List;

public class UIScreen {

    @Getter
    @Setter
    private List<UIElement> elements = new ArrayList<UIElement>();

    public void render() {
        elements.forEach(element -> element.render());
    }

    public void update() {
        elements.forEach(element -> element.update());
    }

    public void record(RenderList renderList) {
        elements.forEach(element -> element.record(renderList));
    }

    public void shutdown() {
        elements.forEach(element -> element.shutdown());
    }
}
