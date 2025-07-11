package games.rednblack.editor.controller.commands;

import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import games.rednblack.editor.controller.SandboxCommand;
import games.rednblack.h2d.common.H2DDialogs;
import games.rednblack.h2d.common.MsgAPI;
import games.rednblack.puremvc.interfaces.INotification;

public class RenameLibraryActionCommand extends SandboxCommand {
    private static final String CLASS_NAME = "games.rednblack.editor.controller.commands.resource.RenameActionCommand";
    public static final String DONE = CLASS_NAME + "DONE";

    @Override
    public void execute(INotification notification) {
        super.execute(notification);

        String libraryActionName = notification.getBody();

        H2DDialogs.showInputDialog(sandbox.getUIStage(), "Rename Action", "Name", false, new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                String[] payload = new String[2];
                payload[0] = libraryActionName;
                payload[1] = input;
                facade.sendNotification(MsgAPI.ACTION_CHANGE_LIBRARY_ACTION, payload);
            }
        }).setText(libraryActionName);
    }
}
