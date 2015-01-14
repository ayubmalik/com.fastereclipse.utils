package com.fastereclipse.utils.dr.handlers;

import static com.fastereclipse.utils.dr.handlers.MyEvents.TOGGLED_RESOURCES;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;

import com.fastereclipse.utils.dr.model.DerivedResources;

public class ToggleHandler extends AbstractHandler {

    private final DerivedResources derivedResources = new DerivedResources(ResourcesPlugin.getWorkspace());
    private boolean derivedToggle = false;

    public Object execute(ExecutionEvent event) throws ExecutionException {
        derivedResources.setAllCandidatesInWorkspaceToDerived(derivedToggle);
        derivedToggle = !derivedToggle;
        publishEvent();
        return null;
    }

    private void publishEvent() {
        Object service = PlatformUI.getWorkbench().getService(IEventBroker.class); 
        ((IEventBroker) service).post(TOGGLED_RESOURCES, new Object());
    }
}
