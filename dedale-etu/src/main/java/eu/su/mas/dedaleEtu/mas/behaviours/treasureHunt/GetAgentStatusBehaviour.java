package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestLocationMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.List;

public class GetAgentStatusBehaviour extends TickerBehaviour {
	private List<AgentStatus> agentsInRange;

	public GetAgentStatusBehaviour(Agent a, long period, List<AgentStatus> agentsInRange ) {
		super(a, period);
		this.agentsInRange = agentsInRange;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-AGENT-STATUS"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if(msgReceived != null){
			this.agentsInRange.clear();
		}
		while (msgReceived != null){
			try {
				AgentStatus sgreceived = (AgentStatus)msgReceived.getContentObject();
				if (this.agentsInRange.stream().noneMatch(agentStatus -> agentStatus.getAgentName().equals(sgreceived.getAgentName()))){
					this.agentsInRange.add(sgreceived);
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			msgReceived=this.myAgent.receive(msgTemplate);
		}
	}
}