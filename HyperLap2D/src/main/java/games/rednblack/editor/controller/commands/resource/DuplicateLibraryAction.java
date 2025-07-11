package games.rednblack.editor.controller.commands.resource;

import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.ui.util.dialog.InputDialogListener;
import games.rednblack.editor.controller.commands.AddToLibraryActionCommand;
import games.rednblack.editor.controller.commands.NonRevertibleCommand;
import games.rednblack.editor.proxy.ProjectManager;
import games.rednblack.editor.renderer.data.GraphVO;
import games.rednblack.editor.renderer.utils.HyperJson;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.editor.view.ui.validator.StringNameValidator;
import games.rednblack.h2d.common.H2DDialogs;
import games.rednblack.h2d.common.MsgAPI;
import games.rednblack.puremvc.Facade;

import java.util.HashMap;

public class DuplicateLibraryAction extends NonRevertibleCommand {

    public DuplicateLibraryAction() {
        setShowConfirmDialog(false);
    }

    @Override
    public void doAction() {
        String libraryActionName = notification.getBody();

        ProjectManager projectManager = Facade.getInstance().retrieveProxy(ProjectManager.NAME);
        HashMap<String, GraphVO> libraryActions = projectManager.currentProjectInfoVO.libraryActions;

        GraphVO actionToDuplicate = libraryActions.get(libraryActionName);

        H2DDialogs.showInputDialog(Sandbox.getInstance().getUIStage(), "Duplicate " + libraryActionName, "New name : ", false, new StringNameValidator(), new InputDialogListener() {
            @Override
            public void finished(String input) {
                if (input == null || input.equals("")) {
                    return;
                }

                Json json = HyperJson.getJson();
                GraphVO duplicated = json.fromJson(GraphVO.class, json.toJson(actionToDuplicate));

                Object[] payload = AddToLibraryActionCommand.getPayload(input, duplicated);
                Facade.getInstance().sendNotification(MsgAPI.ACTION_ADD_TO_LIBRARY_ACTION, payload);
            }

            @Override
            public void canceled() {
                cancel();
            }
        });
    }
}
