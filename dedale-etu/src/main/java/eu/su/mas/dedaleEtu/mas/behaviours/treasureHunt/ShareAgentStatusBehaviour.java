package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

public class ShareAgentStatusBehaviour extends TickerBehaviour{
	private static final long serialVersionUID = -998931308518904610L;
	private final List<String> receivers;
	private final AgentStatus currentStatus;

	public ShareAgentStatusBehaviour(Agent myagent, long period, AgentStatus currentStatus, List<String> receivers) {
		super(myagent, period);
		this.receivers=receivers;
		this.currentStatus = currentStatus;
	}

	@Override
	public void onTick() {
		if (this.currentStatus == null){
			return;
		}
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(this.myAgent.getAID());
		msg.setProtocol("SHARE-AGENT-STATUS");

		if (myPosition!=null && !myPosition.getLocationId().isEmpty()){
			try {
				msg.setContentObject(this.currentStatus);
			} catch (IOException ignored) {}
			for (String agentName : receivers) {
				msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
			}
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		}
	}
}