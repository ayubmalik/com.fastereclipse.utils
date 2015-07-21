package com.fastereclipse.utils.dr.views;

import static com.fastereclipse.utils.dr.handlers.MyEvents.TOGGLED_RESOURCES;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.fastereclipse.utils.dr.model.DerivedResource;
import com.fastereclipse.utils.dr.model.DerivedResources;

public class DerivedResourcesView extends ViewPart implements EventHandler {

    public static final String ID = "com.fastereclipse.utils.dr.views.DerivedResourcesView";

    private TableViewer viewer;
    private Action action1;
    private Action doubleClickAction;

    private final DerivedResources derivedResources = new DerivedResources(ResourcesPlugin.getWorkspace());

    @Override
    public void createPartControl(Composite parent) {
        viewer = new TableViewer(parent, SWT.NO_FOCUS | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.getTable().setHeaderVisible(true);
        createColumns(parent, viewer);
        getSite().setSelectionProvider(viewer);

        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "dkill.viewer");
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        viewer.setInput(derivedResources.allDerivedResourcesInWorkspace());
        viewer.refresh();
        subscribeToEvents();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                DerivedResourcesView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(action1);
        manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
    }

    private void makeActions() {
        action1 = new Action() {
            @Override
            public void run() {
                toggle();
                viewer.setInput(derivedResources.allDerivedResourcesInWorkspace());
            }
        };
        action1.setText("Toggle ALL");
        action1.setToolTipText("Toggle ALL Derived Resources");

        doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                showMessage("Double-click detected on " + obj.toString());
            }
        };
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), "DerivedResourcesView", message);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    private void createColumns(Composite parent, TableViewer viewer) {
        // first column is for the resource name
        TableViewerColumn col = createTableViewerColumn("Resources", 512);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                DerivedResource dr = (DerivedResource) element;
                return dr.getName();
            }
        });

        // second column is for derived flag
        col = createTableViewerColumn("Derived?", 256);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                DerivedResource dr = (DerivedResource) element;
                return dr.isDerived() + "";
            }
        });
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound) {
        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        return viewerColumn;
    }

    private void toggle() {
        IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
        try {
            handlerService.executeCommand("com.fastereclipse.utils.dr.commands.toggle", null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("com.fastereclipse.utils.dr.commands.toggle error!");
        }
    }

    @Override
    public void handleEvent(Event event) {
        viewer.refresh();
    }

    private void subscribeToEvents() {
        Object service = PlatformUI.getWorkbench().getService(IEventBroker.class);
        if (service instanceof IEventBroker)
            ((IEventBroker) service).subscribe(TOGGLED_RESOURCES, this);
    }

}