package games.rednblack.editor.controller.commands;

import com.kotcrab.vis.ui.util.Validators;
import games.rednblack.editor.controller.SandboxCommand;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.utils.runtime.SandboxComponentRetriever;
import games.rednblack.h2d.common.command.TransformCommandBuilder;
import games.rednblack.h2d.common.view.ui.dialog.MultipleInputDialog;
import games.rednblack.h2d.common.view.ui.listener.MultipleInputDialogListener;
import games.rednblack.puremvc.Facade;
import games.rednblack.puremvc.interfaces.INotification;

public class ChangeOriginPointPosition extends SandboxCommand {

    @Override
    public void execute(INotification notification) {
        super.execute(notification);

        int entity = notification.getBody();
        TransformComponent transformComponent = SandboxComponentRetriever.get(entity, TransformComponent.class);

        MultipleInputDialog dialog = new MultipleInputDialog("Origin Position", new String[]{"X : ", "Y : "}, false, new Validators.FloatValidator(), new MultipleInputDialogListener() {
            @Override
            public void finished(String[] input) {
                TransformCommandBuilder commandBuilder = new TransformCommandBuilder();
                commandBuilder.begin(entity, sandbox.getEngine());
                commandBuilder.setOrigin(Float.parseFloat(input[0]), Float.parseFloat(input[1]));
                commandBuilder.execute(Facade.getInstance());
            }

            @Override
            public void canceled() {

            }
        });

        dialog.setText(new String[]{transformComponent.originX+"", transformComponent.originY+""});
        sandbox.getUIStage().addActor(dialog.fadeIn());
    }
}
