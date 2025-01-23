# JIRSend
<div style="text-align:center">
<img src="JIRSendApp/assets/jirsend_logo.png" width="100"><br>
</div>

### Tech Stack

- UDP: for broadcast -> mandatory to communicate with unknown users (contact discovery)
- TCP: for all point-to-point communications -> Reliability
- sqlite: to create local file-based database
- mysql: to store contact and message history between sessions -> efficient queries and storage
- Java Clip: for notification sounds -> efficient for short sounds
- JUnit: easy way to create and automate unit tests
- Java Swing:  for the GUI -> works everywhere (except Wayland)
- JLine: CLI -> allows autocompletion and histroy in the CLI
- JIRSendAPI: to load and run JIRSend mods

### Testing policy

In addition to unit tests, we tested before most commits by launching the project on two (or more depending on tested functionnality) computer using ssh.\
However, some commits were made to synchronized the work between INSA's computers and ours. And those commits were not tested resulting sometimes in GitHub Action failure.

### Highlights

- *JIRSendApp.controller.Pipe*: this class, inspired by M. Le Botlan last CM, implements the Observer Design Pattern. It is heavily used by a lot a classes to subscribe to events such as new message, new contact, message sent, ...
- *JIRSendApp.view.cli.Log*: this static class was created for logs before we were told about Log4J. We think that is fits better than Log4J for our project.
- *JIRSendAPI.JIRSendMod*: this abstract class is used to implement mods for JIRSend: Micasend4JS allows to communicate with the internet (using [Micasend](https://micasend.magictintin.fr), an evergrowing joke project created 2 years ago), Clavardons4JS allows to communicate with Clavardons (chatsystem of Jean-Philippe L. and Aurelien A.), and more mods to communicate with other teams are expected to come!

### For more infomation, look at the project's [README](./README.md)
