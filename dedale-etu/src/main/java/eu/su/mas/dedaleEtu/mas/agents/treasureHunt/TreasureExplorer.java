package eu.su.mas.dedaleEtu.mas.agents.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.ExplorerBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreasureExplorer extends AbstractDedaleAgent {
	private static final long serialVersionUID = -9026696439919020875L;
	private MapRepresentation myMap;
	private List<String> agentNames;
	private List<String> explorerNames;
	private List<Chest> chests;
	private List<AgentStatus> agentsInRange;
	private AgentStatus currentStatus;

	protected void setup(){
		super.setup();
		final Object[] args = getArguments();
		this.agentNames=new ArrayList<String>();
		this.chests = new ArrayList<Chest>();
		this.agentsInRange = new ArrayList<AgentStatus>();
	    this.currentStatus = AgentStatus.builder().agentName(this.getLocalName()).desesperation(0).priority(0).build();
		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}
		else{
			int i = 2;
			while (i < args.length) {
				this.agentNames.add((String) args[i]);
				i++;
			}
			this.agentNames.remove(this.getLocalName());

			this.explorerNames = this.agentNames.stream().filter(x -> x.contains("Explo")).collect(Collectors.toList());
		}

		List<Behaviour> lb=new ArrayList<Behaviour>();

//		lb.add(new ExplorerBehaviour(this,this.myMap, explorerNames, agentNames));
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}



}
