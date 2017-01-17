# sbt在不同版本系统下的安装

# Red Hat Enterprise Linux and other RPM-based distributions
```
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum install sbt

```

# Ubuntu and other Debian-based distributions
```
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
```

Ref:
http://www.scala-sbt.org/0.13/docs/Setup.html