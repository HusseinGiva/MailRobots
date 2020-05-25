# Mail Robots
## Execution
```
javac src/*.java
java src/Main
```
## Notes
The execution process is the same as in the Loading Docks project so it can be made using Eclipse/IntelliJ and just running the main class.
Since the project has some complex calculations being made when it is executed after compilation, sometimes the GUI window may not appear due to hardware limitations and complex calculations that may exhasut the system resources. If this happens, simply terminate the execution and launch it again. This should work. Again, if this happens, just exit and relaunch and it should be fine.
In red are represented the warehouses, in green the delivery addresses and in arrow shapes the agents that will perform the deliveries.
To change the number of warehouses, agents, grid size, houses, mail to be delivered and even to make all the warehouses receive the same amount of mail instead of different, just go to the Board.java file and perform the necessary changes to the values of the parameters.
To change between self-interest and common interest goal behaviour, simply uncomment/comment the lines 388-398 of the Agent.java file.
As all other projects which use the random function, sometimes certain unsupported scenarios may occur. Although rare, just rerun the program to eliminate it and present a supported one.
