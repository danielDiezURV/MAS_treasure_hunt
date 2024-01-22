package eu.su.mas.dedaleEtu.mas.knowledge.enums;

import lombok.Getter;

@Getter
public enum TreasureHuntAction {
	E_EXPLORE(1),
	E_UNLOCK_CHEST(2),
	C_UNLOCK_CHEST(6),
	C_COLLECT(7),
	C_FOLLOW_EXPLORER(8),
	C_EXPLORE(9),
	M_SEND_MESSAGE(10),
	M_FOLLOW_EXPLORER(11),
	M_EXPLORE(12);

	private int priority;

	TreasureHuntAction(int i) {
	}
}