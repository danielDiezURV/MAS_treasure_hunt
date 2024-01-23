package eu.su.mas.dedaleEtu.mas.knowledge;

import eu.su.mas.dedaleEtu.mas.knowledge.enums.ChestStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter @Setter @Builder
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

}
