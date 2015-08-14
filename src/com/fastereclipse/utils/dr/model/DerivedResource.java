package com.fastereclipse.utils.dr.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class DerivedResource {

    private final IResource delegate;

    public DerivedResource(IResource delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getFullPath().toString();
    }

    public boolean isDerived() {
        return delegate.isDerived();
    }

    public void setDerived(boolean derived) {
        try {
            delegate.setDerived(derived, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return delegate.getFullPath().toString();
    }
}
