package eu.su.mas.dedaleEtu.mas.knowledge;

import eu.su.mas.dedale.env.EntityType;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.mas.knowledge.enums.ChestStatusEnum;
import eu.su.mas.dedaleEtu.mas.knowledge.enums.TreasureHuntAction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
public class AgentStatus implements Serializable {

	private static final long serialVersionUID = 9122840206357843902L;

	@Getter @Setter
	private TreasureHuntAction action;
	@Getter @Setter
	private String agentName;
	@Getter @Setter
	private EntityType senderType;
	@Getter @Setter
	private String currentLocation;
	@Getter @Setter
	private List<String> followingPath;
	@Setter
	private Integer priority;
	private Integer desesperation;


	public Integer getPriority() {
		return priority + desesperation;
	}

	public void desesperationIncrease() {
		this.desesperation++;
	}

	public void resetDesesperation() {
		this.desesperation = 0;
	}
}
