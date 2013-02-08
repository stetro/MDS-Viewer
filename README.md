MDS-Viewer
==========

webapplication to browse metadata services (MDS) in fusion middleware 11g

Screenshots
-----------
![Folder View](https://raw.github.com/stetro/MDS-Viewer/master/img/folder.PNG)
![File View](https://raw.github.com/stetro/MDS-Viewer/master/img/file.PNG)
![Login](https://raw.github.com/stetro/MDS-Viewer/master/img/login.PNG)
![Diff Example](https://raw.github.com/stetro/MDS-Viewer/master/img/diffexample.PNG)

Installation
------------
1. To use this project you have to insert the following library's into  ``/lib`` as local maven repository. You can find them in your middleware installation.
    * xmlparserv2.jar ``Middleware\oracle_common\modules\oracle.xdk_11.1.0\xmlparserv2.jar``
    * adf-share-base.jar ``Middleware\oracle_common\modules\oracle.adf.share.ca_11.1.1\adf-share-base.jar``
    * adflogginghandler.jar ``Middleware\oracle_common\modules\oracle.adf.share_11.1.1\adflogginghandler.jar``
    * cache.jar ``Middleware\oracle_common\modules\oracle.javacache_11.1.1\cache.jar``
    * dms.jar ``Middleware\oracle_common\modules\oracle.dms_11.1.1\dms.jar``
    * mdsrt.jar ``Middleware\oracle_common\modules\oracle.mds_11.1.1\mdsrt.jar``
    * ojdbc6.jar ``Middleware\wlserver_10.3\server\adr\ojdbc6.jar``
    * ojdl.jar ``Middleware\oracle_common\modules\oracle.odl_11.1.1\ojdl.jar``
    * oracle.ucp_11.1.0.jar ``Middleware\oracle_common\modules\oracle.ucp_11.1.0.jar``
    * share.jar ``Middleware\oracle_common\modules\oracle.bali.share_11.1.1\share.jar``
    * wsm-pmlib.jar ``Middleware\oracle_common\modules\oracle.wsm.common_11.1.1\wsm-pmlib.jar``
    * wsm-policy-core.jar ``Middleware\oracle_common\modules\oracle.wsm.common_11.1.1\wsm-policy-core.jar``
    * xmlef.jar ``Middleware\oracle_common\modules\oracle.xmlef_11.1.1\xmlef.jar``

2. Use the following command to build your own local maven repository for each file:

    ```
    mvn install:install-file
        -Dfile=tmp/mdslibs/filename.jar
        -DartifactId=filename
        -DgroupId=com.oracle
        -Dversion=1.0
        -Dpackaging=jar
        -DgeneratePom=true
        -DcreateChecksum=true
        -DlocalRepositoryPath=lib
    ```
    Fix the dependencies in ``/web/pom.xml`` if necessary.

3. You also have to change the connection properties in ``mds_db.properties``:
    ```
    mds.user=
    mds.password=
    mds.jdbc=
    mds.schema=
    ```

4. Basic Login for local deployment is weblogic/welcome1 (change in ``/misc/jetty/realm.properties``).
    On WLS, the Administrator group is used.

License
---------------------
GNU LESSER GENERAL PUBLIC LICENSE Version 3, 29 June 2007