# Table of contents

- [Setting the server stack](#setting-the-server-stack)
  - [Neo4j](#neo4j)
  - [Play Activator](#play-and-activator)
  - [Project and IntelliJ](#project-and-intellij)
  - [Launching the server instance](#launching-the-server-instance)

- [Feature branch workflow](#feature-branch-workflow)
  - [You are creating the branch and starting the feature implementation](#you-are-creating-the-branch-and-starting-the-feature-implementation)
  - [You are helping your colleague implementing a feature](#you-are-helping-your-colleague-implementing-a-feature)


# Setting the server stack

## Neo4j

  - Download the [Neo4j 2.3.2 installer](https://feupload.fe.up.pt/get/NUxXqQxnf9qUpsh) and install it.
  
  - Execute the Neo4j DB exe and before you press start go to the Options...->Server Configuration->Edit...
  
  - Make sure this lines are present and with the same values (you may need to remove the comments in the file).
    -  `dbms.security.auth_enabled=true`
    -  `org.neo4j.server.webserver.port=7474`
    -  `org.neo4j.server.webserver.https.enabled=true`
    -  `org.neo4j.server.webserver.https.port=7473`
  
  - Save and close the file, close the options window and press the Start button.
  
  - Try to access the DB instance by going to [http://localhost:7474/browser/](http://localhost:7474/browser/).
    -  The first time you try to access it, you'll be asked for credentials. Use `neo4j` for both username and password.
    -  Next, you'll be prompted for a new password. use `neo` as your new password.
    -  Contact the Support Manager if you did not complete this task successfully.
    
  **You can now harvest the power of a graph oriented DBMS.**

## Play and Activator
  
  - Make sure you have JAVA 1.8 installed.
    - check with `java --version` command.
  - Download the [Activator Zip file](https://feupload.fe.up.pt/get/RonNYEBeo8YoNte).
  - Extract to any directory (with writing permission if you are using unix).
  - Add `C:\path\to\activator-x.x.x\bin` to your PATH enviroment variable.
    - if you are using win8+ you can the `setx PATH=%PATH%;"C:\path\to\activator-x.x.x\bin"` command.
    - linux users can use `export PATH=/path/to/activator-x.x.x:$PATH` and `chmod u+x /path/to/activator-x.x.x/activator` after.
  - Test if you did the previous steps right by using `activator -help` in a command prompt window/terminal
  
  **Congrats. The Framework is set up. You may fire your web framework machinegun.**
  
## Project and IntelliJ
  - Clone this repository: `git clone https://github.com/msandim/feup-lgp.git`
  - Open your IntelliJ IDE and import a new project.
    - Make sure you imported the play directory (the `ISG` folder, located inside the server folder).
  - It will ask you what external model you are using in the project. Pick `SBT`. In the next window, check the Auto-Import option.
  
  **You can now edit this beauty. Almost finished!**
  
## Launching the server instance
  - In your command prompt, go inside the `ISG` folder.
  - Run the command `activator run` and wait until the console tells you that the server started.
  - Go to [http://localhost:9000/](http://localhost:9000/). Don't panic, it may take some time to start, the class are being compiled.
  
  **YOU FINISHED**
  
  Now try some cool stuff to test the whole stack
   - [http://localhost:9000/allQuestions](http://localhost:9000/allQuestions) to get all the question nodes.
   - [http://localhost:9000/addQuestion/:text](http://localhost:9000/addQuestion/:text) change :text to you question text.
   - [http://localhost:9000/question/:id](http://localhost:9000/question/:id) change :id to the node's id you want to get.
   - [http://localhost:9000/deleteQuestion/:id](http://localhost:9000/deleteQuestion/:id) change :id to the node's id you want to delete.
  
  
  


# Feature branch workflow

#### You are creating the branch and starting the feature implementation

- Create a branch for the US you will be implementing  
`git checkout -b US_2-01`

- Add new files/modifications/deletions to commit  
`git add .`

- Commit changes  
`git commit -m "Created DB table for Sorting Centers"`

- Push changes to feature branch  
`git push origin US_2-01`


#### You are helping your colleague implementing a feature

- Checkout the branch your colleague created  
`git checkout US_2-01`

- Pull the branch  
`git pull origin US_2-01`
