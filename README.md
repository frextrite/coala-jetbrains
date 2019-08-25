# coala Jetbrains - coala Plugin for Jetbrains IDEs
[![Build Status](https://img.shields.io/travis/frextrite/coala-jetbrains?label=linux%20build)](https://travis-ci.org/frextrite/coala-jetbrains)  [![Build Status](https://img.shields.io/appveyor/ci/frextrite/coala-jetbrains?label=windows%20build)](https://ci.appveyor.com/project/frextrite/coala-jetbrains)  [![License: AGPL v3](https://img.shields.io/github/license/frextrite/coala-jetbrains)](https://www.gnu.org/licenses/agpl-3.0)

An IntelliJ plugin for [coala](https://coala.io). coala provides a unified interface for linting and fixing code with a single configuration file, regardless of the programming languages used. The same power of coala now in IntelliJ IDEs.

## Steps to run
1. Install `coala-jetbrains` plugin from official Jetbrains Plugin repository
2. Go to `File > Settings > coala` and set coala executable location
3. Create a coala configuration file (`.coafile`) in project's root
4. Click on `Analyze with coala` menu-entry under `Analyze` menu to run coala. Alternatively, `right-click` in an opened editor and click on `Analyze with coala`


### Things to note while creating `.coafile`
* Sections with tag `jetbrains` will be run by the plugin
* Make sure the value of `files` parameter contains files that are a part of the current project, failure to do so may result in unexpected behaviour


## Develop with IntelliJ
1. Import the project as a Gradle project
2. Open **Gradle toolbar** and click on the refresh icon to **Reimport All Gradle Projects**
3. We have pre-created Gradle run-configuration for you to run the plugin. Just select `coala Plugin` from run-configuration menu to run the plugin
4. IntelliJ logs are logged in `$PROJECT_DIR/build/idea-sandbox/system/log/idea.log`

### Creating a new Run Configuration
1. Goto `Run > Edit Configurations`
2. Click on `Add new configuration` plus icon and select `Gradle` and enter a unique name
3. Under `Configurations` tab, select current project as `Gradle project` and set `Task` as `:runIde`
4. Under `Logs` tab, click on `+` icon, enter any `Alias` and as the location specify `$PROJECT_DIR/build/idea-sandbox/system/log/idea.log`


## Filing issues
Please use our [issue tracker](https://github.com/frextrite/coala-jetbrains/issues) for reporting coala Jetbrains issues.

While creating a new issue, mention your system information, the version of plugin you are working with and the exact steps taken to reproduce the problem and if possible attach the stacktrace aswell.
