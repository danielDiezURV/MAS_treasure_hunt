package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.List;

/**
 * The agent periodically share its map.
 * It blindly tries to send all its graph to its friend(s)
 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.

 * @author hc
 *
 */
public class GetMapInstanceBehaviour extends TickerBehaviour {

	private MapRepresentation myMap;
	private List<String> receivers;

	public GetMapInstanceBehaviour(Agent a, long period, MapRepresentation mymap) {
		super(a, period);
		this.myMap = mymap;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-TOPO"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
			try {
				sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			if (sgreceived != null) {
				this.myMap.mergeMap(sgreceived);
			}
		}

	}
}