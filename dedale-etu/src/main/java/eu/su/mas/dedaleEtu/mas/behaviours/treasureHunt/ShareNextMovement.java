package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class ShareNextMovement extends TickerBehaviour{
	private static final long serialVersionUID = -998931308518904610L;
	private final List<String> receivers;
	private final AgentStatus currentStatus;

	public ShareNextMovement(Agent myagent, long period,AgentStatus currentStatus, List<String> receivers) {
		super(myagent, period);
		this.receivers=receivers;
		this.currentStatus = currentStatus;
	}

	@Override
	public void onTick() {
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		//A message is defined by : a performative, a sender, a set of receivers, (a protocol),(a content (and/or contentOBject))
		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		
		msg.setSender(this.myAgent.getAID());
		msg.setProtocol("ShareNextMovementProtocol");

		if (myPosition!=null && myPosition.getLocationId()!=""){
			try {
				msg.setContentObject(this.currentStatus);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			for (String agentName : receivers) {
				msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
			}

			//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		}
	}
}