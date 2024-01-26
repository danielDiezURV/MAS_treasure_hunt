package eu.su.mas.dedaleEtu.mas.knowledge.enums;

import lombok.Getter;

@Getter
public enum ChestStatusEnum {
	CLOSED(0),
	OPEN(1),
	EMPTY(2);

	private final int status;

	ChestStatusEnum(int i) {
		this.status = i;
	}

	public static ChestStatusEnum getStatus(int status, int gold, int diamonds) {
		if (status == 0)
				return ChestStatusEnum.CLOSED;
		if (status == 1 && gold == 0 && diamonds == 0)
				return ChestStatusEnum.EMPTY;
		else return ChestStatusEnum.OPEN;
	}
}
