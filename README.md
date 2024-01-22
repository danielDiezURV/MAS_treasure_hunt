# URV MESIIA Multi-agent systems practice: Preliminary revision
===============================================================
## Introduction
This is a preliminar verion of an implementation of a multi-agent system using Dedale framework.
The main goal of this part of the project is to initialize the dedale enviroment system with the urv.dgs map and the agents.

### Map
The map used for this implementation is the one defined in moodle: urv.dgs.
The URVelements configuration for the map is defined as follows:
```
mapname:MultiTypesV2
gold:-139586:300:3:3
gold:-139601:200:3:3
gold:-140818:100:3:3
gold:-140232:100:3:3
gold:-138928:100:3:3
diamonds:-139451:300:3:3
diamonds:-138849:200:3:3
diamonds:-139599:100:3:3
diamonds:-138877:100:3:3
diamonds:-139499:100:3:3
```
### Agents

| Agent type | Gold capacity | Diamond capacity | Detection radius | Lock-picking strength |
|:-----------|:-------------:|:----------------:|:----------------:|:---------------------:|
| Explo1     |      -1       |        -1        |        0         |           3           |
| Explo2     |      -1       |        -1        |        0         |           3           |
| Explo3     |      -1       |        -1        |        0         |           3           |
| Explo4     |      -1       |        -1        |        0         |           3           |
| Collect1   |      400      |        -1        |        0         |           1           |
| Collect2   |      400      |        -1        |        0         |           1           |
| Collect3   |      -1       |       400        |        0         |           1           |
| Collect4   |      -1       |       400        |        0         |           1           |

It also include the new agent defined in the previous step called messsenger with the following atributes:

| Agent type | Gold capacity | Diamond capacity | Detection radius | Lock-picking strength |
|:-----------|:-------------:|:----------------:|:----------------:|:---------------------:|
| msg1       |      -1       |        -1        |        0         |           0           |
| msg2       |      -1       |        -1        |        0         |           0           |
| msg3       |      -1       |        -1        |        0         |           0           |
| msg4       |      -1       |        -1        |        0         |           0           |
| msg5       |      -1       |        -1        |        0         |           0           |
| msg6       |      -1       |        -1        |        0         |           0           |
| msg7       |      -1       |        -1        |        0         |           0           |
| msg8       |      -1       |        -1        |        0         |           0           |

Agents configuration file: resources/agents.json
````
[
  {
    "agentType":"agentExplo",
    "agentName":"Explo1",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 3,
    "lockPickingExpertise":3
  },
  {
    "agentType":"agentExplo",
    "agentName":"Explo2",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 3,
    "lockPickingExpertise": 3
  },
  {
    "agentType":"agentExplo",
    "agentName":"Explo3",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 3,
    "lockPickingExpertise": 3
  },
  {
    "agentType":"agentExplo",
    "agentName":"Explo4",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 3,
    "lockPickingExpertise":3
  },
  {
    "agentType":"agentCollect",
    "agentName": "Collect1",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": 400,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 1,
    "lockPickingExpertise":1
  },
  {
    "agentType":"agentCollect",
    "agentName": "Collect2",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": 400,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 1,
    "lockPickingExpertise":1
  },
  {
    "agentType":"agentCollect",
    "agentName": "Collect3",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":400,
    "detectionRadius": 0,
    "strengthExpertise": 1,
    "lockPickingExpertise":1
  },
  {
    "agentType":"agentCollect",
    "agentName": "Collect4",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":400,
    "detectionRadius": 0,
    "strengthExpertise": 1,
    "lockPickingExpertise":1
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg1",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg2",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg3",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg4",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg5",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg6",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"agentExplo",
    "agentName": "Msg7",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  },
  {
    "agentType":"wumpus",
    "agentName": "Msg8",
    "communicationRange": 3,
    "initialLocation": "free",
    "backPackCapacityGold": -1,
    "backPackCapacityDiamond":-1,
    "detectionRadius": 0,
    "strengthExpertise": 0,
    "lockPickingExpertise":0
  }
]
````

## How to execute
Follow the next steps to install and execute Dedale
1. Clone or download the Git repository
   https://gitlab.com/dedale/dedale-etu
2. Open the dedale-etu project using IntelliJ. Note: open
   the inner dedale-etu folder
3. Import the project as a Maven project
4. If prompted, trust the project

Build and execute Dedale using:
````
mvn install exec:java
````
