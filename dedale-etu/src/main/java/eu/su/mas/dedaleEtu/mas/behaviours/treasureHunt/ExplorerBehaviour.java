package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.enums.ChestStatusEnum;
import eu.su.mas.dedaleEtu.mas.knowledge.enums.TreasureHuntAction;
import jade.core.behaviours.SimpleBehaviour;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExplorerBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;
	private boolean finished = false;

	//Explorer behaviour parameters
	private MapRepresentation myMap;
	private List<String> explorerNames;
	private List<String> agentNames;
	private List<Chest> chests;
	private List<AgentStatus> agentsInRange;
	private AgentStatus currentStatus;



	public ExplorerBehaviour(final AbstractDedaleAgent myAgent, MapRepresentation myMap, List<String> explorerNames,
							 List<String> agentNames, List<Chest> chests, List<AgentStatus> agentsInRange, AgentStatus currentStatus) {
		super(myAgent);
		this.myAgent = myAgent;
		this.myMap=myMap;
		this.chests = chests;
        this.explorerNames = explorerNames;
		this.agentNames = agentNames;
		this.agentsInRange = agentsInRange;
		this.currentStatus = currentStatus;
	}

	@Override
	public void action() {
		if (this.myMap == null){
			this.myMap = new MapRepresentation();
			this.myAgent.addBehaviour(new ShareMapInstanceBehaviour(this.myAgent, 100, this.myMap, explorerNames));
			this.myAgent.addBehaviour(new GetMapInstanceBehaviour(this.myAgent, 100, this.myMap));
			this.myAgent.addBehaviour(new shareChestInfoBehaviour(this.myAgent, 100, this.chests, agentNames));
			this.myAgent.addBehaviour(new GetChestInfoBehaviour(this.myAgent, 100, this.chests, this.myMap));
			this.myAgent.addBehaviour(new ShareAgentStatusBehaviour(this.myAgent, 100, this.currentStatus, agentNames));
			this.myAgent.addBehaviour(new GetAgentStatusBehaviour(this.myAgent, 100, this.agentsInRange));
		}
		Location myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		assert myPosition != null;

		List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

		this.wait(100);

		this.updateMap(lobs, myPosition);

		this.currentStatus.setAction(this.chooseNextAction());

		this.performNextMovement(this.currentStatus.getAction());

	}

	private TreasureHuntAction chooseNextAction() {
		Optional<Chest> closedChest = this.chests.stream().filter(chest -> chest.getStatus().equals(ChestStatusEnum.CLOSED) && chest.getLockPickRequired() <= chest.getNotifiedLockPick()).findFirst();
		if (closedChest.isPresent()) {
			// Si el nodo de closedChest.get().getChestLocation no se ha descubierto en el mapa, se añade

			List<String> shortestPath = this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), closedChest.get().getChestLocation());
 			if (CollectionUtils.isNotEmpty(shortestPath)){
				this.currentStatus.setFollowingPath(shortestPath);
			}
			else {
				// If any other location of pathToChest is found in the observable locations, follow the path from that location
				List<String> observableLocations = ((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
				List<String> pathToChest = closedChest.get().getPathToChest();
				Optional<String> observableLocation = observableLocations.stream().filter(pathToChest::contains).findFirst();
				if (observableLocation.isPresent()){
					int indexOfObservableLocation = pathToChest.indexOf(observableLocation.get());
					this.currentStatus.setFollowingPath(pathToChest.subList(indexOfObservableLocation, pathToChest.size()));
				}
				else {
					// followingPath = random movement to observable location
					this.currentStatus.setFollowingPath(Collections.singletonList(observableLocations.get(new Random().nextInt(0,observableLocations.size() - 1))));
				}
			}
			return TreasureHuntAction.E_UNLOCK_CHEST;
		}
		else if (this.myMap.hasOpenNode()) {
			this.currentStatus.setFollowingPath(this.myMap.getShortestPathToClosestOpenNode(((AbstractDedaleAgent) this.myAgent).getCurrentPosition().getLocationId()));
			return TreasureHuntAction.E_EXPLORE;
		}
		else {
			 closedChest = this.chests.stream().filter(chest -> chest.getStatus().equals(ChestStatusEnum.CLOSED)).findFirst();
			 if (closedChest.isPresent()){
				 this.currentStatus.setFollowingPath(this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), closedChest.get().getChestLocation()));
				 return TreasureHuntAction.E_UNLOCK_CHEST;
			 }
			 else done();
			 return null;
		}
	}

	private void performNextMovement(TreasureHuntAction nextAction) {
		List<String> concurrentLocations=((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
		// AgentsWithPriority -> where agent priority is higher than current agent priority or same priority but higher hierarchy
		List<AgentStatus> agentsWithPriority = this.agentsInRange.stream().filter(agentStatus -> agentStatus.getPriority() > this.currentStatus.getPriority() ||
				(agentStatus.getPriority() == this.currentStatus.getPriority() && agentStatus.getHierarchy() > this.currentStatus.getHierarchy())).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(agentsWithPriority)) {
			//Avoid blocking agents with higher priority
			List<String> priorityMovements = agentsWithPriority.stream().map(AgentStatus::getFollowingPath).limit(2).flatMap(Collection::stream).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(concurrentLocations)) {
				this.currentStatus.resetDesesperation();
				//filter concurrentLocations not in priorityMovements
				List<String> freeLocations = concurrentLocations.stream().filter(lob -> !priorityMovements.contains(lob)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(freeLocations)) {
					// select randomly a free location using random
					String nextNodeId = freeLocations.get(new Random().nextInt(0,freeLocations.size()-1));
					this.currentStatus.setFollowingPath(this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), nextNodeId));
					((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
				}
				else {
					String nextNodeId = concurrentLocations.get(new Random().nextInt(0,concurrentLocations.size()-1));
					this.currentStatus.setFollowingPath(this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), nextNodeId));
					((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
				}
			}
			else {
				this.currentStatus.desesperationIncrease();
			}
		}

		else{
			//No agents with higher priority, perform next action
			switch (nextAction) {
				case E_UNLOCK_CHEST:
					if (CollectionUtils.isNotEmpty(currentStatus.getFollowingPath())) {
						((AbstractDedaleAgent) this.myAgent).moveTo(new gsLocation(this.currentStatus.getFollowingPath().get(0)));
					}
					// get last node of the path
					List<Observation> observables = ((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getRight).flatMap(Collection::stream).map(Couple::getLeft).collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(observables)) {
						Boolean chestOpened = ((AbstractDedaleAgent) this.myAgent).openLock(observables.get(0));
						if (chestOpened){
							//print
							System.out.println(this.myAgent.getLocalName() + " - Chest is open at " + ((AbstractDedaleAgent) this.myAgent).getCurrentPosition().getLocationId());
						}
					}
					break;

				case E_EXPLORE:
					((AbstractDedaleAgent) this.myAgent).moveTo(new gsLocation(this.currentStatus.getFollowingPath().get(0)));
					break;

				default:
					finished = true;
					System.out.println(this.myAgent.getLocalName() + " - Exploration successufully done, behaviour removed.");

			}
		}

	}

	private void updateChestLocations(String agentLocation, String chestLocation, List<Couple<Observation, Integer>> nodeData) {
		Map<String, Integer> obsMap = new HashMap<>();
		for (Couple<Observation, Integer> obs : nodeData) {
			obsMap.put(obs.getLeft().toString(), obs.getRight());
		}
		if (obsMap.containsKey("Diamond") || obsMap.containsKey("Gold")) {
			// if chest is in the list comparing by location, update it
			if (this.chests.stream().anyMatch(chest -> chest.getChestLocation().equals(chestLocation))) {
				this.chests.stream().filter(chest -> chest.getChestLocation().equals(chestLocation)).forEach(chest -> {
					chest.setStatus(ChestStatusEnum.getStatus(obsMap.get("LockIsOpen"), obsMap.getOrDefault("Gold", 0), obsMap.getOrDefault("Diamond", 0)));
					chest.setLockPickRequired(obsMap.getOrDefault("LockPicking", 0));
					chest.setActualLockPick(obsMap.getOrDefault("Strength", 0));
					chest.setNotifiedLockPick(obsMap.getOrDefault("LockPicking", 0));
					chest.setGold(obsMap.getOrDefault("Gold", 0));
					chest.setDiamond(obsMap.getOrDefault("Diamond", 0));
					chest.setLastUpdate(new Date());
				});
			}
			else{
			Chest chest = Chest.builder()
						 .status(ChestStatusEnum.getStatus(obsMap.get("LockIsOpen"), obsMap.getOrDefault("Gold", 0),	obsMap.getOrDefault("Diamond", 0)))
						 .pathToChest(this.myMap.getShortestPath(agentLocation, chestLocation))
						 .chestLocation(chestLocation)
						 .lockPickRequired(obsMap.getOrDefault("LockPicking", 0))
						 .actualLockPick(obsMap.getOrDefault("Strength", 0))
						 .notifiedLockPick(obsMap.getOrDefault("LockPicking", 0))
						 .gold(obsMap.getOrDefault("Gold", 0))
						 .diamond(obsMap.getOrDefault("Diamond", 0))
						 .lastUpdate(new Date())
						 .build();

			this.chests.add(chest);
			}
		}

	}

	private void updateMap(List<Couple<Location, List<Couple<Observation, Integer>>>> lobs, Location myPosition) {
		this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

		String nextNodeId = null;
		for (Couple<Location, List<Couple<Observation, Integer>>> node : lobs) {
			Location accessibleNode = node.getLeft();
			boolean isNewNode = this.myMap.addNewNode(accessibleNode.getLocationId());
			if (isNewNode) {
				this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
				this.myMap.addNode(accessibleNode.getLocationId(), MapAttribute.open);
			}
			if (CollectionUtils.isNotEmpty(node.getRight())) {
				updateChestLocations(myPosition.getLocationId(), accessibleNode.getLocationId(), node.getRight());
			}
		}
	}

	private void wait(int miliseconds) {
		try {
			this.myAgent.doWait(miliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean done() {
		return finished;
	}

}
