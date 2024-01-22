package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestLocationMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;

public class AskForChestBehaviour extends TickerBehaviour {
	private List<String> receivers;
	private List<Chest> chestsLocation;

	public AskForChestBehaviour(Agent a, long period, List<Chest> chestsLocations, List<String> receivers) {
		super(a, period);
		this.chestsLocation = chestsLocations;
		this.receivers = receivers;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-CHEST");
		msg.setSender(this.myAgent.getAID());
		if (CollectionUtils.isNotEmpty(this.chestsLocation)) {
			for (String agentName : receivers) {
				msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			}
			try {
				ChestLocationMessage ChestLocationMsg = new ChestLocationMessage(this.chestsLocation);
				msg.setContentObject(ChestLocationMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
		}
	}
}