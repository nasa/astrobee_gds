package gov.nasa.rapid.v2.ui.e4.handlers;

import gov.nasa.rapid.v2.ui.e4.parts.DiscoveredDdsInfoPart;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class DdsInfoTopicsHandler {
	private final String discoveredDdsInfoName = "gov.nasa.rapid.v2.ui.e4.part.discoveredDdsInfo";
	private final String topicsToolItemName = "gov.nasa.rapid.v2.ui.e4.directtoolitem.topics";

	@Execute
	public void execute(DiscoveredDdsInfoPart ddip, EPartService eps) {
		System.out.println("Topics handler called");

		MPart part = eps.findPart(discoveredDdsInfoName);
		List<MToolBarElement> tbes = part.getToolbar().getChildren();

		for(MToolBarElement tb : tbes) {
			if(tb.getElementId().equals(topicsToolItemName)) {
				MDirectToolItem dti = (MDirectToolItem)tb;
				ddip.setShowTopics(dti.isSelected());
			}
		}
	}
}
