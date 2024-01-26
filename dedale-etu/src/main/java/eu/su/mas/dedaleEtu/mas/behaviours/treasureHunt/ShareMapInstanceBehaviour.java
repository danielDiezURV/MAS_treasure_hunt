package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.CollectionUtils;

public class ShareMapInstanceBehaviour extends TickerBehaviour {

	private MapRepresentation myMap;
	private List<AgentStatus> agentsInRange;

	public ShareMapInstanceBehaviour(Agent a, long period, MapRepresentation mymap, List<AgentStatus> agentsInRange) {
		super(a, period);
		this.myMap = mymap;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		if (CollectionUtils.isEmpty(this.agentsInRange)) {
			return;
		}
		List<AgentStatus> explorersInRange = this.agentsInRange.stream().filter(x -> x.getAgentName().contains("Explo")).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(explorersInRange)) {
			return;
		}

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());

		for (AgentStatus explorer : explorersInRange) {
			msg.addReceiver(new AID(explorer.getAgentName(), AID.ISLOCALNAME));
		}

		SerializableSimpleGraph<String, MapAttribute> sg = this.myMap.getSerializableGraph();
		try { msg.setContentObject(sg); } catch (IOException ignored) {}
		((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
	}
}