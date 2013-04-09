Overview
========
COSBench is developed with Java* language, it is a modular system based on OSGi* framework, and includes a few OSGi plugin projects 
under "dev" folder.


Directory structure
-------------------
#  + adaptor-dev  	# a sample project for adaptor development
#	+ dev				# all cosbench plugin projects
#	+ dist				# all library
#	+ version			# major binary versions
#	+ release			# supporting files for release
#		+ conf			# system and workload configuration files
#		+ javadoc		# java doc files
#		+ lib-src		# the corresponding source code for CDDL libraries
#		+ licenses		# licenses for third-party libraries


COSBench is developed with Java* language, it is a modular system based on OSGi* framework, and includes a few OSGi plugin projects 
under "dev" folder. Below are steps to set up development environment in eclipse:
i) download eclipse SDK (Juno) from http://www.eclipse.org/downloads/
ii) get cosbench source code tree by git or downloading the whole zip package.
iii) in eclipse,  "File -> Import ... -> Existeing Projects into Workspace", and select the root directory to the "dev" folder in cosbench, then eclipse will recognize and import all plugin projects.
iv) after imported, there will be error signs on projects, additional library path configuration should take to resolve them. "Window -> Preferences -> Plug-in Development -> Target Platform", in "Target definitions", choose the active one, normally, it's "Running Platform". Selecting "Edit..." button to add required plugins. In COSBench, 3 folders under "dist" folder for plugins should be included: "main", "osgi" and "osgi\libs".
v) 

....

