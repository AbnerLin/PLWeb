# PLWeb
Web Server: Tomcat 6 or latest version  
Database: Mysql 5.x or latest version  

## Tomcat  
設定檔位置(預設): /var/lib/tomcat/conf/server.xml
資料庫連接設定: /var/lib/tomcat/conf/Catalina/localhost/ROOT.xml

* 先配置server.xml，以下為範例，還須依照環境需求修改。
```
<Service name="Catalina">
    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443"  
      enableLookups="false" URIEncoding="UTF-8" connectionTimeout="60000"  
      acceptCount="1000" maxThreads="1000" />
    <Engine name="Catalina" defaultHost="localhost">
        <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
             resourceName="UserDatabase"/>
        <Host name="localhost"  appBase="webapps"  
          unpackWARs="true" autoDeploy="true"  
          xmlValidation="false" xmlNamespaceAware="false">
        </Host>
    </Engine>
</Service>
```
## MySQL
依照需求請自建MySQL的帳號與密碼，詳細請參考[MySQL Document](http://dev.mysql.com/doc/)，帳號建立完成後，請建立資料庫，名為PLWeb，之後將[Schema](https://github.com/AbnerLin/PLWeb/blob/master/plweb.sql)匯入。

##Deploy
Tomcat與MySQL都設定完成後，將[ROOT.war](https://github.com/AbnerLin/PLWeb/blob/master/ROOT.war)複製至Tomcat的網頁資料夾處(預設為/var/lib/tomcat/webapps/)，並修改tomcat的資料庫設定檔(預設為/var/lib/tomcat/conf/Catalina/localhost/ROOT.xml)，都設定完成後，重啟Tomcat，即完成。

* ROOT.xml範例，還須依照環境需求修改。
```
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource
      name="jdbc/plweb"
      auth="Container"
      type="javax.sql.DataSource"
      username="UserName"
      password="PassWord"
      driverClassName="com.mysql.jdbc.Driver"
      url="jdbc:mysql://localhost/plweb?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true&amp;autoReconnectForPools=true"
      testOnBorrow="true"
      testOnReturn="true"
      validationQuery="SELECT 1"
      maxActive="200"
      maxIdle="100"
      maxWait="-1"
      initialSize="10"
      minEvictableIdleTimeMillis="600000"
      timeBetweenEvictionRunsMillis="600000"
      removeAbandoned="true"
      removeAbandonedTimeout="60"
      logAbandoned="true"
    />
</Context>
```
