package std.wlj.pom;

import java.util.*;

public class PomFile {
    // Basic properties ...
	String groupId;
	String artifactId;
	String version;
	String name;
	String directory;

	// Parent, children
	Dependency parent;
	PomFile parentPom;
	List<PomFile> children = new ArrayList<>();

	// Details
	Map<String,String> properties = new TreeMap<>();
	Set<String> propertyUsage = new TreeSet<>();
	List<Dependency> managedDependencies = new ArrayList<>();
	List<Dependency> dependencies = new ArrayList<>();
	List<Plugin> managedPlugins = new ArrayList<>();
	List<Plugin> plugins = new ArrayList<>();
}
