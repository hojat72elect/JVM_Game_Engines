package games.rednblack.h2d.extension.typinglabel;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TypingLabel;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;

@All(LabelComponent.class)
public class TypingLabelSystem extends IteratingSystem {
    protected ComponentMapper<LabelComponent> labelComponentMapper;
    protected ComponentMapper<TypingLabelComponent> typingLabelComponentMapper;
    protected ComponentMapper<DimensionsComponent> dimensionsComponentMapper;

    @Override
    protected void process(int entity) {
        LabelComponent labelComponent = labelComponentMapper.get(entity);
        TypingLabelComponent typingLabelComponent = typingLabelComponentMapper.get(entity);
        labelComponent.typingEffect = typingLabelComponent != null;
        if (!labelComponent.typingEffect) return;

        DimensionsComponent dimensionsComponent = dimensionsComponentMapper.get(entity);

        if (typingLabelComponent.typingLabel == null) {
            typingLabelComponent.labelStyle = new Styles.LabelStyle(labelComponent.style);
            typingLabelComponent.typingLabel = new TypingLabel(labelComponent.getText().toString(), typingLabelComponent.labelStyle);
            typingLabelComponent.setOriginalText(labelComponent.getText());

            float fontScaleX = labelComponent.fontScaleX;
            float fontScaleY = labelComponent.fontScaleY;

            Font font = typingLabelComponent.labelStyle.font;
            font.scale(fontScaleX, fontScaleY);
            typingLabelComponent.typingLabel.setSize(dimensionsComponent.width, dimensionsComponent.height);
            typingLabelComponent.typingLabel.setWrap(labelComponent.wrap);
            typingLabelComponent.typingLabel.setAlignment(labelComponent.labelAlign);
        } else {
            if (!typingLabelComponent.getOriginalText().equals(labelComponent.getText())){
                typingLabelComponent.typingLabel.setText(labelComponent.getText().toString());
                typingLabelComponent.setOriginalText(labelComponent.getText());
            }
            if (typingLabelComponent.typingLabel.isWrap() != labelComponent.wrap) {
                typingLabelComponent.typingLabel.setWrap(labelComponent.wrap);
            }
            if (typingLabelComponent.typingLabel.getAlignment() != labelComponent.labelAlign) {
                typingLabelComponent.typingLabel.setAlignment(labelComponent.labelAlign);
            }
            if (typingLabelComponent.typingLabel.getWidth() != dimensionsComponent.width) {
                typingLabelComponent.typingLabel.setWidth(dimensionsComponent.width);
            }
            if (typingLabelComponent.typingLabel.getHeight() != dimensionsComponent.height) {
                typingLabelComponent.typingLabel.setHeight(dimensionsComponent.height);
            }
        }

        typingLabelComponent.typingLabel.act(world.getDelta());
    }
}

