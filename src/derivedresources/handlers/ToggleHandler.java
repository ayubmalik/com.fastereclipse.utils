package derivedresources.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ToggleHandler extends AbstractHandler {

    private boolean toggled = false;

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        showMessage(window);
        toggled = !toggled;
        System.out.println("TOGGLED: " + toggled);
        return null;
    }

    private void showMessage(IWorkbenchWindow window) {
        MessageDialog.openInformation(window.getShell(), "Dkill", "TODO: List Candidate Derived Resources - toggled = "
                + toggled);
    }
}
