package com.fastereclipse.utils.dr.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
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

    public List<DerivedResource> allDerivedResourcesInWorkspace() {
        return allCandidateDerivedResourcesInWorkspace();
    }

    public void setAllCandidatesInWorkspaceToDerived(boolean derived) {
        for (DerivedResource dr : allCandidateDerivedResourcesInWorkspace()) {
            dr.setDerived(derived);
        }
    }

    private List<DerivedResource> allCandidateDerivedResourcesInWorkspace() {
        IProject[] projects = workspace.getRoot().getProjects();
        List<DerivedResource> candidates = new ArrayList<DerivedResource>();
        try {
            addCandidatesFromAllProjects(projects, candidates);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return candidates;
    }

    private void addCandidatesFromAllProjects(IProject[] projects, List<DerivedResource> candidates)
            throws CoreException {
        for (IProject p : projects) {
            processContainer(candidates, p);
        }
    }

    private void processContainer(List<DerivedResource> candidates, IContainer parent) throws CoreException {
        if (parent.isAccessible()) {
            for (IResource member : parent.members()) {
                if (isFolder(member)) {
                    System.out.println("processing folder: " + parent.getFullPath());
                    IFolder folder = (IFolder) member;
                    processContainer(candidates, folder);
                    addFolderIfCandidate(candidates, folder);
                }
            }
        }
    }

    private void addFolderIfCandidate(List<DerivedResource> candidates, IFolder folder) throws CoreException {
        if (isCandidate(folder)) {
            candidates.add(new DerivedResource(folder));
        }
    }

    private boolean isFolder(IResource folder) {
        return folder.getType() == 2;
    }

    private boolean isCandidate(IResource folder) {
        return candidateDerivedResourceNames.contains(folder.getName());
    }

}
