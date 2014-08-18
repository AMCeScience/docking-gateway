Docking Gateway Portlet
============================

Introduction
------------
The Docking Gateway is a Liferay portlet aimed to work together with gUSE/WSPGRADE. It provides access to distributed computing resources (grid, PBS, SGE, ...) to biochemists working on molecular docking.

Requirements
------------
* Liferay 6.1.0
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

WebDAV and Liferay Authentication
---------------------------------
For a local deployment where the WebDAV repository and Liferay instance run on the same machine, WebDAV relies on the Liferay user credentials for athentication. Also, two different WebDAV context are provided:
* a publicly available  WebDAV context, the one users can access from their browsers
* a private one only reachable from localhost for internal communication between the portlet and WebDAV repository

The proposed authentiation scheme is based on `mod_auth_mysql` and Apache >=2.2 server. The Apache configuration is as follows:
```
# public WebDAV context
Alias /webdav "path/repository"
# private localhost-only WebDAV context
Alias /webdav-curl "path/repository"

# WebDAV configuration
<Location /webdav>
        DAV On
        AuthBasicAuthoritative Off
        AuthUserFile /dev/null
        AuthMySQLEnable On
        AuthName "webdav"
        AuthType Basic
        AuthMySQLHost localhost
        AuthMySQLUser liferay
        AuthMySQLPassword liferay.password
        AuthMySQLDB liferay
        AuthMySQLUserTable User_
        AuthMySQLNameField emailAddress
        AuthMySQLPasswordField password_
        AuthMySQLNoPasswd Off
        AuthMySQLPwEncryption sha1
        AuthMySQLAuthoritative On
        require valid-user
</Location>


# WebDAV configuration, local context
<Location /webdav>
        DAV On
        AuthBasicAuthoritative Off
        AuthUserFile /dev/null
        AuthMySQLEnable On
        AuthName "webdav"
        AuthType Basic
        AuthMySQLHost localhost
        AuthMySQLUser liferay
        AuthMySQLPassword liferay.password
        AuthMySQLDB liferay
        AuthMySQLUserTable User_
        AuthMySQLNameField emailAddress
        AuthMySQLPasswordField password_
        AuthMySQLNoPasswd Off
        AuthMySQLPwEncryption sha1
        AuthMySQLAuthoritative On
        require valid-user
</Location>
```
To make possible for `mod_auth_mysql` module to authenticate against Liferay User database, it is required to change the hasshing algorithm and encoding used by default by Liferay. The above authentication scheme would require the following options on `$HOMELIFERAY/porta-ext.properties`:
```
passwords.encryption.algorithm=SHA
passwords.digest.encoding=hex
```

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
