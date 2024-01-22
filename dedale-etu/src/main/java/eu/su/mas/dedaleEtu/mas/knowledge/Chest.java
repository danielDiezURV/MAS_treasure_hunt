package eu.su.mas.dedaleEtu.mas.knowledge;

import eu.su.mas.dedaleEtu.mas.knowledge.enums.ChestStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class Chest implements Serializable {
	private static final long serialVersionUID = -2343866585541649998L;

	private ChestStatusEnum status;
	private List<String> pathToChest;
	private String chestLocation;
	private Integer lockPickRequired;
	private Integer actualLockPick;
	private Integer notifiedLockPick;
	private Integer gold;
	private Integer diamond;
	private Date lastUpdate;
	private Integer notifiedTimes;

	public Chest(ChestStatusEnum chestStatus, List<String> pathToChest, String chestLocation, Integer lockPickRequired, Integer actualLockPick, Integer gold, Integer diamond, Date lastUpdate) {
		this.status = chestStatus;
		this.pathToChest = pathToChest;
		this.chestLocation = chestLocation;
		this.actualLockPick = lockPickRequired;
		this.lockPickRequired = actualLockPick;
		this.notifiedLockPick = getActualLockPick();
		this.gold = gold;
		this.diamond = diamond;
		this.lastUpdate = lastUpdate;
		this.notifiedTimes = 0;
	}
}
