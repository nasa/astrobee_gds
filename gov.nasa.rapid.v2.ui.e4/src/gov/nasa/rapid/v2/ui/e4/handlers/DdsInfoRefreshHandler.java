package gov.nasa.rapid.v2.ui.e4.handlers;

import gov.nasa.rapid.v2.ui.e4.parts.DiscoveredDdsInfoPart;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

public class DdsInfoRefreshHandler {
	
	@Execute
	public void execute(DiscoveredDdsInfoPart ddip) {
		System.out.println("Refresh handler called");
		ddip.refresh();
	}

}
