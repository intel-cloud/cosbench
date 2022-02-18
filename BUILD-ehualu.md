Overview
--------

The following procedure indicates how to compile and package cosbench for Ehualu. 
First, please read [BUILD.md](BUILD.md).


Development Environment
-----------------------

To get a running comilation environment, please refer to the same section in [BUILD.md#development-environment](BUILD.md#development-environment).

Note that Eclipse **4.7.3a** is a knwon working release. You must use **JDK 1.8** and configure eclipse this way:

1. in `Window -> Preferences -> Java -> Prefered JRE` ensure a JDK 1.8 is selected.

2. in `Window -> Preferences -> Java -> Compiler` ensure `Compiler compliance level` is set to **1.8**.

Releasing new version
---------------------

When you are ready to release a new version:

1. Update the file `VERSION`.

2. Update the version in `dev/*/META-INF/MANIFEST.MF` by running the script `./version_unify.sh`.

3. Remove previous JAR: `rm dist/osgi/plugins/cosbench-*.jar`.

4. In eclipe, select all project, right click then `export ... -> Plug-in Development -> Deployable plugins and fragments`.

5. Run `./pack-ehualu.sh` to generate `cosbench-$VERSION-ehualu.tar.gz`.

6. Commit changes, tag to `${VERSION}-ehualu` and push to git (tagging can be done when making a new release from the github page).

7. Create a release from [github page](https://github.com/open-io/cosbench/releases).
