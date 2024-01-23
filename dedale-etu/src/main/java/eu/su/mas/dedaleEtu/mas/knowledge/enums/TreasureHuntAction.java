package eu.su.mas.dedaleEtu.mas.knowledge.enums;

import lombok.Getter;

@Getter
public enum TreasureHuntAction {
	E_EXPLORE(9),
	E_UNLOCK_CHEST(8),
	C_UNLOCK_CHEST(7),
	C_COLLECT(6),
	C_FOLLOW_EXPLORER(5),
	C_EXPLORE(4),
	M_SEND_MESSAGE(3),
	M_FOLLOW_EXPLORER(2),
	M_EXPLORE(1);

	private int priority;

	TreasureHuntAction(int i) {
		this.priority = i;
	}
}