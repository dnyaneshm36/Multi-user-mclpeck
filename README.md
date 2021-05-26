# Multi-user certificateless public key encryption with conjunctive keyword search for cloud-based telemedicine.

Welcome to the Multi-user certificateless public key encryption with conjunctive keyword search, implemented using Java. 

Here is a guideline to help you understand code.

# Folder Structure

The workspace contains two folders, where:

- `src`: the folder to maintain java sources.
- `lib`: the folder contains jpbc library jar files. [Download jpbc. ](http://gas.dia.unisa.it/projects/jpbc/download.html#.YGVrEXUzZEY)


# Requirements
### Install JRE.
        sudo apt install default-jre 
### Verify the installation.
        java -version
### Install JDK.
        sudo apt install default-jdk
### Verify the installation.
        javac -version
### Setting the JAVA_HOME Environment Variable.
#### Open editer by cmd.
        sudo gedit /etc/environment
#### Copy paste the path.       
        JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
#### Reloading changes.
        source /etc/environment
#### Verify that the environment variable is set.
        echo $JAVA_HOME
for infromation check. [more](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-18-04)



## Java Vscode

The `JAVA DEPENDENCIES` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-pack/blob/master/release-notes/v0.9.0.md#work-with-jar-files-directly).