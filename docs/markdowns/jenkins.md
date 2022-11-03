docker run -u root --rm -d -p 8081:8080 -p 50000:50000 -v /boozdata/pods/boozsoft-jenkins/data/jenkins-data:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkinsci/blueocean




docker run -d  --name jk -u root -p 9090:8080  jenkinsci/blueocean