package std.wlj.pom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CheckPomFile {
	public static void main(String... args) {
		String projectDir = "C:/Users/wjohnson000/git/std-ws-place";
		String pomPath = projectDir + "/" + "pom.xml";
		List<String> subDirs = new ArrayList<>();

		for (String subDir : new File(projectDir).list()) {
			String tPath = projectDir + "/" + subDir + "/" + "pom.xml";
			File tFile = new File(tPath);
			if (tFile.exists() && tFile.isFile()) {
				subDirs.add(subDir);
			}
		}

		PomFile parentPom = PomParser.parseFrom(pomPath);
		parentPom.directory = "parent";
		for (String subdir : subDirs) {
			String tPath = projectDir + "/" + subdir + "/" + "pom.xml";
			PomFile childPom = PomParser.parseFrom(tPath);
			childPom.directory = subdir;
			parentPom.children.add(childPom);
			childPom.parent = parentPom;
			fixChildDependencies(childPom);
		}

		checkProperties(parentPom);
		checkDependencies(parentPom);
	}

    public static void checkProperties(PomFile pomFile) {
		System.out.println(">> PROPERTIES <<");
		Set<String> allProps = extractAllProperties(pomFile);
		for (String prop : allProps) {
		    System.out.println(prop);
		    String propValue = null;
            if (pomFile.properties.containsKey(prop)) {
                propValue = pomFile.properties.get(prop);
                System.out.println("   " + pomFile.directory + "|" + pomFile.properties.get(prop));
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

	static void fixChildDependencies(PomFile childPom) {
        for (Dependency dependency : childPom.dependencies) {
	        Dependency parDependency = childPom.parent.managedDependencies.stream()
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
		return propNames;
	}

	static Set<String> extractAllDependencies(PomFile pomFile) {
		Set<String> dependencies = new TreeSet<>();
		addDpendencies(dependencies, pomFile);
		for (PomFile childPom : pomFile.children) {
			addDpendencies(dependencies, childPom);
		}
		return dependencies;
	}

	static void addDpendencies(Set<String> dependencies, PomFile pomFile) {
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
}
