# URV MESIIA Multi-agent systems practice
===============================================================
## Introduction
Implementation of a multi-agent system using Dedale framework.
The main goal of this project is to implement a multi-agent system capable of collecting as many resources as possible in a map with a limited number of resources through cooperation between agents. 

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

| Agent type | Gold capacity | Diamond capacity | Detection radius | Lock-picking strength | Communication range |
|:-----------|:-------------:|:----------------:|:----------------:|:---------------------:|:-------------------:|
| Explo1     |      -1       |        -1        |        0         |           3           |          5          |
| Explo2     |      -1       |        -1        |        0         |           3           |          5          |
| Explo3     |      -1       |        -1        |        0         |           3           |          5          |
| Explo4     |      -1       |        -1        |        0         |           3           |          5          |
| Collect1   |      400      |        -1        |        0         |           1           |          5          |
| Collect2   |      400      |        -1        |        0         |           1           |          5          |
| Collect3   |      -1       |       400        |        0         |           1           |          5          |
| Collect4   |      -1       |       400        |        0         |           1           |          5          |


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
  }
]
````

## How to execute
Follow the next steps to install and execute Dedale
1. Clone or download the Git repository
   https://github.com/danielDiezURV/MAS_treasure_hunt
2. Open the dedale-etu project using IntelliJ. Note: open
   the inner dedale-etu folder
3. Import the project as a Maven project
4. If prompted, trust the project
5. Install lombok plugin https://plugins.jetbrains.com/plugin/6317-lombok

Build and execute Dedale using:
````
mvn clean install exec:java
````
