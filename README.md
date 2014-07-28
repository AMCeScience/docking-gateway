Docking Gateway Portlet
============================

Introduction
------------
The Docking Gateway is a Liferay portlet aimed to work together with gUSE/WSPGRADE. It provides access to distributed computing resources (grid, PBS, SGE, ...) to biochemists working on molecular docking.

Requirements
------------
* gUSE/WSPGRADE 3.6.1 or later
* UMD2/3 middleware: gLite, MyProxy, lcg_utils, ...
* XNAT 1.6
* MySQL 5.1
* cURL

Database creation
-----------------
The portlet requires a user and scheme on a MySQL server.

Building .war file
------------------
1. Get the latest source code 
`git clone https://github.com/AMCeScience/docking-gateway.git docking`
2. Generate docking.war file using Maven
```
cd docking
mvn -s ./ebioinfra.settings.xml package
```
3. The resulting `autodock.war` file can be found on `docking/target`

Deploying the portlet
---------------------
1. Log in Liferay using and admin/privileged account
2. Go to Control Panel / Plugin Installation / Install more portlets / Upload file
3. Upload the `autodock.war` file
4. Restart Liferay
NOTE: don't forget to deploy the appropriate version of the processing manager portlet.

Configuring Liferay
-------------------
1. Using a Liferay admin account and the control panel, create a `DockingAdmin` role
2. Choose one of the Liferay sites and create a new page
3. Insert a `autodock` porlet in the new page

Further configuration
---------------------
1. Create a folder inside the webapp directory of apache the default name is `autodock_files`
2. Drop the `config.json` and the ligand libraries in this folder
3. The structure for the ligand library folder is `/autodock_files/ligands/[library name]/[ligand files]`


Further documentation and contact info
--------------------------------------
Official Docking Gateway documentation
https://docking.ebioscience.amc.nl/portal/documentation

For further information and details, contac us at: 
`support-nsg[at]ebioscience[dot]amc[dot]nl`
