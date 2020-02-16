# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [1.7.0] - 2017-01-05
### Added
- Support for configuration variants, different maven artifacts that provide different configuration for the same service.

## [1.6.0] - 2018-01-03
### Added
- Local ports of the docker host can now be bound to container ports. All ports are mapped (to random available ports)
 by default on Windows and MacOS since there is no docker0 bridge in those environments
### Fixed
- Fixed Windows and MacOS compatibility bugs
- Fixed a bug in PullPolicy.IF_LOCAL_IMAGE_ABSENT where the image would not be pulled even when it was not present locally
### Changed
- Added exceptions when invocation of external commands (docker / docker-compose) return non-zero exit codes
- It is now possible to pull images from extensions (without requiring a reference to the container orchestration runtime)

## [1.5.4] - 2017-12-13
### Fixed
- No longer ignore -Dservice=... in zet:clean goal of the maven plugin
- Fixed Windows compatibility issue
- Fixed Error message describing detected cycles
- Fixed an error when downloading the top level pom file from a remote maven repo (the bug was introduced in 1.5.0)

## [1.5.3] - 2017-12-07
### Changed
- Added exit-code check on internal maven invocations
- Added check for uniqueness of artifactIds in environments
- Improved error message when dependency cycles are detected
### Fixed
- Windows compatibility fixes
## [1.5.2] - 2017-12-07
### fixed
- Fix IOException when merging into non-existing file
## [1.5.1] - 2017-12-07
### Changed
- Add option to degrade the correctness of configuration overrides in cases of dependency cycles instead of failing. The default behavior remains unchanged (fail).
- Configuration in .merge files is not ignored when there is no target file to merge with.
- Add support for `start.by.default=false` in carnotzet.properties to support optional services.
## [1.5.0] - 2017-11-18
### Added
- Classifiers can now be used instead of suffixes in the artifactId to define carnotzet maven artifacts
### Fixed
- Use resources in jar files in all cases (even when top level module resources path is provided)
## [1.4.0] - 2017-11-03
### Added
- Support for image pulling policies in docker-compose runtime
## [1.3.0] - 2017-10-23
### Added
- Support for maven version placeholders in docker.image versions. Example : docker.image=my-image:${my-module.version}
- Support for extra entries in /etc/hosts in containers, use the extra.hosts property in carnotzet.properties
### Fixed
- Ignore MAVEN_DEBUG_OPTS when invoking maven to resolve dependencies, this allows to debug (with suspend=y) 
without the sub-maven process also suspending.
- Custom network aliases are now properly exported as dnsdock aliases.
- Improved the error message in case of cycles in the maven dependency graph
## [1.2.3] - 2017-09-21
### Fixed
- Detect cycles in maven dependency graphs to avoid StackOverflowErrors in case of cycles.
### Changed
- Ignore all artifacts imported with scopes other than compile and runtime.
- Allow injection/override of arbitrary files and folders in module resources by default.
## [1.2.2] - 2017-08-25
### Fixed
- Fixed a bug where configuration file overrides were not applied properly in some cases
### Changed
- Changed maven resolver from shrinkwrap to maven invoker + maven-dependency-plugin
- The core library now depends on having a functional maven installation in the environment (M2_HOME or ${maven.home} must be set)
## [1.2.1] - 2017-08-03
### Fixed
- Fixed "welcome" page generation regression introduced in 1.2.0
## [1.2.0] - 2017-07-17
### Added
- Possibility to configure docker.entrypoint and docker.cmd in carnotzet.properties
- Possibility to override cartnozet.properties of downstream dependencies.
### Changed
- Removed support for pattern `{service_name}.network.aliases=...` in carnotzet.properties. Use the new carnotzet.properties override/merge feature instead with the `network.aliases` key.
### Fixed
- Maven dependencies are now better aligned. Fixes issues with aether-utils backwards compatibility experienced by some uers.. Minimal compatible version of Maven is now 3.2.5 
### Internal
- Refactored internal resource file structure to keep both expanded-jars and resolved resources to simplify testing and debugging
- Added maven wrapper to fix maven version issues in travis-ci
## [1.1.0] - 2017-06-09
### Changed
- Hostname pattern to establish network connections with containers changed from `(container_name.)image_name.docker` to `(instance_id.)module_name.docker`
### Added
- Possibility to configure extensions in pom.xml files
### Fixed
- Prevent NPE when programmatically adding modules that have no labels or properties
- e2e tests randomly freezing
## [1.0.0] - 2017-05-04

Intitial release. 
