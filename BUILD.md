Directory structure
-------------------
  
${ROOT}
  
    + adaptor-dev        (a sample project for adaptor development)    
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
	  + log				(log files)

Overview
--------

COSBench is developed with Java* language, it is a modular system based on OSGi* framework, and includes a few OSGi 
plugin projects under "dev" folder.


Development Environment
-----------------------

Below are steps to set up development environment in eclipse:
1. download eclipse SDK (Juno) from http://www.eclipse.org/downloads/

2. get cosbench source code tree by git or downloading the whole zip package.

3. in eclipse,  "File -> Import ... -> Existeing Projects into Workspace", and select the root directory to the "dev" 
folder in cosbench, then eclipse will recognize and import all plugin projects.

4. after imported, there will be error signs on projects, additional library path configuration should take to resolve 
them. "Window -> Preferences -> Plug-in Development -> Target Platform", in "Target definitions", choose the active one,
normally, it's "Running Platform". Selecting "Edit..." button to add required plugins. 

5. In COSBench, 3 folders under "dist" folder for plugins should be included: "main", "osgi" and "osgi\libs". After added those folders, then apply changes.

6. Those error signs should disappear, then the development environment is ready.


== END ==
