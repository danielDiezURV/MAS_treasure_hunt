package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.mainBehaviours;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.*;
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

public class ExplorerMainBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;
	private boolean finished = false;

	//Explorer behaviour parameters
	private MapRepresentation myMap;
	private List<String> explorerNames;
	private List<String> agentNames;
	private List<Chest> chests;
	private List<AgentStatus> agentsInRange;
	private AgentStatus currentStatus;
	private String previousLocation;
    private Boolean myJobHereIsDone;
    private Integer tickCounter;
    private Integer period;

	public ExplorerMainBehaviour(final AbstractDedaleAgent myAgent, Integer period, MapRepresentation myMap,
								 List<String> agentNames, List<Chest> chests, List<AgentStatus> agentsInRange, AgentStatus currentStatus) {
		super(myAgent);
		this.myAgent = myAgent;
        this.period = period;
		this.myMap=myMap;
		this.chests = chests;
		this.agentNames = agentNames;
		this.agentsInRange = agentsInRange;
		this.currentStatus = currentStatus;
		this.previousLocation = "";
        this.tickCounter = 0;
        this.myJobHereIsDone = false;
	}

	@Override
	public void action() {
        tickCounter++;
		if (this.myMap == null){
			this.myMap = new MapRepresentation();
			this.myAgent.addBehaviour(new ShareMapInstanceBehaviour(this.myAgent, period, this.myMap, this.agentsInRange));
			this.myAgent.addBehaviour(new GetMapInstanceBehaviour(this.myAgent, period, this.myMap));
            this.myAgent.addBehaviour(new ShareChestInfoBehaviour(this.myAgent, period, this.chests, this.agentsInRange, this.myMap));
            this.myAgent.addBehaviour(new GetChestInfoBehaviour(this.myAgent, period, this.chests));
			this.myAgent.addBehaviour(new ShareAgentStatusBehaviour(this.myAgent, period, this.currentStatus, agentNames));
			this.myAgent.addBehaviour(new GetAgentStatusBehaviour(this.myAgent, period, this.agentsInRange));
		}

		Location myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		assert myPosition != null;

		List<Couple<Location,List<Couple<Observation,Integer>>>> lobs = ((AbstractDedaleAgent)this.myAgent).observe();

		this.wait(period);

		this.updateMap(lobs, myPosition);

		this.currentStatus.setAction(this.chooseNextAction(lobs));

		this.performNextMovement(this.currentStatus.getAction(), lobs);

	}

	private TreasureHuntAction chooseNextAction(List<Couple<Location,List<Couple<Observation,Integer>>>> lobs) {
		List<String> freeLocations = lobs.stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(freeLocations)) {
			List<Chest> closedChest = this.chests.stream().filter(chest -> chest.getStatus().equals(ChestStatusEnum.CLOSED) && chest.getLockPickRequired() <= chest.getNotifiedLockPick()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(closedChest)) {
				this.currentStatus.setFollowingPath(nextFollowingPathToChest(closedChest, freeLocations));
				return TreasureHuntAction.E_UNLOCK_CHEST;
			} else if (this.myMap.hasOpenNode()) {
				this.currentStatus.setFollowingPath(this.myMap.getShortestPathToClosestOpenNode(((AbstractDedaleAgent) this.myAgent).getCurrentPosition().getLocationId()));
				return TreasureHuntAction.E_EXPLORE;
			} else {
                if(!myJobHereIsDone){
                    System.out.println(this.myAgent.getLocalName() + " - My job here is done. All chest are open and I know all the map. Leaving explorer role and turning into coordinator. Total ticks:" + this.tickCounter);
                    this.myJobHereIsDone = true;
                }
				this.currentStatus.setFollowingPath(this.getRandomMovementPath(freeLocations));
				return TreasureHuntAction.E_COORDINATE;
			}
		}
		return null;
	}

	private List<String> nextFollowingPathToChest(List<Chest> closedChests, List<String> observableLocations) {
		List<String> chestPath = null;

		for (Chest chest : closedChests) {
			try {
				chestPath = this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), chest.getChestLocation());
			} catch (Exception ignore) {}
			if (CollectionUtils.isNotEmpty(chestPath)) {
				break;
			}
		}

		if (CollectionUtils.isEmpty(chestPath)) {
			for (Chest chest : closedChests) {
				Optional<String> observableLocation = observableLocations.stream().filter(chest.getPathToChest()::contains).findFirst();
				if (observableLocation.isPresent()) {
					int indexOfObservableLocation = chest.getPathToChest().indexOf(observableLocation.get());
					chestPath = new ArrayList<>(chest.getPathToChest().subList(indexOfObservableLocation, chest.getPathToChest().size()));
					break;
				}
			}
		}
		if (CollectionUtils.isEmpty(chestPath)) {
			chestPath = getRandomMovementPath(observableLocations);
		}
		return chestPath;
	}

	private void performNextMovement(TreasureHuntAction nextAction, List<Couple<Location,List<Couple<Observation,Integer>>>> lobs) {
		this.previousLocation = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId();

		List<AgentStatus> agentsWithPriority = this.agentsInRange.stream().filter(agentStatus -> agentStatus.getPriority() > this.currentStatus.getPriority() ||
				(agentStatus.getPriority() == this.currentStatus.getPriority() && agentStatus.getHierarchy() < this.currentStatus.getHierarchy())).collect(Collectors.toList());

		List<String> priorityMovements = agentsWithPriority.stream()
				.filter(agentStatus -> CollectionUtils.isNotEmpty(agentStatus.getFollowingPath()))
				.flatMap(agentStatus -> agentStatus.getFollowingPath().stream().limit(1))
				.collect(Collectors.toList());
		priorityMovements.addAll(agentsWithPriority.stream().map(AgentStatus::getCurrentLocation).collect(Collectors.toList()));

		if (!priorityMovements.contains(this.currentStatus.getFollowingPath().get(0))) {
			switch (nextAction) {
				case E_UNLOCK_CHEST:
					if (CollectionUtils.isNotEmpty(currentStatus.getFollowingPath())) {
						this.performMovement(this.currentStatus.getFollowingPath().get(0), Boolean.FALSE);
					}

					// get last node of the path
					List<Couple<Observation, Integer>> observables = ((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getRight).flatMap(Collection::stream).collect(Collectors.toList());
					Integer observableClosedChestStatus = observables.stream().filter(ob -> ob.getLeft().equals(Observation.LOCKSTATUS)).map(Couple::getRight).findFirst().orElse(1);

					if (observableClosedChestStatus == ChestStatusEnum.CLOSED.getStatus()) {
						boolean chestOpened = ((AbstractDedaleAgent) this.myAgent).openLock(observables.get(0).getLeft());
						if (chestOpened){
							this.chests.stream().filter(chest -> chest.getChestLocation().equals(this.currentStatus.getFollowingPath().get(0))).forEach(chest -> {
								chest.setStatus(ChestStatusEnum.OPEN);
								chest.setLastUpdate(new Date());
							});
							System.out.println(this.myAgent.getLocalName() + " - Chest is open at " + ((AbstractDedaleAgent) this.myAgent).getCurrentPosition().getLocationId());
						}
					}
					break;

				case E_COORDINATE:
				case E_EXPLORE:
					this.performMovement(this.currentStatus.getFollowingPath().get(0), Boolean.FALSE);
					break;
			}
		}
		else{
			//detect if concurrentLocations are accesibles observing though agent
			List<String> concurrentLocations = lobs.stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
			avoidDeadlock(priorityMovements, concurrentLocations);
		}
		this.currentStatus.setCurrentLocation(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId());
	}

	private ArrayList<String> getRandomMovementPath(List<String> observableLocations) {
		observableLocations.remove(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId());
		if (observableLocations.size() > 1) {
			observableLocations.remove(this.previousLocation);
		}
		return new ArrayList<>(Collections.singletonList(observableLocations.get(new Random().nextInt(0, observableLocations.size()))));
	}

	private void performMovement(String nextNodeId, Boolean avoidingDeadlock) {
		boolean movementPerformed = ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
		if (movementPerformed && !((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId().equals(previousLocation)) {
			this.currentStatus.resetDesesperation();
		}
		else if (avoidingDeadlock){
			this.currentStatus.desesperationIncrease();
		}
	}

	private void avoidDeadlock(List<String> priorityMovements, List<String> concurrentLocations) {
		boolean movementPerformed;
		List<String> freeLocations = concurrentLocations.stream().filter(lob -> !priorityMovements.contains(lob)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(freeLocations)) {
			// select randomly a free location using random
			String nextNodeId = freeLocations.get(new Random().nextInt(0,freeLocations.size()));
			if (freeLocations.size() > 1){
				freeLocations.remove(this.previousLocation);
			}
			this.currentStatus.setFollowingPath(new ArrayList<>(Collections.singletonList(nextNodeId)));
			this.performMovement(nextNodeId, Boolean.TRUE);
		}
		else {
			String nextNodeId = concurrentLocations.get(new Random().nextInt(0,concurrentLocations.size()));
			this.currentStatus.setFollowingPath(this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), nextNodeId));
			this.performMovement(nextNodeId, Boolean.TRUE);
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
