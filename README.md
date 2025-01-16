# StateMachine


Changelog
| Version | Changes | Bugs | Fixes |
|---------|---------|------|-------|
| 0.0.1 | Initial State Machine Implementation||
| 0.0.2 | Visibility API for States and Execution Threads|||


InnoBridge StateMachine (ISM) is a developer-friendly library for creating distributed state machines. Transitions between states are defined programmatically using Java functions, and ISM supports the creation of child state machines.

## Architecture

![StateMachine](https://github.com/user-attachments/assets/a91166ff-0854-45b2-8300-142a4401ebf6)

### State
Our state machine consists of the State interface, which executes the action function when being processed.
```java
public interface State {
   void action(Optional<JsonNode> input); 
}
```

Every state machine instance must consists of the InitialState and TerminalState.

#### Initial State
InitialState is the starting point of our state machine instance, it is responsible for instantiating our state machine, and defining the transitions from one state and another.

When we create our Initial State class we extend AbstractInitialState because it contains the boilerplate code for processing the state.

Eg. InitialHelloWorld.java
```java
public class InitialHelloWorld extends AbstractInitialState {
    
    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Initializing Hello World");
    }

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new WhatIsYourName());
        transitions.put(new WhatIsYourName(), state -> new HelloWorld());
        transitions.put(new HelloWorld(), state -> {
            HelloWorld helloWorld = (HelloWorld) state;
            return new NonBlockingHelloWorld(helloWorld.getName());
        });
        transitions.put(new NonBlockingHelloWorld(null), state -> new TerminalHelloWorld());
        this.transitions = transitions;
    }
}
```

We define the transitions of the setTransitions method as map of Java functions. Where they key of the map is the source state, and the state returned by the Java function is the destination state of the transition.

```java
transitions.put(new HelloWorld(), state -> {
            HelloWorld helloWorld = (HelloWorld) state;
            return new NonBlockingHelloWorld(helloWorld.getName());
        });
```

The above shows how we can pass value from the source state and the destination state.

#### Terminal State
The TerminalState represents the end of a state machine instance, it is responsible for cleaning up the state machine instance. You can implement your own Terminal State by extending AbstractTerminalState.

Eg. TerminalHelloWorld
```java
public class TerminalHelloWorld extends AbstractTerminalState {
    
    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Terminating Hello World");
    }
}
```

##### Non Blocking Transition State
A NonBlockingTransitionState is a state that the transition from previous state to the current state(Non Blocking) happens automatically without needing external triggers. When the previous state has been processed, and the transition is to a non blocking state a message with the state machine instance id is published to a queue. A consumer will read the instance id from the queue and process the non blocking state.

Eg. NonBlockingHelloWorld
```java
public class NonBlockingHelloWorld extends AbstractNonBlockingTransitionState {
    
    private String name;

    public NonBlockingHelloWorld(String name) {
        super();
        this.name = name;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Non BlockingHello World " + name);
    } 
}
```

##### Blocking Transition State
A BlockingTransitionState is a state that pauses the execution of the state machine instance, waiting from an external trigger.

The state machine resumes when the processStateMachine API in the StateMachineService is called with the state machine instance id and an optional input JSON payload.

Eg. HelloWorld
```java
public class HelloWorld extends AbstractBlockingTransitionState {
    
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        input.ifPresentOrElse(json -> {
            System.out.println("Hello " + json.get("name").asText());
            this.name = json.get("name").asText();
        }, () -> {
            System.out.println("Hello World without input");
        });
    } 
}
```

##### Child State
A ChildState is a state that allows you to spin up child state machine instance from a parent instance. The parent state machine instance will be block until all the child instance has finished executing.

To create child instance we define a list of initial states in the registerChildInstances method.

Eg. ChildMeal
```java
public class ChildMeal extends AbstractChildState {

    @Override
    public List<InitialState> registerChildInstances() {
        return List.of(
                new InitialBreakfast(),
                new InitialLunch(),
                new InitialDinner()
        );
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Having breakfast, lunch and dinner");
    }
    
}
```

#### Execution Thread
An ExecutionThread keeps track of the current state in a state machine instance. It has the following fields:

- id: The state machine instance id.
- instanceType: The concrete implementation of the InitialState
- parentId: The parent state machine instance id if the current instance is a child instance. We will use this id to notify the parent instance when the child instance has finished execution.
- currentState: The current state of the state machine instance.

## Requirements
- MongoDB
- RabbitMQ
To make the state machine distributed we need to persist the states and execution thread in database. We store the states and execution thread in the MongoDB collections States and StateMachineInstance respectively.

## Setup
Add the following dependency in pom.xml
```xml
<dependency>
   <groupId>io.github.innobridge</groupId>
   <artifactId>statemachine</artifactId>
   <version>0.0.2</version>
 </dependency>
```

### Developing Locally
Refer to this demo repo: https://github.com/InnoBridge/StateMachineDemo

We will use docker to set up the infrastructure (MongoDB, RabbitMQ) docker-compose.yml
```
services:

  ############ statemachine application ############
  statemachine_application:
    image: openjdk:22-slim
    container_name: statemachine-application
    working_dir: /app
    extra_hosts:
      - "localhost:192.168.65.2"
    ports:
      - 8080:8080
      - 5005:5005
    env_file:
      - .env
    volumes:
      - .:/app
      - /var/run/docker.sock:/var/run/docker.sock
      - ./local/root:/root
    tty: true

  ############ mongodb ############
  mongodb:
    image: mongo:latest
    container_name: mongodb
    ports:
      - "${MONGODB_PORT}:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      - MONGO_INITDB_ROOT_USERNAME=${MONGODB_ROOT_USERNAME}
      - MONGO_INITDB_ROOT_PASSWORD=${MONGODB_ROOT_PASSWORD}

  ############ rabbitmq ############
  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    ports:
      - "5672:5672"   # AMQP protocol port
      - "15672:15672" # Management UI port
    environment:
      - RABBITMQ_DEFAULT_USER=admin
      - RABBITMQ_DEFAULT_PASS=admin
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

volumes:
  mongodb_data:
  rabbitmq_data:
```

Reference https://github.com/InnoBridge/StateMachine/tree/main/src/main/java/io/github/innobridge/statemachine/state/usecases

to create your states, and the InitialState to define the transaction for your workflow.

### Component Scan State Machine Library
In Application.java scan the state machine base package.
```java
@SpringBootApplication
@EnableMongoRepositories(basePackages = "io.github.innobridge.statemachine.repository")
@ComponentScan(basePackages = {
    "io.github.innobridge.statemachinedemo",
    "io.github.innobridge.statemachine"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### (Optional) Creating Controller
Creating controller to create your state machine instance, and call processStateMachine API to trigger a blocking state.

Eg. StateMachineController
```java
@RestController
public class StateMachineController {

    @Autowired
    private StateMachineService stateMachineService;

    @PostMapping("/create/helloworld")
    public String createHelloWorld(
            @RequestBody(required = false) JsonNode input
    ) {
        return stateMachineService.createStateMachine(new InitialHelloWorld(), Optional.ofNullable(input), Optional.empty());
    }

    @PostMapping("/create/meal")
    public String createMeal(
            @RequestBody(required = false) JsonNode input
    ) {
        return stateMachineService.createStateMachine(new InitialMeal(), Optional.ofNullable(input), Optional.empty());
    }

    @PostMapping("/process")
    public String processStateMachine(@RequestParam String instanceId,
            @RequestBody(required = false) JsonNode input) {
        return stateMachineService.processStateMachine(instanceId, Optional.ofNullable(input));
    }
}
```

#### Spin Up State Machine Application with Docker
Run the following commands in your terminal.
```bash
docker compose build && docker compose up
```

In another terminal
```bash
docker exec -it statemachine-application sh
./mvnw spring-boot:run
```

You can access the endpoints to create and process state machine using the endpoint http://localhost:8080/swagger-ui/index.html

### Debugging
You can query the database using the following terminal command.

```bash
docker exec -it mongodb sh
mongosh -u root -p example
use StateMachine
db.States.find({})
db.StateMachineInstance.find({})
```
