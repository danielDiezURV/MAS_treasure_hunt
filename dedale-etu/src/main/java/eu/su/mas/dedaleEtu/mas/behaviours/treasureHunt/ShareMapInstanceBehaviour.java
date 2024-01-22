package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class ShareMapInstanceBehaviour extends TickerBehaviour {

	private MapRepresentation myMap;
	private List<String> receivers;

	public ShareMapInstanceBehaviour(Agent a, long period, MapRepresentation mymap, List<String> receivers) {
		super(a, period);
		this.myMap = mymap;
		this.receivers = receivers;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());
		for (String agentName : receivers) {
			msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
		}

		SerializableSimpleGraph<String, MapAttribute> sg = this.myMap.getSerializableGraph();
		try {
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
		// show map in console
	}
}