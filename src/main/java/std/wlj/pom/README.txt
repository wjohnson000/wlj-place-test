POM File best practices -- some suggestions ...
  -- Order should be
     * definition (name, group-id, etc.)
     * parent
     * modules (parent POM only)
     * properties
     * scm
     * ciManagement
     * build/pluginManagement
     * plugins
     * dependencyManagement
     * dependencies
     * profiles
     * reporting
  -- "groupId" precedes "artifactId"
  -- Parent POM file should have "pluginManagement" and "dependencyManagement", and include plugins that are used by all (or most) of the children
  -- Child POM file should NOT have "pluginManagement" or "dependencyManagement" section, unless it has its own child modules
  -- Child POM file should not, in general, override a parent POM property value
  -- All dependencies and plugins that define a <version> tag should use a property value: no hard-coded version numbers
  -- All dependency "<excludes>" should be in a parent POM file, not a child POM file
  -- Related dependencies should be contiguous
  -- All "test" dependencies should appear together, at the bottom of the <dependencies> section
  