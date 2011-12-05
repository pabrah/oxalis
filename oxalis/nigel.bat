
----------------------------------------------------------------------------
Demo
----------------------------------------------------------------------------


cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis
mvn install
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-standalone
mvn -Dmaven.test.skip=true assembly:assembly
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-standalone/target

java -jar oxalis.jar -k /usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks -d /Users/nigel/Filer/mazeppa/SendRegning/doc/EHF-faktura.xml -r 9908:983974724 -s 9909:976098897 -p=peppol

java -jar oxalis.jar -k /usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks -d /Users/nigel/Filer/mazeppa/SendRegning/doc/EHF-faktura.xml -r 0088:7314455667781 -s 9909:976098897 -p=peppol

java -jar oxalis.jar -k /usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks -d /Users/nigel/Filer/mazeppa/SendRegning/doc/EHF-faktura.xml -r 9909:976098897 -s 9909:976098897 -p=peppol -u https://10.0.0.87:8443/oxalis/accesspointService


git clone https://github.com/SendRegning/sr-peppol-binary.git


----------------------------------------------------------------------------
Normal cycle
----------------------------------------------------------------------------

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis
mvn install
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-start-inbound
mvn package -Dmaven.test.skip cargo:deployer-undeploy cargo:deployer-deploy
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-standalone
mvn -Dmaven.test.skip=true assembly:assembly
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-standalone/target
java -jar oxalis.jar -k /usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks -d /Users/nigel/Filer/mazeppa/SendRegning/doc/EHF-faktura.xml -r 9908:976098897 -s 9908:976098897 -p=peppol -u https://localhost:8443/oxalis/accesspointService

java -jar oxalis.jar -k /usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks -d /Users/nigel/Filer/mazeppa/SendRegning/doc/EHF-faktura.xml -r 9908:976098897 -s 9908:976098897 -p=peppol -u https://192.168.1.100:8443/oxalis/accesspointService

----------------------------------------------------------------------------
Clean cycle
----------------------------------------------------------------------------

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis
mvn clean install
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-start-inbound
mvn package -Dmaven.test.skip cargo:deployer-undeploy cargo:deployer-deploy
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-standalone
mvn -Dmaven.test.skip assembly:assembly
cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-standalone/target
java -jar oxalis-standalone.jar -k /usr/local/apache-tomcat-7.0.21/conf/keystore/keystore.jks -d /Users/nigel/Filer/mazeppa/SendRegning/doc/EHF-faktura.xml -r 9908:976098897 -s 9908:976098897 -p=peppol -u https://localhost:8443/oxalis/accesspointService


----------------------------------------------------------------------------
Logs
----------------------------------------------------------------------------

cd /usr/local/apache-tomcat-7.0.21/logs
tail -f -n 300 oxalis.log

rm /usr/local/apache-tomcat-7.0.21/logs/oxalis.log

----------------------------------------------------------------------------
Git
----------------------------------------------------------------------------

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis
git status
git log
git mv oxalis-start-server oxalis-start-inbound
git commit -a -m 'Rename modules'
git push origin master

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol
git blame README.md


----------------------------------------------------------------------------
Testing
----------------------------------------------------------------------------

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-start-outbound
mvn test

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis/oxalis-start-inbound
mvn test


----------------------------------------------------------------------------
Tomcat
----------------------------------------------------------------------------

cd /usr/local/apache-tomcat-7.0.21/bin
/usr/local/apache-tomcat-7.0.21/bin/shutdown.sh
cd /usr/local/apache-tomcat-7.0.21/logs
tail -f -n 300 catalina.out

rm /usr/local/apache-tomcat-7.0.21/logs/catalina.out
cd /usr/local/apache-tomcat-7.0.21/bin
/usr/local/apache-tomcat-7.0.21/bin/startup.sh
cd /usr/local/apache-tomcat-7.0.21/logs
tail -f -n 300 catalina.out


----------------------------------------------------------------------------
Diverse
----------------------------------------------------------------------------

./fetch-metadata.sh 9908:983974724
./fetch-metadata.sh 9902:DK28158815

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis
mvn dependency:tree

cd /Users/nigel/Filer/mazeppa/SendRegning/sr-peppol/oxalis
find . -name .svn
find . -name .svn |xargs rm -rf

