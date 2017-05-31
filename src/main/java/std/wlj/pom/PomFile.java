package std.wlj.pom;

import java.util.*;

public class PomFile {
	String groupId;
	String artifactId;
	String version;
	String name;
	String directory;

	PomFile parent;
	List<PomFile> children = new ArrayList<>();

	Map<String,String> properties = new TreeMap<>();
	List<Dependency> managedDependencies = new ArrayList<>();
	List<Dependency> dependencies = new ArrayList<>();
	List<Plugin> managedPlugins = new ArrayList<>();
	List<Plugin> plugins = new ArrayList<>();
	Set<String> propertyUsage = new TreeSet<>();
}
