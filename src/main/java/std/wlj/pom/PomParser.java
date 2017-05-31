package std.wlj.pom;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PomParser {

	private static class TagAndValue {
		private String tag;
		private String value;
	}

	private static final String PARENT_TAG = "parent";
	private static final String NAME_TAG = "name";
	private static final String GROUP_ID_TAG = "groupId";
	private static final String ARTIFACT_ID_TAG = "artifactId";
	private static final String VERSION_TAG = "version";
	private static final String PLUGIN_TAG = "plugin";
	private static final String PLUGIN_MGT_TAG = "pluginManagement";
	private static final String DEPENDENCY_TAG = "dependency";
	private static final String DEPENDENCY_MGT_TAG = "dependencyManagement";
	private static final String PROPERTIES_TAG = "properties";
    private static final String CONFIGURATION_TAG = "configuration";
    private static final String EXCLUSIONS_TAG = "exclusions";

	public static PomFile parseFrom(String path) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
			return parseFrom(lines);
		} catch (IOException e) {
			System.out.println("Ugly exception for '" + path + "': " + e.getMessage());
		}

		return null;
	}

	public static PomFile parseFrom(List<String> lines) {
		boolean inComment = false;
		boolean inParent = false;
		boolean inProperties = false;
		boolean inDependency = false;
		boolean inDependencyMgt = false;
		boolean inPlugin = false;
		boolean inPluginMgt = false;
		boolean inConfiguration = false;
		boolean inExclusions = false;

		PomFile pomFile = new PomFile();
		Plugin plugin = null;
		Dependency parent = null;
		Dependency dependency = null;

		for (String line : lines) {
		    // Check for comment beginning+end, comment beginning-only and comment end-only
			line = removeFullComment(line);
			if (isComment(line)) {
				inComment = true;
			} else if (isCommentEnd(line)) {
				inComment = false;
			}
			if (inComment) {
				continue;
			}

			TagAndValue tagValue = getTagAndValue(line);
			if (! inProperties  &&  isProperty(tagValue.value)) {
				pomFile.propertyUsage.add(tagValue.value);
			}

			if (isParent(tagValue)) {
				inParent = true;
				parent = new Dependency();
			} else if (isParentEnd(tagValue)) {
				inParent = false;
				parent = null;
			} else if (isProperties(tagValue)) {
				inProperties = true;
			} else if (isPropertiesEnd(tagValue)) {
				inProperties = false;
			} else if (isPluginManagement(tagValue)) {
				inPluginMgt = true;
			} else if (isPluginManagementEnd(tagValue)) {
				inPluginMgt = false;
			} else if (isPlugin(tagValue)) {
				inPlugin = true;
				plugin = new Plugin();
			} else if (isPluginEnd(tagValue)) {
				inPlugin = false;
				if (inPluginMgt) {
					pomFile.managedPlugins.add(plugin);
				} else {
					pomFile.plugins.add(plugin);
				}
				plugin = null;
			} else if (isDependencyManagement(tagValue)) {
				inDependencyMgt = true;
			} else if (isDependencyManagementEnd(tagValue)) {
				inDependencyMgt = false;
			} else if (isDependency(tagValue)) {
				inDependency = true;
				dependency = new Dependency();
			} else if (isDependencyEnd(tagValue)) {
				inDependency = false;
				if (inDependencyMgt) {
					pomFile.managedDependencies.add(dependency);
				} else {
					pomFile.dependencies.add(dependency);
				}
				dependency = null;
			} else if (isConfiguration(tagValue)) {
			    inConfiguration = true;
            } else if (isConfigurationEnd(tagValue)) {
                inConfiguration = false;
            } else if (isExclusions(tagValue)) {
                inExclusions = true;
            } else if (isExclusionsEnd(tagValue)) {
                inExclusions = false;
			} else {
				if (inProperties  &&  ! inPlugin  &&  ! inPluginMgt  &&  ! inConfiguration  &&  ! inExclusions) {
					if (tagValue != null  &&  tagValue.tag != null  &&  tagValue.value != null) {
						pomFile.properties.put(tagValue.tag, tagValue.value);
					}
				} else if (isName(tagValue)) {
					if (! inParent  &&  ! inPlugin  &&  ! inDependency) {
						pomFile.name = tagValue.value;
					}
				} else if (isGroupId(tagValue)) {
					if (inParent) {
						parent.groupId = tagValue.value;
					} else if (inPlugin) {
						plugin.groupId = tagValue.value;
					} else if (inDependency) {
						dependency.groupId = tagValue.value;
					} else {
						pomFile.groupId = tagValue.value;
					}
				} else if (isArtifactId(tagValue)) {
					if (inParent) {
						parent.artifactId = tagValue.value;
					} else if (inPlugin) {
						plugin.artifactId = tagValue.value;
					} else if (inDependency) {
						dependency.artifactId = tagValue.value;
					} else {
						pomFile.artifactId = tagValue.value;
					}
				} else if (isVersion(tagValue)) {
					if (inParent) {
						parent.version = tagValue.value;
					} else if (inPlugin) {
						plugin.version = tagValue.value;
					} else if (inDependency) {
						dependency.version = tagValue.value;
					} else {
						pomFile.version = tagValue.value;
					}
				}
			}
		}

		return pomFile;
	}
	
	private static boolean isProperty(String value) {
	    return (value != null  &&  value.startsWith("${"));
	}

	private static String removeFullComment(String line) {
		String temp = line;

		int ndx0 = temp.indexOf("<!--");
		int ndx1 = temp.indexOf("-->");
		while (ndx0 > 0  &&  ndx1 > ndx0) {
			String before = (ndx0 == 0) ? "" : temp.substring(0, ndx0);
			String after  = (ndx1+3 >= temp.length()) ? "" : temp.substring(ndx1+3);
			temp = before + " " + after;
			ndx0 = temp.indexOf("<!--");
			ndx1 = temp.indexOf("-->");
		}

		return temp;
	}

	private static boolean isComment(String line) {
		int ndx0 = line.indexOf("<!--");
		int ndx1 = line.indexOf("-->");
		return (ndx0 >= 0  &&  ndx1 == -1);
	}

	private static boolean isCommentEnd(String line) {
		int ndx0 = line.indexOf("<!--");
		int ndx1 = line.indexOf("-->");
		return (ndx0 == -1  &&  ndx1 >= 0);
	}

	private static boolean isName(TagAndValue tagValue) {
		return (NAME_TAG.equals(tagValue.tag));
	}

	private static boolean isGroupId(TagAndValue tagValue) {
		return (GROUP_ID_TAG.equals(tagValue.tag));
	}

	private static boolean isArtifactId(TagAndValue tagValue) {
		return (ARTIFACT_ID_TAG.equals(tagValue.tag));
	}

	private static boolean isVersion(TagAndValue tagValue) {
		return (VERSION_TAG.equals(tagValue.tag));
	}

	private static boolean isParent(TagAndValue tagValue) {
		return (PARENT_TAG.equals(tagValue.tag));
	}

	private static boolean isParentEnd(TagAndValue tagValue) {
		return (endTag(PARENT_TAG).equals(tagValue.tag));
	}

	private static boolean isProperties(TagAndValue tagValue) {
		return (PROPERTIES_TAG.equals(tagValue.tag));
	}

	private static boolean isPropertiesEnd(TagAndValue tagValue) {
		return (endTag(PROPERTIES_TAG).equals(tagValue.tag));
	}

	private static boolean isPluginManagement(TagAndValue tagValue) {
		return (PLUGIN_MGT_TAG.equals(tagValue.tag));
	}

	private static boolean isPluginManagementEnd(TagAndValue tagValue) {
		return (endTag(PLUGIN_MGT_TAG).equals(tagValue.tag));
	}

	private static boolean isPlugin(TagAndValue tagValue) {
		return (PLUGIN_TAG.equals(tagValue.tag));
	}

	private static boolean isPluginEnd(TagAndValue tagValue) {
		return (endTag(PLUGIN_TAG).equals(tagValue.tag));
	}

	private static boolean isDependencyManagement(TagAndValue tagValue) {
		return (DEPENDENCY_MGT_TAG.equals(tagValue.tag));
	}

	private static boolean isDependencyManagementEnd(TagAndValue tagValue) {
		return (endTag(DEPENDENCY_MGT_TAG).equals(tagValue.tag));
	}

	private static boolean isDependency(TagAndValue tagValue) {
		return (DEPENDENCY_TAG.equals(tagValue.tag));
	}

	private static boolean isDependencyEnd(TagAndValue tagValue) {
		return (endTag(DEPENDENCY_TAG).equals(tagValue.tag));
	}

    private static boolean isConfiguration(TagAndValue tagValue) {
        return (CONFIGURATION_TAG.equals(tagValue.tag));
    }

    private static boolean isConfigurationEnd(TagAndValue tagValue) {
        return (endTag(CONFIGURATION_TAG).equals(tagValue.tag));
    }

    private static boolean isExclusions(TagAndValue tagValue) {
        return (EXCLUSIONS_TAG.equals(tagValue.tag));
    }

    private static boolean isExclusionsEnd(TagAndValue tagValue) {
        return (endTag(EXCLUSIONS_TAG).equals(tagValue.tag));
    }

	private static TagAndValue getTagAndValue(String line) {
		TagAndValue tagValue = new TagAndValue();

		int ndx0 = line.indexOf('<');
		int ndx1 = line.indexOf('>', ndx0+1);
		int ndx2 = line.indexOf('<', ndx1+1);
		int ndx3 = line.indexOf('>', ndx2+1);

		if (ndx0 >= 0  &&  ndx1 > ndx0) {
			tagValue.tag = line.substring(ndx0+1, ndx1); 
		}

		if (ndx2 >= 0  &&  ndx3 > ndx2) {
			tagValue.value = line.substring(ndx1+1,  ndx2);
		}
		return tagValue;
	}

	private static String endTag(String tag) {
		return "/" + tag;
	}
}
