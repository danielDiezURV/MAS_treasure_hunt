package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.ShareNextMovement;
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
			this.myAgent.addBehaviour(new AskForChestBehaviour(this.myAgent, 100, this.chests, agentNames));
			this.myAgent.addBehaviour(new GetPathForChestBehaviour(this.myAgent, 100, this.chests));
			this.myAgent.addBehaviour(new ShareNextMovement(this.myAgent, 100, this.currentStatus, agentNames));
		}
		Location myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		assert myPosition != null;

		List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

		this.wait(100);

		this.updateMap(lobs, myPosition);

		TreasureHuntAction nextAction = this.chooseNextAction();

		this.performNextMovement(nextAction);
	}

	private TreasureHuntAction chooseNextAction() {
		Optional<Chest> closedChest = this.chests.stream().filter(chest -> chest.getStatus().equals(ChestStatusEnum.CLOSED) && chest.getLockPickRequired() < chest.getNotifiedLockPick()).findFirst();
		if (closedChest.isPresent()) {
			this.currentStatus.setFollowingPath(this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), closedChest.get().getChestLocation()));
			return TreasureHuntAction.E_UNLOCK_CHEST;
		}
		else if (this.myMap.hasOpenNode()){
			return TreasureHuntAction.E_EXPLORE;
		}
		else {
			this.done();
			return null;
		}
	}

	private void performNextMovement(TreasureHuntAction nextAction) {
		List<String> concurrentLocations=((AbstractDedaleAgent)this.myAgent).observe().stream().map(Couple::getLeft).map(Location::getLocationId).collect(Collectors.toList());
		List<AgentStatus> agentsWithPriority = this.agentsInRange.stream().filter(agentStatus -> agentStatus.getPriority() > this.currentStatus.getPriority()).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(agentsWithPriority)) {
			//Avoid blocking agents with higher priority
			List<String> priorityMovements = agentsWithPriority.stream().map(AgentStatus::getFollowingPath).limit(2).flatMap(Collection::stream).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(concurrentLocations)) {
				this.currentStatus.resetDesesperation();
				//filter concurrentLocations not in priorityMovements
				List<String> freeLocations = concurrentLocations.stream().filter(lob -> !priorityMovements.contains(lob)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(freeLocations)) {
					// select randomly a free location using random
					String nextNodeId = freeLocations.get(new Random().nextInt(freeLocations.size())-1);
					this.currentStatus.setFollowingPath(this.myMap.getShortestPath(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId(), nextNodeId));
					((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
				}
				else {
					String nextNodeId = concurrentLocations.get(new Random().nextInt(concurrentLocations.size())-1);
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
					((AbstractDedaleAgent) this.myAgent).moveTo(new gsLocation(this.currentStatus.getFollowingPath().get(0)));
					break;

				case E_EXPLORE:
					String nextNodeId = this.myMap.getShortestPathToClosestOpenNode(((AbstractDedaleAgent) this.myAgent).getCurrentPosition().getLocationId()).get(0);
					((AbstractDedaleAgent) this.myAgent).moveTo(new gsLocation(nextNodeId));
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
		if (obsMap.containsKey("Diamond") || obsMap.containsKey("Gold")){
			Chest chest = new Chest(
					ChestStatusEnum.getStatus(obsMap.get("LockIsOpen"), obsMap.getOrDefault("Gold", 0),	obsMap.getOrDefault("Diamond", 0)),
					this.myMap.getShortestPath(agentLocation, chestLocation),
					chestLocation,
					obsMap.getOrDefault("LockPicking", 0),
					obsMap.getOrDefault("Strength", 0),
					obsMap.getOrDefault("Gold", 0),
					obsMap.getOrDefault("Diamond", 0),
					new Date());

			this.chests.add(chest);
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
