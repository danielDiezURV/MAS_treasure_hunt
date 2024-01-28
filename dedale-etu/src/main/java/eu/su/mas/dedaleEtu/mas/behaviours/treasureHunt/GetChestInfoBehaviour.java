package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.ChestLocationMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.List;


public class GetChestInfoBehaviour extends TickerBehaviour {
	private List<ChestStatus> chestsLocations;

	public GetChestInfoBehaviour(Agent a, long period, List<ChestStatus> chestsLocations) {
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
				if (sgreceived == null) { return; }
			} catch (UnreadableException ignored) {}
			for (ChestStatus chestLocation : sgreceived.getChestLocations()) {
				if (this.chestsLocations.stream().noneMatch(chestLocation1 -> chestLocation1.getChestLocation().equals(chestLocation.getChestLocation()))) {
					this.chestsLocations.add(chestLocation);
				}
				else {
					this.chestsLocations.stream().filter(chestLocation1 -> chestLocation1.getChestLocation().equals(chestLocation.getChestLocation())).forEach(chestLocation1 -> {
						chestLocation1.setStatus( (chestLocation.getStatus().getStatus() > chestLocation1.getStatus().getStatus()) ? chestLocation.getStatus() : chestLocation1.getStatus());
						chestLocation1.setActualLockPick( (chestLocation.getActualLockPick() > chestLocation1.getActualLockPick()) ? chestLocation.getActualLockPick() : chestLocation1.getActualLockPick());
						chestLocation1.setGold( (chestLocation.getGold() < chestLocation1.getGold()) ? chestLocation.getGold() : chestLocation1.getGold());
						chestLocation1.setDiamond( (chestLocation.getDiamond() < chestLocation1.getDiamond()) ? chestLocation.getDiamond() : chestLocation1.getDiamond());
						chestLocation1.setLastUpdate( (chestLocation.getLastUpdate().after(chestLocation1.getLastUpdate())) ? chestLocation.getLastUpdate(): chestLocation1.getLastUpdate());
						boolean betterPath = (!chestLocation1.getPathToChest().contains(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId()) &&
											  chestLocation.getPathToChest().contains(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().getLocationId()));
						chestLocation1.setPathToChest( (chestLocation1.getPathToChest() == null || betterPath) ? chestLocation.getPathToChest(): chestLocation1.getPathToChest());
					});
				}
			}
		}
	}
}