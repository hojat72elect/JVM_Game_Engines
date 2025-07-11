package games.rednblack.editor.view.ui.box;

import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Array;
import games.rednblack.editor.controller.commands.*;
import games.rednblack.editor.controller.commands.resource.DeleteResourceCommand;
import games.rednblack.editor.renderer.data.LayerItemVO;
import games.rednblack.editor.utils.runtime.EntityUtils;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.h2d.common.MsgAPI;
import games.rednblack.puremvc.Facade;
import games.rednblack.puremvc.interfaces.INotification;
import games.rednblack.puremvc.util.Interests;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by sargis on 4/10/15.
 */
public class UIItemsTreeBoxMediator extends PanelMediator<UIItemsTreeBox> {
    private static final String TAG = UIItemsTreeBoxMediator.class.getCanonicalName();
    public static final String NAME = TAG;

    public UIItemsTreeBoxMediator() {
        super(NAME, new UIItemsTreeBox());
    }

    @Override
    public void listNotificationInterests(Interests interests) {
        super.listNotificationInterests(interests);
        interests.add(MsgAPI.SCENE_LOADED,
                MsgAPI.NEW_ITEM_ADDED,
                UIItemsTreeBox.ITEMS_SELECTED,
                SetSelectionCommand.DONE);
        interests.add(AddSelectionCommand.DONE,
                ReleaseSelectionCommand.DONE,
                DeleteResourceCommand.DONE,
                MsgAPI.DELETE_ITEMS_COMMAND_DONE);
        interests.add(MsgAPI.ACTION_Z_INDEX_CHANGED,
                MsgAPI.ACTION_CAMERA_CHANGE_COMPOSITE,
                MsgAPI.ITEM_DATA_UPDATED,
                LayerJumpCommand.DONE);
        interests.add(LayerSwapCommand.DONE, MsgAPI.UPDATE_TREE_ITEMS_FILTER);
    }

    @Override
    public void handleNotification(INotification notification) {
        super.handleNotification(notification);
        Sandbox sandbox = Sandbox.getInstance();
        switch (notification.getName()) {
            case MsgAPI.SCENE_LOADED:
                int rootEntity = sandbox.getCurrentViewingEntity();
                viewComponent.init(rootEntity);
                break;
            case MsgAPI.ACTION_CAMERA_CHANGE_COMPOSITE:
                rootEntity = notification.getBody();
                viewComponent.init(rootEntity);
                break;
            case MsgAPI.ITEM_DATA_UPDATED:
            case MsgAPI.ACTION_Z_INDEX_CHANGED:
            case MsgAPI.NEW_ITEM_ADDED:
            case MsgAPI.DELETE_ITEMS_COMMAND_DONE:
            case MsgAPI.UPDATE_TREE_ITEMS_FILTER:
            case DeleteResourceCommand.DONE:
            case LayerJumpCommand.DONE:
            case LayerSwapCommand.DONE:
                rootEntity = sandbox.getCurrentViewingEntity();
                if (notification.getType() == null || !notification.getType().equals(ItemsMoveCommand.TAG))
                    viewComponent.update(rootEntity);
                break;
            case UIItemsTreeBox.ITEMS_SELECTED:
                Selection<UIItemsTreeNode> selection = notification.getBody();
                Array<UIItemsTreeNode> nodes = selection.toArray();
                Set<Integer> items = new HashSet<>();

                for (UIItemsTreeNode node : nodes) {
                    String entityId = node.getValue().entityId;
                    int item = EntityUtils.getByUniqueId(entityId);
                    //layer lock thing
                    LayerItemVO layerItemVO = EntityUtils.getEntityLayer(item);
                    if(layerItemVO != null && layerItemVO.isLocked) {
                        continue;
                    }
                    if (item != -1) {
                        items.add(item);
                    }
                }

                sendSelectionNotification(items);

                break;
            case SetSelectionCommand.DONE:
            case AddSelectionCommand.DONE:
            case ReleaseSelectionCommand.DONE:
                viewComponent.setSelection(sandbox.getSelector().getSelectedItems());
                break;
        }
    }

    private void sendSelectionNotification(Set<Integer> items) {
        Set<Integer> ntfItems = (items.isEmpty())? null : items;
        Facade.getInstance().sendNotification(MsgAPI.ACTION_SET_SELECTION, ntfItems);
    }
}