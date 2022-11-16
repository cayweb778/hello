<template></template>
<script></script>
tar -zxvf /boozdata/dist/nc/jar/dist.tar.gz -C  /boozdata/dist/nc/jar/
podman stop  boozsoft-service-nc-system
podman stop   boozsoft-service-nc-gateway
podman stop   boozsoft-service-nc-gdzc
podman stop   boozsoft-service-nc-stock
podman stop    boozsoft-service-nc-zongzhang
podman stop    boozsoft-service-nc-auth


rm -rf /boozdata/pods/boozsoft-nc-server/auth/abc.jar
rm -rf /boozdata/pods/boozsoft-nc-server/gateway/abc.jar
rm -rf /boozdata/pods/boozsoft-nc-server/stock/abc.jar
rm -rf /boozdata/pods/boozsoft-nc-server/system/abc.jar
rm -rf /boozdata/pods/boozsoft-nc-server/zongzhang/abc.jar
rm -rf /boozdata/pods/boozsoft-nc-server/gdzc/abc.jar

mv /boozdata/dist/nc/jar/packages/dist/boozsoft-auth-0.0.1-SNAPSHOT.jar /boozdata/pods/boozsoft-nc-server/auth/abc.jar
mv /boozdata/dist/nc/jar/packages/dist/boozsoft-gateway-0.0.1-SNAPSHOT.jar /boozdata/pods/boozsoft-nc-server/gateway/abc.jar
mv /boozdata/dist/nc/jar/packages/dist/boozsoft-platform-gdzc-0.0.1-SNAPSHOT.jar  /boozdata/pods/boozsoft-nc-server/gdzc/abc.jar
mv /boozdata/dist/nc/jar/packages/dist/boozsoft-platform-system-0.0.1-SNAPSHOT.jar /boozdata/pods/boozsoft-nc-server/system/abc.jar
mv /boozdata/dist/nc/jar/packages/dist/boozsoft-platform-stock-0.0.1-SNAPSHOT.jar  /boozdata/pods/boozsoft-nc-server/stock/abc.jar
mv /boozdata/dist/nc/jar/packages/dist/ boozsoft-platform-zhongzhang-0.0.1-SNAPSHOT.jar /boozdata/pods/boozsoft-nc-server/zongzhang/abc.jar

podman start  boozsoft-service-nc-system
podman  start  boozsoft-service-nc-gateway
podman  start  boozsoft-service-nc-gdzc
podman  start  boozsoft-service-nc-stock
podman  start   boozsoft-service-nc-zongzhang
podman  start   boozsoft-service-nc-auth
