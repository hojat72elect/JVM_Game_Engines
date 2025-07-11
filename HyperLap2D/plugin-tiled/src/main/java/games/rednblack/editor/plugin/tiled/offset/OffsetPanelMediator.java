package games.rednblack.editor.plugin.tiled.offset;

import com.badlogic.gdx.math.Vector2;
import games.rednblack.editor.plugin.tiled.TiledPlugin;
import games.rednblack.puremvc.Mediator;
import games.rednblack.puremvc.interfaces.INotification;
import games.rednblack.puremvc.util.Interests;

/**
 * Created by mariam on 5/12/16.
 */
public class OffsetPanelMediator extends Mediator<OffsetPanel> {

    private static final String TAG = OffsetPanelMediator.class.getCanonicalName();
    public static final String NAME = TAG;

    private TiledPlugin tiledPlugin;


    public OffsetPanelMediator(TiledPlugin tiledPlugin) {
        super(NAME, tiledPlugin.offsetPanel);

        this.tiledPlugin = tiledPlugin;
    }

    @Override
    public void listNotificationInterests(Interests interests) {
        interests.add(TiledPlugin.ACTION_OPEN_OFFSET_PANEL,
                TiledPlugin.TILE_GRID_OFFSET_ADDED,
                TiledPlugin.TILE_SELECTED);
    }

    @Override
    public void handleNotification(INotification notification) {
        super.handleNotification(notification);

        switch (notification.getName()) {
            case TiledPlugin.ACTION_OPEN_OFFSET_PANEL:
                viewComponent.refreshOffsetValues();
                viewComponent.show(tiledPlugin.getAPI().getUIStage());
                break;
            case TiledPlugin.TILE_GRID_OFFSET_ADDED:
                Vector2 offsetValue = notification.getBody();
                tiledPlugin.setSelectedTileGridOffset(offsetValue);
                tiledPlugin.applySelectedTileGridOffset();
                break;
            case TiledPlugin.TILE_SELECTED:
                if (viewComponent.isOpen) {
                    viewComponent.refreshOffsetValues();
                }
                break;
        }

    }
}
