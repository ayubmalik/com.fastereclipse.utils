package com.fastereclipse.utils.dr.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;

public class DerivedResources {

    private final List<String> candidateDerivedResourceNames = asList("bin", "build", "target");
    private final IWorkspace workspace;

    public DerivedResources(IWorkspace workspace) {
        this.workspace = workspace;
    }

    public List<DerivedResource> getAllCandidatesInWorkspace() {
        return allCandidatesInWorkspace();
    }

    public void setAllCandidatesInWorkspaceToDerived(boolean derived) {
        for (DerivedResource dr : allCandidatesInWorkspace()) {
            dr.setDerived(derived);
        }
    }

    private List<DerivedResource> allCandidatesInWorkspace() {
        IProject[] projects = workspace.getRoot().getProjects();
        List<DerivedResource> candidates = new ArrayList<>();
        try {
            addCandidatesFromAllProjects(projects, candidates);
            return candidates;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addCandidatesFromAllProjects(IProject[] projects, List<DerivedResource> candidates)
            throws CoreException {
        for (IProject p : projects) {
            addCandidatesFromSingleProject(candidates, p);
        }
    }

    private void addCandidatesFromSingleProject(List<DerivedResource> candidates, IProject p) throws CoreException {
        for (IResource folder : p.members()) {
            if (isCandidate(folder)) {
                candidates.add(new DerivedResource(folder));
            }
        }
    }

    private boolean isCandidate(IResource folder) {
        return candidateDerivedResourceNames.contains(folder.getName());
    }

}
