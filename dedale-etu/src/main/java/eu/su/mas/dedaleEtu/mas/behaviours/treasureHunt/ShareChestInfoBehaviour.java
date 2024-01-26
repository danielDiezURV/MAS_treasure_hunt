package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestLocationMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ShareChestInfoBehaviour extends TickerBehaviour {
	private List<Chest> chestLocations;
	private MapRepresentation myMap;
	private final List<AgentStatus> agentsInRange;



	public ShareChestInfoBehaviour(Agent a, long period, List<Chest> chestLocations, List<AgentStatus> agentsInRange, MapRepresentation myMap) {
		super(a, period);
		this.chestLocations = chestLocations;
		this.agentsInRange = agentsInRange;
		this.myMap = myMap;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-CHEST");
		msg.setSender(this.myAgent.getAID());
		if (CollectionUtils.isNotEmpty(this.chestLocations) && CollectionUtils.isNotEmpty(this.agentsInRange)) {
			for (AgentStatus agent : agentsInRange) {
				msg.addReceiver(new AID(agent.getAgentName(), AID.ISLOCALNAME));

				ChestLocationMessage chestLocationMsg = new ChestLocationMessage();
				if (this.myMap != null){
					for (Chest chest : this.chestLocations) {
						List<String> shortestChestPathForAgent = null;
						try{ shortestChestPathForAgent = this.myMap.getShortestPath(agent.getCurrentLocation(), chest.getChestLocation()); } catch (Exception e) {e.printStackTrace();}
						chest.setPathToChest(CollectionUtils.isNotEmpty(shortestChestPathForAgent) ? shortestChestPathForAgent : chest.getPathToChest());
						chestLocationMsg.addChestLocation(chest);
					}
				}
				else {
					chestLocationMsg.setChestLocations(this.chestLocations);
				}

                try { msg.setContentObject(chestLocationMsg); } catch (IOException e) {throw new RuntimeException(e);}
                ((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
			}

		}
	}
}