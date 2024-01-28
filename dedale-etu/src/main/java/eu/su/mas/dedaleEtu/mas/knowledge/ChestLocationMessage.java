package eu.su.mas.dedaleEtu.mas.knowledge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ChestLocationMessage implements Serializable {

	private static final long serialVersionUID = -568863390879327961L;
	// Map<PathToChest, [ActualLockPick, lockPickRequired]>
	private List<ChestStatus> chestLocations;
	// Map<PathToChest, [gold, diamond]>

	public ChestLocationMessage() {
		this.chestLocations = new ArrayList<>();
	}

	public void addChestLocation(ChestStatus chestLocation) {
		this.chestLocations.add(chestLocation);
	}
}
