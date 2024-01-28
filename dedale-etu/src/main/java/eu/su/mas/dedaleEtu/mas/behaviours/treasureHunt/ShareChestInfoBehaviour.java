package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestLocationMessage;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;

public class ShareChestInfoBehaviour extends TickerBehaviour {
	private List<ChestStatus> chestLocations;
	private MapRepresentation myMap;
	private final List<AgentStatus> agentsInRange;



	public ShareChestInfoBehaviour(Agent a, long period, List<ChestStatus> chestLocations, List<AgentStatus> agentsInRange, MapRepresentation myMap) {
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
					for (ChestStatus chest : this.chestLocations) {
						List<String> shortestChestPathForAgent = null;
						try{ shortestChestPathForAgent = this.myMap.getShortestPath(agent.getCurrentLocation(), chest.getChestLocation()); } catch (Exception ignore) {}
						chest.setPathToChest(CollectionUtils.isNotEmpty(shortestChestPathForAgent) ? shortestChestPathForAgent : chest.getPathToChest());
						chestLocationMsg.addChestLocation(chest);
					}
				}
				else {
					chestLocationMsg.setChestLocations(this.chestLocations);
				}

                try { msg.setContentObject(chestLocationMsg); } catch (IOException ignored) {}
                ((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
			}

		}
	}
}