package games.rednblack.editor.view.ui;

import com.kotcrab.vis.ui.widget.VisTable;
import games.rednblack.editor.utils.HyperLap2DUtils;
import games.rednblack.editor.view.menu.HyperLap2DMenuBar;
import games.rednblack.editor.view.menu.HyperLap2DMenuBarMediator;
import games.rednblack.editor.view.ui.box.*;
import games.rednblack.editor.view.ui.widget.H2DLogo;
import games.rednblack.puremvc.Facade;
import org.apache.commons.lang3.SystemUtils;

public class UIMainTable extends VisTable {
    private final VisTable topTable, middleTable;
    private final Facade facade;

	public UIMainTable() {
        facade = Facade.getInstance();

        setFillParent(true);
        top();
        topTable = new VisTable();
        middleTable = new VisTable();
        add(topTable).fillX().expandX();
        row();
        add(middleTable).fillX().growY().padTop(1);

        initMenuBar();
        topTable.row();
		initSupportMenus();
        initToolsPanel();
        initLeftBoxesPanel();
        initRightBoxesPanel();
    }

	private void initMenuBar() {
		topTable.add(new H2DLogo()).left().fillY();

        HyperLap2DMenuBarMediator hyperlap2DMenuBarMediator = facade.retrieveMediator(HyperLap2DMenuBarMediator.NAME);
        HyperLap2DMenuBar menuBar = hyperlap2DMenuBarMediator.getViewComponent();

		if (SystemUtils.IS_OS_WINDOWS) {
            topTable.add(menuBar.getTable()).height(32);

            UIWindowTitleMediator uiWindowTitleMediator = facade.retrieveMediator(UIWindowTitleMediator.NAME);
            UIWindowTitle uiWindowTitle = uiWindowTitleMediator.getViewComponent();
            topTable.add(uiWindowTitle).growX().fillY();

            UIWindowActionMediator uiWindowActionMediator = facade.retrieveMediator(UIWindowActionMediator.NAME);
            UIWindowAction uiWindowAction = uiWindowActionMediator.getViewComponent();
            topTable.add(uiWindowAction).padTop(-1).fillY();
        } else if (SystemUtils.IS_OS_MAC) {
            topTable.add(menuBar.getTable()).height(32);

            UIWindowTitleMediator uiWindowTitleMediator = facade.retrieveMediator(UIWindowTitleMediator.NAME);
            UIWindowTitle uiWindowTitle = uiWindowTitleMediator.getViewComponent();
            HyperLap2DUtils.setWindowDragListener(uiWindowTitle);
            topTable.add(uiWindowTitle).growX().fillY();
        } else {
            topTable.add(menuBar.getTable()).growX().height(32);
        }
	}

    private void initSupportMenus() {
		UISubmenuBar compositePanel = new UISubmenuBar();
        topTable.add(compositePanel).fillX().colspan(topTable.getChildren().size).expandX().height(32);
    }

	private void initLeftBoxesPanel() {
		//Align
		VisTable leftBoxesPanel = new VisTable();
		UIAlignBoxMediator uiAlignBoxMediator = facade.retrieveMediator(UIAlignBoxMediator.NAME);
		UIAlignBox uiAlignBox = uiAlignBoxMediator.getViewComponent();
		leftBoxesPanel.add(uiAlignBox).expandX().fillX();
		leftBoxesPanel.row();

		//TreeView
		UIItemsTreeBoxMediator uiItemsTreeBoxMediator = facade.retrieveMediator(UIItemsTreeBoxMediator.NAME);
		UIItemsTreeBox itemsBox = uiItemsTreeBoxMediator.getViewComponent();
		leftBoxesPanel.add(itemsBox).fillX().maxHeight(660).top();
		middleTable.add(leftBoxesPanel).top().left().expand().padTop(15).padLeft(16);
	}

    private void initRightBoxesPanel() {
        VisTable rightPanel = new VisTable();

        //PropertyBox
        UIMultiPropertyBoxMediator multiPropertyBoxMediator = facade.retrieveMediator(UIMultiPropertyBoxMediator.NAME);
        UIMultiPropertyBox multiPropertyBox = multiPropertyBoxMediator.getViewComponent();
        rightPanel.add(multiPropertyBox).top();
        rightPanel.row();

        //ResourcesBox
        UIResourcesBoxMediator resourceBoxMediator = facade.retrieveMediator(UIResourcesBoxMediator.NAME);
        UIResourcesBox resourceBox = resourceBoxMediator.getViewComponent();
        rightPanel.add(resourceBox).top();
        rightPanel.row();

        //LayerBox
        UILayerBoxMediator layerBoxMediator = facade.retrieveMediator(UILayerBoxMediator.NAME);
        UILayerBox layerBox = layerBoxMediator.getViewComponent();
        rightPanel.add(layerBox).top();

        middleTable.add(rightPanel).top().right().expand().padTop(15);
    }

    private void initToolsPanel() {
        VisTable toolsPanel = new VisTable();
        toolsPanel.background("toolbar-bg");
        //
        UIToolBoxMediator uiToolBoxMediator = facade.retrieveMediator(UIToolBoxMediator.NAME);
        UIToolBox uiToolBox = uiToolBoxMediator.getViewComponent();
        toolsPanel.add(uiToolBox).top().expandY().padTop(4);
        //
        middleTable.add(toolsPanel).top().left().width(40).fillY().expandY();
    }
}
