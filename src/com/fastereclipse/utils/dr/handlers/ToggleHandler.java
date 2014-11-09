package com.fastereclipse.utils.dr.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;

import com.fastereclipse.utils.dr.model.DerivedResources;

public class ToggleHandler extends AbstractHandler {

    private final DerivedResources derivedResources = new DerivedResources(ResourcesPlugin.getWorkspace());
    private boolean derivedToggle = false;

    public Object execute(ExecutionEvent event) throws ExecutionException {
        derivedResources.setAllCandidatesInWorkspaceToDerived(derivedToggle);
        derivedToggle = !derivedToggle;
        return null;
    }
}
