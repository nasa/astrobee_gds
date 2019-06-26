package gov.nasa.arc.verve.freeflyer.workbench.parts.advanced;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.AbstractControlPanel;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.StandardControls;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class JustStandardControls extends AbstractControlPanel {
	@Inject 
	public JustStandardControls(Composite parent, AstrobeeStateManager manager) {
		super(manager);
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);

		standardControls = new StandardControls(parent, manager);
		if(agent != null) {
			standardControls.onAgentSelected(agent);
		}
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null) {
			return;
		}
		super.onAgentSelected(a);
	}
}