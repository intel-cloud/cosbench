Directory structure
-------------------
  
${ROOT}
  
    + ext		      	(sample projects for adaptor development)    
    + dev				(all cosbench plugin projects)
    + dist				(all libraries)
		+ main			(the osgi launcher)
		+ osgi			(all osgi bundles including osgi framework) 
			+ libs		(those from third-parties)
			+ plugins	(those from cosbench itself)			
    + version			(major binary versions)
		+ 
    + release			(supporting files for release)
      + conf			(system and workload configuration files)
      + javadoc		    (java doc files)
      + lib-src		    (the corresponding source code for CDDL libraries)
      + licenses		(licenses for third-party libraries)

Overview
--------

COSBench is developed with Java* language, it is a modular system based on OSGi* framework, and includes a few OSGi 
plugin projects under "dev" folder.


Development Environment
-----------------------

Below are steps to set up development environment in eclipse:

1. download eclipse SDK from [http://download.eclipse.org/eclipse/downloads/](http://download.eclipse.org/eclipse/downloads/), and we are using the version 4.2.1 (Juno).

2. get cosbench source code tree by git or downloading the whole zip package.

3. in eclipse,  "File -> Import ... -> Existeing Projects into Workspace", and select the root directory to the "dev" 
folder in cosbench, then eclipse will recognize and import all plugin projects.

4. after imported, there will be error signs on projects, additional library path configuration should take to resolve 
them. "Window -> Preferences -> Plug-in Development -> Target Platform", in "Target definitions", choose the active one,
normally, it's "Running Platform". Selecting "Edit..." button to add required plugins. 

5. In COSBench, 3 folders under "dist" folder for plugins should be included: **main**, **osgi** and **osgi\libs**. After added those folders, then apply changes.

6. Those error signs should disappear, then the development environment is ready.

7. After applied modifications on one project, just generate the plugins by right clicking the project, and select "export... -> Plug-in Development -> Deployable plugins and fragments", 
and set the "Directory" to "dist\osgi" folder. Then the plugins library will be placed at "dist\osgi\plugins" folder.

8. One script called "pack.cmd" or "pack.sh" could help generate one delivable package by passing it the version number.  


Debugging Environment
---------------------

COSBench includes a few OSGi bundle projects, and there are two major executables, one is COSBench Controller, another is COSBench Driver.
To debug both executables, a few special settings are required.

1. COSBench Controller

	1.1 The major bundle project is "cosbench-web-controller".
	
	1.2 Right click on the project, and select "Run As"\"Run Configuration...".
	
	1.3 In "Run Configuration" window, right click on "OSGi Framework" and select "New" to create an new configuration with name "controller".
	
	1.4 In "Bundles" tab, there are a few parameters can set, the settings depend on the file "release\conf\.controller\config.ini" in github repository.
	
	1.5 At the header, make changes as following:
		"Default Start level:" = 8
		"Default Auto-Start:" = true
		
	1.6 At the bundle table, for each bundle, there are two parameters: "Start Level" and "Auto-Start". The information can get from "osgi.bundles" parameter in config.ini.
		e.g.
		"libs/com.springsource.freemarker-2.3.20.jar@2\:start" means the freemarker bundle will be with "Start Level" = 2 and "Auto-Start" = true.
		
	1.7 Some bundles may not set the two parameters, like "com.springsource.apache.coyote". in this case, just let it be. 
	Ensure below two system bundles are checked: **org.eclipse.equinox.launcher_1.2.0.v20110502.jar**, **org.eclipse.osgi-3.7.0.v20110613.jar**
	
	1.8 After configured bundle part, the next step is to configure "Arguments" tab, here is the settings:
		
		> program arguments: -os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog -console
		> vm arguments: -Xms40m -Xmx512m -Declipse.ignoreApp=true -Dosgi.noShutdown=true -Dosgi.startLevel=8 -Dcosbench.tomcat.config=./conf/controller-tomcat-server.xml
		> working directory: ${ROOT}/release
		
	1.9 There are two parameter pairs are added beside default, one is **osgi.startLevel** which tells framework the start level, another is **cosbench.tomcat.config** which indicates where to find the tomcat configuration file.
		
	1.10 Making one sub folder called "plugins" in "dist\osgi" folder, and copy **org.eclipse.osgi-3.7.0.v20110613.jar** into "plugins" folder, then refresh all bundle projects, now there should no error marks on each project.
		
	1.11 Running the project should see messages as below on eclipse console window:
	
		osgi> Persistence bundle starting...
		Persistence bundle started.
		----------------------------------------------
		!!! Service will listen on web port: 19088 !!!
		----------------------------------------------
	
2. COSBench Driver

	1.1 The major bundle project is "cosbench-driver-web". 
	
	1.2 The configuration is similar to that for COSBench Controller, and the bundle dependency information can be extracted from "release\conf\.driver\config.ini".



== END ==
