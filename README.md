twinpush-mock
=============

Mock for Twinpush Rest Services

Run server forever:
-------------------
mvn exec:java -Dexec.mainClass="org.test.twinpushmock.ServerRunner"

mvn exec:java

Request:
--------
curl -X POST  -H "X-TwinPush-REST-API-Token: APITOKEN"  -H "Content-Type: application/json"  -d "{ 'notification': {'alert': 'Mensaje a enviar.','devices_ids': ['2b3c4d5f6g']}}" http://localhost:8082/api/v2/apps/app1/notifications

Generate a new jersey project:
------------------------------
mvn archetype:generate -DarchetypeArtifactId=jersey-quickstart-grizzly2 -DarchetypeGroupId=org.glassfish.jersey.archetypes -DinteractiveMode=false -DgroupId=com.example -DartifactId=simple-service -Dpackage=com.example -DarchetypeVersion=2.13

Apache Https Configuration:
---------------------------
Listen 80

ProxyPass / http://localhost:8082/
ProxyPassReverse / http://localhost:8082/

Listen 443

\<VirtualHost *:443\>

SSLEngine on

SSLCertificateFile c:\data\programs\Apache24\conf\ssl\server.crt

SSLCertificateKeyFile c:\data\programs\Apache24\conf\ssl\server.key

\<\/VirtualHost\>

