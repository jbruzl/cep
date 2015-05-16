# README #

## Run from jar ##
Run command:
java -Dfile.encoding=UTF-8 -jar web-1.0.0-RELEASE.jar

## Run from source ##
Build project first, then in web:
mvn spring-boot:run

## BUILD ORDER ##
api module
core module
activiti modules...
web module

On whole project:
mvn install
mvn package
run web/target/web-1.0.0-RELEASE.jar:
java -Dfile.encoding=UTF-8 -jar web-1.0.0-RELEASE.jar

## Radiobroadcaster ##
Radiobroadcaster app is not part of master thesis and isn't necessary, it serves only for demonstration, as radio broadcasting service / siren service stub.
For full functionality run app as mvn spring-boot:run. Application will start on port 8080 (if not changed).

Application consist of one page with received radio messages. 

## First use ##
To gain full access to implemented functionality user has to have account with filled his phone number. Also, account has to be member of mayor group to gain access to processes.

After first start, administrator account is created. His credentials:
Login: admin@admin.cz
Passw: 1234
He is member od administrator group, so he can manage memberships. Use his account to assign your account to mayor group. After logout and log in as with your account, you will have full access to processes.

### Subscriptions ###
To receive any informations, you need to subscribe to events. You do so in "Moje odb�ry" tab. 
To experience whole funkcionality of application, subscribe to all events.