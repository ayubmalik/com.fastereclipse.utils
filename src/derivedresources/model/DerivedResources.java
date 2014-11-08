package derivedresources.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class DerivedResources {

    private final List<String> ignoredResources = asList(".classpath", ".project", ".settings", ".git", ".svn");
    private final List<String> candidateDerivedResourceNames = asList("bin", "build", "target");
    private final IWorkspace workspace;

    public DerivedResources() {
        workspace = ResourcesPlugin.getWorkspace();
    }

    public List<String> getAllInWorkspace() {
        return getFolders();
    }

    private List<String> getFolders() {
        IProject[] projects = workspace.getRoot().getProjects();
        List<String> folders = new ArrayList<String>();
        try {
            for (IProject p : projects) {
                for (IResource m : p.members()) {
                    if (isCandidate(m)) {
                        folders.add(makeName(p, m));
                    }
                }
            }
            return folders;
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String makeName(IProject p, IResource m) {
        StringBuilder sb = new StringBuilder();
        sb.append(m.isDerived() ? "\u25C8 " : "\u25C7 ").append(p.getName()).append("/").append(m.getName());
        return sb.toString();
    }

    private boolean isCandidate(IResource m) {
        String folder = m.getName();
        return !ignoredResources.contains(folder) && candidateDerivedResourceNames.contains(folder);
    }

}
