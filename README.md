# README #

Run command:
java -Dfile.encoding=UTF-8 -jar web-1.0.0-RELEASE.jar (or run run.bat)
Note: jar can't be on file path with special characters, if so it won't parse processes. It's caused by Activiti parse component.

If you encounter any problems with jar, try run it from IDE as Spring Boot App.

## BUILD ORDER ##
api module
core module
activiti modules...
web module

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
To receive any informations, you need to subscribe to events. You do so in "Moje odbìry" tab. 
To experience whole funkcionality of application, subscribe to all events.