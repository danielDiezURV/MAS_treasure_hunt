package eu.su.mas.dedaleEtu.mas.knowledge.enums;

import lombok.Getter;

@Getter
public enum TreasureHuntActionEnum {
	E_EXPLORE(4),
	E_UNLOCK_CHEST(3),
	E_COORDINATE(0),
	C_COLLECT_CHEST(5),
	C_UNLOCK_CHEST(2),
	C_EXPLORE(1);

	private final int priority;

	TreasureHuntActionEnum(int i) {
		this.priority = i;
	}
}