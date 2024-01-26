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
import weka.gui.visualize.JPEGWriter;

import java.util.*;
import java.util.stream.Collectors;

public class CollectorMainBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished;
	private boolean initialConfig;
	private List<String> agentNames;
	private List<Chest> chests;
	private List<AgentStatus> agentsInRange;
	private AgentStatus currentStatus;
	private String previousLocation;
    private Boolean myJobHereIsDone;
    private Integer tickCounter;
    private Integer period;

	public CollectorMainBehaviour(final AbstractDedaleAgent myAgent, Integer period, List<String> agentNames,
								  List<Chest> chests, List<AgentStatus> agentsInRange, AgentStatus currentStatus) {
		super(myAgent);
		this.myAgent = myAgent;
        this.period = period;
		this.chests = chests;
		this.agentNames = agentNames;
		this.agentsInRange = agentsInRange;
		this.currentStatus = currentStatus;
		this.previousLocation = "";
        this.tickCounter = 0;
        this.myJobHereIsDone = false;
		this.initialConfig = true;
	}

	@Override
	public void action() {
        tickCounter++;
		if (this.initialConfig){
            this.myAgent.addBehaviour(new ShareChestInfoBehaviour(this.myAgent, period, this.chests, this.agentsInRange, null));
            this.myAgent.addBehaviour(new GetChestInfoBehaviour(this.myAgent, period, this.chests));
			this.myAgent.addBehaviour(new ShareAgentStatusBehaviour(this.myAgent, period, this.currentStatus, agentNames));
			this.myAgent.addBehaviour(new GetAgentStatusBehaviour(this.myAgent, period, this.agentsInRange));
			initialConfig = false;
		}

		this.currentStatus.setCurrentLocation(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId());

		assert this.currentStatus.getCurrentLocation() != null;

		List<Chest> closedChests = this.chests.stream().filter(chest -> chest.getStatus().equals(ChestStatusEnum.CLOSED) && chest.getLockPickRequired() <= chest.getNotifiedLockPick()).collect(Collectors.toList());
		List<Chest> openChests = this.chests.stream().filter(chest -> chest.getStatus().equals(ChestStatusEnum.OPEN)).collect(Collectors.toList());

		List<Couple<Location,List<Couple<Observation,Integer>>>> lobs = ((AbstractDedaleAgent)this.myAgent).observe();

		this.updateChestLocations(this.currentStatus.getCurrentLocation(), lobs);

		this.wait(period);

		this.currentStatus.setAction(this.chooseNextAction(lobs, closedChests, openChests));

		this.performNextMovement(this.currentStatus.getAction(), lobs, closedChests, openChests);

	}

	private TreasureHuntAction chooseNextAction(List<Couple<Location,List<Couple<Observation,Integer>>>> lobs, List<Chest> closedChests, List<Chest> openChests) {
		List<String> freeLocations = lobs.stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(freeLocations)) {
			List<String> chestPath = new ArrayList<>();
			Optional<String> observablePathLocation;
			if (CollectionUtils.isNotEmpty(openChests)) {
				for (Chest chest : closedChests) {
					observablePathLocation = freeLocations.stream().filter(chest.getPathToChest()::contains).findFirst();
					if (observablePathLocation.isPresent()) {
						int indexOfObservableLocation = chest.getPathToChest().indexOf(observablePathLocation.get());
						this.currentStatus.setFollowingPath(chest.getPathToChest().subList(indexOfObservableLocation, chest.getPathToChest().size()));
						return TreasureHuntAction.C_COLLECT_CHEST;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(closedChests)) {
				for (Chest chest : closedChests) {
					observablePathLocation = freeLocations.stream().filter(chest.getPathToChest()::contains).findFirst();
					if (observablePathLocation.isPresent()) {
						int indexOfObservableLocation = chest.getPathToChest().indexOf(observablePathLocation.get());
						this.currentStatus.setFollowingPath(chest.getPathToChest().subList(indexOfObservableLocation, chest.getPathToChest().size()));
						return TreasureHuntAction.C_UNLOCK_CHEST;
					}
				}
			}
			else {
				this.currentStatus.setFollowingPath(this.getRandomMovementPath(freeLocations));
				return TreasureHuntAction.C_EXPLORE;
			}
		}
		return null;
	}

	private void performNextMovement(TreasureHuntAction nextAction, List<Couple<Location,List<Couple<Observation,Integer>>>> lobs, List<Chest> closedChests, List<Chest> openChests) {
		List<Observation> observables;
		this.previousLocation = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId();

		List<AgentStatus> agentsWithPriority = this.agentsInRange.stream().filter(agentStatus -> agentStatus.getPriority() > this.currentStatus.getPriority() ||
				(agentStatus.getPriority() == this.currentStatus.getPriority() && agentStatus.getHierarchy() > this.currentStatus.getHierarchy())).collect(Collectors.toList());

		List<String> priorityMovements = agentsWithPriority.stream()
				.filter(agentStatus -> CollectionUtils.isNotEmpty(agentStatus.getFollowingPath()))
				.flatMap(agentStatus -> agentStatus.getFollowingPath().stream().limit(2))
				.collect(Collectors.toList());

		if (!priorityMovements.contains(this.currentStatus.getFollowingPath().get(0))) {
			switch (nextAction) {
				case C_COLLECT_CHEST:
					if (CollectionUtils.isNotEmpty(currentStatus.getFollowingPath())) {
						this.performMovement(this.currentStatus.getFollowingPath().get(0), this.previousLocation);
					}
					// get last node of the path
					observables = ((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getRight).flatMap(Collection::stream).map(Couple::getLeft).collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(observables)) {
						//Check my backpack capacity of gold and diamonds and try to collect if i have capacity of gold or diamonds
						return;
					}
					break;

				case C_UNLOCK_CHEST:
					if (CollectionUtils.isNotEmpty(currentStatus.getFollowingPath())) {
						((AbstractDedaleAgent) this.myAgent).moveTo(new gsLocation(this.currentStatus.getFollowingPath().get(0)));
					}
					observables = ((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getRight).flatMap(Collection::stream).map(Couple::getLeft).collect(Collectors.toList());
					if (CollectionUtils.isNotEmpty(observables)) {
						boolean chestOpened = ((AbstractDedaleAgent) this.myAgent).openLock(observables.get(0));
						if (chestOpened){
							this.chests.stream().filter(chest -> chest.getChestLocation().equals(this.currentStatus.getFollowingPath().get(0))).forEach(chest -> {
								chest.setStatus(ChestStatusEnum.OPEN);
								chest.setLastUpdate(new Date());
							});
							System.out.println(this.myAgent.getLocalName() + " - Chest is open at " + ((AbstractDedaleAgent) this.myAgent).getCurrentPosition().getLocationId());
						}
					}
					break;

				case E_EXPLORE:
                    this.performMovement(this.currentStatus.getFollowingPath().get(0), this.previousLocation);
					break;
			}
		}
		else{
			//detect if concurrentLocations are accesibles observing though agent
			List<String> concurrentLocations = lobs.stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
			avoidDeadlock(priorityMovements, concurrentLocations);
		}
	}

	private List<String> getRandomMovementPath(List<String> observableLocations) {
		observableLocations.remove(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId());
		if (observableLocations.size() > 1) {
			observableLocations.remove(this.previousLocation);
		}
		return Collections.singletonList(observableLocations.get(new Random().nextInt(0, observableLocations.size())));
	}

	private void avoidDeadlock(List<String> priorityMovements, List<String> concurrentLocations) {
		boolean movementPerformed;
		List<String> freeLocations = concurrentLocations.stream().filter(lob -> !priorityMovements.contains(lob)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(freeLocations)) {
			// select randomly a free location using random
			String nextNodeId = freeLocations.get(new Random().nextInt(0,freeLocations.size()));
			this.performMovement(nextNodeId, this.currentStatus.getCurrentLocation());
		}
		else {
			String nextNodeId = concurrentLocations.get(new Random().nextInt(0,concurrentLocations.size()));
			this.currentStatus.addMovementToPath(nextNodeId);
			performMovement(nextNodeId, this.currentStatus.getCurrentLocation());
		}

    }

	private void performMovement(String nextNodeId, String previousLocation) {
		boolean movementPerformed = ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
		if (movementPerformed) {
			this.updatePaths(this.previousLocation, previousLocation);
			this.currentStatus.resetDesesperation();
		}
		else {
			this.currentStatus.desesperationIncrease();
		}
	}
	private void updatePaths(String previousLocation, String currentLocation) {
		boolean wasInPath;
		boolean isInPath;
		List<Chest> pathChestToUpdate = this.chests.stream().filter(chest -> chest.getPathToChest().contains(previousLocation) || chest.getPathToChest().contains(currentLocation)).collect(Collectors.toList());

		for (Chest chest : pathChestToUpdate){
			wasInPath = chest.getPathToChest().contains(previousLocation);
			isInPath = chest.getPathToChest().contains(currentLocation);
			if (wasInPath && !isInPath) {
				chest.addMovementToPath(previousLocation);
			}
			else if (!wasInPath && isInPath){
				int indexOfCurrentLocation = chest.getPathToChest().indexOf(currentLocation);
				chest.setPathToChest(chest.getPathToChest().subList(indexOfCurrentLocation, chest.getPathToChest().size() - 1));
			}
			else if (wasInPath){
				chest.removeMovementFromPath();
			}
		}
	}

	private void updateChestLocations(String agentLocation, List<Couple<Location, List<Couple<Observation, Integer>>>> lobs) {
		Map<String, Integer> obsMap = new HashMap<>();
		String nextNodeId = null;
		for (Couple<Location, List<Couple<Observation, Integer>>> node : lobs) {
			String accessibleNode = node.getLeft().getLocationId();
			if (CollectionUtils.isNotEmpty(node.getRight())) {
				for (Couple<Observation, Integer> obs : node.getRight()) {
					obsMap.put(obs.getLeft().toString(), obs.getRight());
				}
				if (obsMap.containsKey("Diamond") || obsMap.containsKey("Gold")) {
					// if chest is in the list comparing by location, update it
					if (this.chests.stream().anyMatch(chest -> chest.getChestLocation().equals(accessibleNode))) {
						this.chests.stream().filter(chest -> chest.getChestLocation().equals(accessibleNode)).forEach(chest -> {
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
								.pathToChest(Arrays.asList(agentLocation, accessibleNode))
								.chestLocation(accessibleNode)
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
