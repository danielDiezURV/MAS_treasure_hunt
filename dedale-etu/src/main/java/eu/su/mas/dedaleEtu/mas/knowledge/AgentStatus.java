package eu.su.mas.dedaleEtu.mas.knowledge;

import eu.su.mas.dedaleEtu.mas.knowledge.enums.TreasureHuntActionEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Builder
public class AgentStatus implements Serializable {

	private static final long serialVersionUID = 9122840206357843902L;

	@Getter
	private TreasureHuntActionEnum action;
	@Getter @Setter
	private String agentName;
	@Getter @Setter
	private String currentLocation;
	@Getter @Setter
	private List<String> followingPath;
	@Setter
	private Integer priority;
	@Getter
	private Integer desesperation;
	@Getter
	private Integer hierarchy;

	public void setAction(TreasureHuntActionEnum action) {
		this.action = action;
		this.priority = action.getPriority();
	}
	public Integer getPriority() {
		return priority + desesperation;
	}

	public void desesperationIncrease() {
		this.desesperation++;
	}

	public void resetDesesperation() {
		this.desesperation = 0;
	}
	public void addMovementToPath(String location) {this.followingPath.add(0, location);}
	public void removeMovementFromPath() {
		if (CollectionUtils.isNotEmpty(this.followingPath)) {
			this.followingPath.remove(0);
		}
	}
}
