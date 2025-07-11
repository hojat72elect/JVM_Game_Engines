package games.rednblack.editor.view.ui;

import games.rednblack.editor.controller.commands.CompositeCameraChangeCommand;
import games.rednblack.editor.controller.commands.CreateStickyNoteCommand;
import games.rednblack.editor.controller.commands.ModifyStickyNoteCommand;
import games.rednblack.editor.controller.commands.RemoveStickyNoteCommand;
import games.rednblack.editor.renderer.data.SceneVO;
import games.rednblack.editor.renderer.data.StickyNoteVO;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.h2d.common.MsgAPI;
import games.rednblack.puremvc.Mediator;
import games.rednblack.puremvc.interfaces.INotification;
import games.rednblack.puremvc.util.Interests;

public class StickyNotesUIMediator  extends Mediator<StickyNotesUI> {
    private static final String TAG = StickyNotesUIMediator.class.getCanonicalName();
    public static final String NAME = TAG;

    /**
     * Constructor.
     */
    public StickyNotesUIMediator() {
        super(NAME, new StickyNotesUI());
    }

    @Override
    public void onRegister() {
        viewComponent.setVisible(false);
    }

    @Override
    public void listNotificationInterests(Interests interests) {
        interests.add(MsgAPI.SCENE_LOADED,
                CompositeCameraChangeCommand.DONE,
                CreateStickyNoteCommand.DONE,
                RemoveStickyNoteCommand.DONE);
        interests.add(ModifyStickyNoteCommand.DONE);
    }

    @Override
    public void handleNotification(INotification notification) {
        super.handleNotification(notification);

        switch (notification.getName()) {
            case CompositeCameraChangeCommand.DONE:
                viewComponent.setVisible(Sandbox.getInstance().isViewingRootEntity());
                break;
            case MsgAPI.SCENE_LOADED:
                viewComponent.setVisible(true);
                createStickyNotes();
                break;
            case CreateStickyNoteCommand.DONE:
                StickyNoteVO noteVO = notification.getBody();
                viewComponent.attachNote(noteVO);
                break;
            case RemoveStickyNoteCommand.DONE:
                viewComponent.removeNote(notification.getBody());
                break;
            case ModifyStickyNoteCommand.DONE:
                viewComponent.updateNote(notification.getBody());
                break;
        }
    }

    private void createStickyNotes() {
        viewComponent.clear();

        SceneVO sceneVO = Sandbox.getInstance().getSceneControl().getCurrentSceneVO();
        for (StickyNoteVO noteVO : sceneVO.composite.sStickyNotes.values()) {
            viewComponent.attachNote(noteVO);
        }
    }
}
