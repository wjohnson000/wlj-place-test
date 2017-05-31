package std.wlj.pom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CheckPomFile {
	public static void main(String... args) {
		String projectDir = "C:/Users/wjohnson000/git/std-lib-place";
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
                    System.out.println("   " + childPom.directory + "|" + childPom.properties.get(prop) + msg);
                }
            }
		}
	}

	public static void checkDependencies(PomFile pomFile) {
		System.out.println();
		System.out.println();
		System.out.println(">> DEPENDENCIES <<");
		Set<String> dependencyNames = extractAllDependencies(pomFile);

		for (String depName : dependencyNames) {
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

	private static Set<String> extractAllProperties(PomFile pomFile) {
		Set<String> propNames = new TreeSet<>();
		propNames.addAll(pomFile.properties.keySet());
		for (PomFile childPom : pomFile.children) {
			propNames.addAll(childPom.properties.keySet());
		}
		return propNames;
	}

	private static Set<String> extractAllDependencies(PomFile pomFile) {
		Set<String> dependencies = new TreeSet<>();
		addDpendencies(dependencies, pomFile);
		for (PomFile childPom : pomFile.children) {
			addDpendencies(dependencies, childPom);
		}
		return dependencies;
	}

	private static void addDpendencies(Set<String> dependencies, PomFile pomFile) {
		for (Dependency dd : pomFile.managedDependencies) {
			dependencies.add(generateDependencyKey(dd));
		}
		for (Dependency dd : pomFile.dependencies) {
			dependencies.add(generateDependencyKey(dd));
		}
	}

	private static String generateDependencyKey(Dependency dependency) {
		return dependency.groupId + "::" + dependency.artifactId;
	}
}
