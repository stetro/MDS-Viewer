MDS-Viewer
==========

webapplication to browse metadata services (MDS) in fusion middleware 11g

Screenshots
-----------
![Folder View](https://raw.github.com/stetro/MDS-Viewer/master/img/folder.PNG)
![File View](https://raw.github.com/stetro/MDS-Viewer/master/img/file.PNG)
![File View](https://raw.github.com/stetro/MDS-Viewer/master/img/login.PNG)

Installation
------------
To use this project you have to insert the following library's into  ``/lib``.
You can find them in your middleware installation.

* nxsdlibs/
    * xmlparserv2.jar ``Middleware\oracle_common\modules\oracle.xdk_11.1.0\xmlparserv2.jar``
* mdslibs/
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

You also have to change the connection properties in ``mds_db.properties``:

```
mds.user=
mds.password=
mds.jdbc=
mds.schema=
```

Basic Login is weblogic/welcome1 (change in ``src/main/webapp/WEB-INF/security-app-context.xml``)

License
---------------------
GNU LESSER GENERAL PUBLIC LICENSE Version 3, 29 June 2007