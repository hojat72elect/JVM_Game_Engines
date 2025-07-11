package games.rednblack.editor.view.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import games.rednblack.editor.view.ui.box.bottom.*;
import games.rednblack.h2d.common.view.ui.StandardWidgetsFactory;
import games.rednblack.puremvc.Facade;

public class UIBottomMenuBar extends VisTable {
	private final Facade facade;

	public UIBottomMenuBar() {
		Skin skin = VisUI.getSkin();
		facade = Facade.getInstance();

		setBackground(skin.getDrawable("sub-menu-bg"));

		VisTable mainGroup = new VisTable();
		VisScrollPane scrollPane = StandardWidgetsFactory.createScrollPane(mainGroup);
		add(scrollPane).fill().padLeft(5).padRight(5);

		//scene
		UISceneBoxMediator uiSceneBoxMediator = facade.retrieveMediator(UISceneBoxMediator.NAME);
		UISceneBox uiSceneBox = uiSceneBoxMediator.getViewComponent();
		mainGroup.add(uiSceneBox);

		//grid
		UIGridBoxMediator uiGridBoxMediator = facade.retrieveMediator(UIGridBoxMediator.NAME);
		UIGridBox uiGridBox = uiGridBoxMediator.getViewComponent();
		mainGroup.add(uiGridBox);

		//zoom
		UIZoomBoxMediator uiZoomBoxMediator = facade.retrieveMediator(UIZoomBoxMediator.NAME);
		UIZoomBox uiZoomBox = uiZoomBoxMediator.getViewComponent();
		mainGroup.add(uiZoomBox);

		//pan
		UIPanBoxMediator uiPanBoxMediator = facade.retrieveMediator(UIPanBoxMediator.NAME);
		UIPanBox uiPanBox = uiPanBoxMediator.getViewComponent();
		mainGroup.add(uiPanBox);

		//resolution box
		UIResolutionBoxMediator uiResolutionBoxMediator = facade.retrieveMediator(UIResolutionBoxMediator.NAME);
		UIResolutionBox uiResolutionBox = uiResolutionBoxMediator.getViewComponent();
		mainGroup.add(uiResolutionBox);

		//live preview
		UILivePreviewBoxMediator uiLivePreviewBoxMediator = facade.retrieveMediator(UILivePreviewBoxMediator.NAME);
		UILivePreviewBox uiLivePreviewBox = uiLivePreviewBoxMediator.getViewComponent();
		mainGroup.add(uiLivePreviewBox);
	}
}
