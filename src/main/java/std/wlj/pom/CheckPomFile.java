package std.wlj.pom;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class CheckPomFile {

    static final String M2_BASE = "C:/Users/wjohnson000/.m2/repository";

	public static void main(String... args) {
		String projectDir = "C:/Users/wjohnson000/git/std-ws-place-55";
		String pomPath = projectDir + "/" + "pom.xml";
		List<String> subDirs = new ArrayList<>();

		// Parse the main POM file, then chain up to all its parents
		PomFile mainPom = PomParser.parseFrom(pomPath);
		mainPom.directory = "parent";
		
		PomFile tempPom = mainPom;
		while (tempPom.parent != null) {
		    String parDir = getM2Dir(tempPom);
		    PomFile parentPom = PomParser.parseFrom(parDir);
		    parentPom.directory = parentPom.artifactId;
		    tempPom.parentPom = parentPom;
		    tempPom = parentPom;
		}

		// Collect all sub-directories (candidates for children POM)
		for (String subDir : new File(projectDir).list()) {
			String tPath = projectDir + "/" + subDir + "/" + "pom.xml";
			File tFile = new File(tPath);
			if (tFile.exists() && tFile.isFile()) {
				subDirs.add(subDir);
			}
		}

		// Add all children POM files
		for (String subdir : subDirs) {
			String tPath = projectDir + "/" + subdir + "/" + "pom.xml";
			PomFile childPom = PomParser.parseFrom(tPath);
			childPom.directory = subdir;
			mainPom.children.add(childPom);
			childPom.parentPom = mainPom;
			fixChildDependencies(childPom);
		}

		checkProperties(mainPom);
		checkDependencies(mainPom);
		checkPlugins(mainPom);
	}

    public static String getM2Dir(PomFile tempPom) {
        String path = M2_BASE;

        String[] groupPath = PlaceHelper.split(tempPom.parent.groupId, '.');
        path += Arrays.stream(groupPath).collect(Collectors.joining("/", "/", ""));

        path += "/" + tempPom.parent.artifactId;
        path += "/" + tempPom.parent.version;
        path += "/" + tempPom.parent.artifactId + "-" + tempPom.parent.version + ".pom";

        return path;
    }

    public static void checkProperties(PomFile pomFile) {
		System.out.println(">> PROPERTIES <<");
		Set<String> allProps = extractAllProperties(pomFile);
		for (String prop : allProps) {
		    System.out.println(prop);
		    String propValue = null;

            PomFile parentPom = pomFile.parentPom;
            while (parentPom != null) {
                if (parentPom.properties.containsKey(prop)) {
                    if (propValue == null) {
                        propValue = parentPom.properties.get(prop);
                        System.out.println("   " + parentPom.directory + "|" + propValue);
                    } else {
                        System.out.println("   " + parentPom.directory + "|" + parentPom.properties.get(prop) + " -- redefined");
                    }
                }
                parentPom = parentPom.parentPom;
            }

		    if (pomFile.properties.containsKey(prop)) {
		        if (propValue == null) {
		            propValue = pomFile.properties.get(prop);
		            System.out.println("   " + pomFile.directory + "|" + propValue);
		        } else {
	                  System.out.println("   " + pomFile.directory + "|" + pomFile.properties.get(prop) + " -- redefined");
		        }
            }

            for (PomFile childPom : pomFile.children) {
                if (childPom.properties.containsKey(prop)) {
                    String msg = (propValue == null  || ! propValue.equals(childPom.properties.get(prop))) ? "" : " -- duplicate";
                    if (! pomFile.propertyUsage.contains(prop)) {
                        msg += " -- unused";
                    }
                    System.out.println("   " + childPom.directory + "|" + childPom.properties.get(prop) + msg);
                }
            }
		}

        System.out.println();
        System.out.println();
        System.out.println(">> UNDEFINED PROPERTIES <<");
		pomFile.propertyUsage.stream()
		    .filter(prop -> ! pomFile.properties.containsKey(prop))
		    .forEach(System.out::println);
	}

	public static void checkDependencies(PomFile pomFile) {
		Set<String> dependencyNames = extractAllDependencies(pomFile);

		// Two passes ... first for regular dependencies, second for "TEST" dependencies
		for (int pass=1;  pass<=2;  pass++) {
		    System.out.println();
		    System.out.println();
		    if (pass == 1) {
		        System.out.println(">> DEPENDENCIES <<");
		    } else {
                System.out.println(">> TEST DEPENDENCIES <<");
		    }

		    for (String depName : dependencyNames) {
		        if (pass == 1  &&  depName.endsWith("::test")) {
		            continue;
		        } else if (pass == 2  &&  ! depName.endsWith("::test")) {
		            continue;
		        }

		        System.out.println(depName);
		        pomFile.managedDependencies.stream()
		            .filter(dd -> generateDependencyKey(dd).equals(depName))
		            .findFirst()
		            .ifPresent(dd -> System.out.println("   mgt|" + pomFile.directory + "|" + dd.version));
		        
		        for (PomFile childPom : pomFile.children) {
		            childPom.managedDependencies.stream()
		                .filter(dd -> generateDependencyKey(dd).equals(depName))
		                .findFirst()
		                .ifPresent(dd -> System.out.println("   mgt|" + childPom.directory + "|" + dd.version + "|???"));
		        }

		        pomFile.dependencies.stream()
		            .filter(dd -> generateDependencyKey(dd).equals(depName))
		            .findFirst()
		            .ifPresent(dd -> {
		                String vWarning = (dd.version == null || dd.version.startsWith("${")) ? "" : "<bad>";
		                System.out.println("   dep|" + pomFile.directory + "|" + dd.version + "|" + vWarning);
		            });
		        
		        for (PomFile childPom : pomFile.children) {
		            childPom.dependencies.stream()
		                .filter(dd -> generateDependencyKey(dd).equals(depName))
		                .findFirst()
		                .ifPresent(dd -> {
		                    String vWarning = (dd.version == null || dd.version.startsWith("${")) ? "" : "<bad>";
		                    System.out.println("   dep|" + childPom.directory + "|" + dd.version + "|" + vWarning);
		                });
		        }
		    }
		}
	}

    public static void checkPlugins(PomFile pomFile) {
        Set<String> pluginNames = extractAllPlugins(pomFile);

        System.out.println();
        System.out.println();
        System.out.println(">> PLUGINS <<");

        for (String pluginName : pluginNames) {
            System.out.println(pluginName);

            // Managed PLUGINs
            PomFile parentPom = pomFile.parentPom;
            while (parentPom != null) {
                final String dir = parentPom.directory;
                parentPom.managedPlugins.stream()
                    .filter(pp -> generatePluginKey(pp).equals(pluginName))
                    .findFirst()
                    .ifPresent(pp -> System.out.println("   mgt|" + dir + "|" + pp.version));
                parentPom = parentPom.parentPom;
            }

            pomFile.managedPlugins.stream()
                .filter(pp -> generatePluginKey(pp).equals(pluginName))
                .findFirst()
                .ifPresent(pp -> System.out.println("   mgt|" + pomFile.directory + "|" + pp.version));

            for (PomFile childPom : pomFile.children) {
                childPom.managedPlugins.stream()
                    .filter(pp -> generatePluginKey(pp).equals(pluginName))
                    .findFirst()
                    .ifPresent(pp -> System.out.println("   mgt|" + childPom.directory + "|" + pp.version + "|???"));
            }

            // Regular PLUGINs
            parentPom = pomFile.parentPom;
            while (parentPom != null) {
                final String dir = parentPom.directory;
                parentPom.plugins.stream()
                    .filter(pp -> generatePluginKey(pp).equals(pluginName))
                    .findFirst()
                    .ifPresent(pp -> {
                        String vWarning = (pp.version == null || pp.version.startsWith("${")) ? "" : "<bad>";
                        System.out.println("   plg|" + dir + "|" + pp.version + "|" + vWarning);
                    });
                parentPom = parentPom.parentPom;
            }

            pomFile.plugins.stream()
                .filter(pp -> generatePluginKey(pp).equals(pluginName))
                .findFirst()
                .ifPresent(pp -> {
                    String vWarning = (pp.version == null || pp.version.startsWith("${")) ? "" : "<bad>";
                    System.out.println("   plg|" + pomFile.directory + "|" + pp.version + "|" + vWarning);
                });

            for (PomFile childPom : pomFile.children) {
                childPom.plugins.stream()
                    .filter(pp -> generatePluginKey(pp).equals(pluginName))
                    .findFirst()
                    .ifPresent(pp -> {
                        String vWarning = (pp.version == null || pp.version.startsWith("${")) ? "" : "<bad>";
                        System.out.println("   plg|" + childPom.directory + "|" + pp.version + "|" + vWarning);
                    });
            }
        }
    }

	static void fixChildDependencies(PomFile childPom) {
        for (Dependency dependency : childPom.dependencies) {
	        Dependency parDependency = childPom.parentPom.managedDependencies.stream()
	            .filter(parD -> parD.groupId.equals(dependency.groupId)  &&  parD.artifactId.equals(dependency.artifactId))
	            .findFirst()
	            .orElse(null);
	        if (dependency.scope == null  &&  parDependency != null) {
	            dependency.scope = parDependency.scope;
	        }
	    }
    }

	static Set<String> extractAllProperties(PomFile pomFile) {
		Set<String> propNames = new TreeSet<>();
		propNames.addAll(pomFile.properties.keySet());

		for (PomFile childPom : pomFile.children) {
			propNames.addAll(childPom.properties.keySet());
		}

		PomFile parentPom = pomFile.parentPom;
		while (parentPom != null) {
		    propNames.addAll(parentPom.properties.keySet());
		    parentPom = parentPom.parentPom;
		}

		return propNames;
	}

	static Set<String> extractAllDependencies(PomFile pomFile) {
		Set<String> dependencies = new TreeSet<>();
		addDependencies(dependencies, pomFile);
		for (PomFile childPom : pomFile.children) {
			addDependencies(dependencies, childPom);
		}
		return dependencies;
	}

	static void addDependencies(Set<String> dependencies, PomFile pomFile) {
		for (Dependency dd : pomFile.managedDependencies) {
			dependencies.add(generateDependencyKey(dd));
		}
		for (Dependency dd : pomFile.dependencies) {
			dependencies.add(generateDependencyKey(dd));
		}
	}

	static String generateDependencyKey(Dependency dependency) {
	    StringBuilder buff = new StringBuilder(64);

	    buff.append(dependency.groupId);
	    buff.append("::").append(dependency.artifactId);
	    if (dependency.scope != null) {
	        buff.append("::").append(dependency.scope);
	    }

	    return buff.toString();
	}

    static Set<String> extractAllPlugins(PomFile pomFile) {
        Set<String> plugins = new TreeSet<>();
        addPlugins(plugins, pomFile);
        for (PomFile childPom : pomFile.children) {
            addPlugins(plugins, childPom);
        }
        return plugins;
    }

    static void addPlugins(Set<String> plugins, PomFile pomFile) {
        for (Plugin pp : pomFile.managedPlugins) {
            plugins.add(generatePluginKey(pp));
        }
        for (Plugin pp : pomFile.plugins) {
            plugins.add(generatePluginKey(pp));
        }
    }

	static String generatePluginKey(Plugin plugin) {
	    return plugin.groupId + "::" + plugin.artifactId;
	}
}
