package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestLocationMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.List;

/**
 * The agent periodically share its map.
 * It blindly tries to send all its graph to its friend(s)
 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.

 * @author hc
 *
 */
public class GetPathForChestBehaviour extends TickerBehaviour {
	private List<Chest> chestsLocations;
	private List<String> receivers;

	public GetPathForChestBehaviour(Agent a, long period, List<Chest> chestsLocations) {
		super(a, period);
		this.chestsLocations = chestsLocations;
	}

	private static final long serialVersionUID = -568863390879327961L;

	@Override
	protected void onTick() {
		MessageTemplate msgTemplate=MessageTemplate.and(
				MessageTemplate.MatchProtocol("SHARE-CHEST"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
		if (msgReceived!=null) {
			ChestLocationMessage sgreceived=null;
			try {
				sgreceived = (ChestLocationMessage)msgReceived.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			sgreceived.getChestLocations().forEach(chestLocation -> {
				if (this.chestsLocations.stream().noneMatch(chestLocation1 -> chestLocation1.getChestLocation().equals(chestLocation.getChestLocation()))) {
					this.chestsLocations.add(chestLocation);
				}
				else {
					this.chestsLocations.stream().filter(chestLocation1 -> chestLocation1.getChestLocation().equals(chestLocation.getChestLocation())).forEach(chestLocation1 -> {
						if (chestLocation.getLastUpdate().after(chestLocation1.getLastUpdate())) {
							chestLocation1.setStatus(chestLocation.getStatus());
							chestLocation1.setActualLockPick(chestLocation.getActualLockPick());
							chestLocation1.setGold(chestLocation.getGold());
							chestLocation1.setDiamond(chestLocation.getDiamond());
							chestLocation1.setLastUpdate(chestLocation.getLastUpdate());
						}
					});
				}
			});
		}
	}
}