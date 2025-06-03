package org.oreon.common.quadtree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.oreon.core.math.Vec2f;

@Getter
@Setter
@AllArgsConstructor
public class ChunkConfig {

    private int lod;
    private Vec2f location;
    private Vec2f index;
    private float gap;
}
