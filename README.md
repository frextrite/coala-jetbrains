# coala Jetbrains - coala Plugin for Jetbrains IDEs
[![Build Status](https://img.shields.io/travis/frextrite/coala-jetbrains?label=linux%20build)](https://travis-ci.org/frextrite/coala-jetbrains)  [![Build Status](https://img.shields.io/appveyor/ci/frextrite/coala-jetbrains?label=windows%20build)](https://ci.appveyor.com/project/frextrite/coala-jetbrains)  [![License: AGPL v3](https://img.shields.io/github/license/frextrite/coala-jetbrains)](https://www.gnu.org/licenses/agpl-3.0)

An IntelliJ plugin for [coala](https://coala.io). coala provides a unified interface for linting and fixing code with a single configuration file, regardless of the programming languages used. The same power of coala now in IntelliJ IDEs.

## Steps to run
1. Install `coala-jetbrains` plugin from official Jetbrains Plugin repository
2. Go to `File > Settings > coala` and set coala executable location
3. Create a coala configuration file (`.coafile`) and set the value of `tag` parameter to `jetbrains` for the sections you wish to analyze with the plugin
4. Click on `Analyze with coala` menu-entry under `Analyze` menu to run coala. Alternatively, `right-click` in an opened editor and click on `Analyze with coala`


## Filing issues
Please use our [issue tracker](https://github.com/frextrite/coala-jetbrains/issues) for reporting coala Jetbrains issues.

While creating a new issue, mention your system information, the version of plugin you are working with and the exact steps taken to reproduce the problem and if possible attach the stacktrace aswell.
