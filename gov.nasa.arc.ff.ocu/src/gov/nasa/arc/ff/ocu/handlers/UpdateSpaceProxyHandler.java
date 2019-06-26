/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.ff.ocu.handlers;

import gov.nasa.util.ui.MessageBox;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateSpaceProxyHandler {

	final int WINDOW_WIDTH = 500;
	final int WINDOW_HEIGHT = 600;
	File qosFile = null;
	Document xml = null;
	
	@Execute
	public void execute(final MApplication application) {
		
		Node IPNode = null;
		boolean alreadySet = false;
	
		try{
			qosFile = new File(FileLocator.toFileURL(Platform.getBundle("gov.nasa.rapid.v2.ui.e4").getEntry("RAPID_QOS_PROFILES.xml")).toURI());
		}catch(final Exception e){
			new MessageBox().error("Error", "Could not find RAPID_QOS_PROFILES.xml. Unable to make changes to the Space Proxy");
			e.printStackTrace();
		}
		
		try{
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			xml = docFactory.newDocumentBuilder().parse(qosFile);
			xml.getDocumentElement().normalize();
			//getDocumentElement().getNodeName()
			
			IPNode = findIPNode(xml);
			if(IPNode ==null){
				createIPNode(xml);
				IPNode = findIPNode(xml);
			}else{
				alreadySet = true;
			}
			
		}catch(final Exception e){
			
		}
		
		
		final UpdateInputDialog dialog = new UpdateInputDialog(Display.getCurrent().getActiveShell(), "Update Proxy IP Address", "Proxy IP", alreadySet,IPNode != null ? IPNode.getTextContent() : "00.00.00.00", new IInputValidator() {	
			@Override
			public String isValid(final String newText) {
				if(!newText.matches("\\d{1,3}[\\.]\\d{1,3}[\\.]\\d{1,3}[\\.]\\d{1,3}")){
					return "Not a valid IP Address.";
				}else
					return null;
			}
		});
	   
		
	    if(dialog.open() == Window.OK){
	    	
	    	IPNode.setTextContent(dialog.getValue());
	    	try{
	    	// write the content into xml file
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(xml);
			final StreamResult result = new StreamResult(qosFile);
			transformer.transform(source, result);
	    	}
	    	catch(final Exception e){
	    		
	    	}
	    	
	    }
	    
	    	
	}
	
	public Node findIPNode(final Document xml){
		//try to find ip address
		final NodeList nodeList = xml.getElementsByTagName("element");
		for(int i = 0; i < nodeList.getLength(); i++){
			final NodeList childList = nodeList.item(i).getChildNodes();
			for(int j = 0; j < childList.getLength(); j++){
				Node item = childList.item(j);
				if(item.getNodeName().equals("name")){
					final String text = item.getTextContent();
					if(text.equals("dds.transport.UDPv4.builtin.public_address")){
						Node parent = item;
						while(parent != null && !parent.getNodeName().equals("qos_library")){
							parent = parent.getParentNode();
						}
						if(parent == null || !parent.getAttributes().getNamedItem("name").getNodeValue().equals("RapidQosLibrary"))
							continue;
						
						while(item != null && !item.getNodeName().equals("value")){
							item = item.getNextSibling();
						}
						return item;
					}
				}
			}
		}
		return null;
	}
	
	public void createIPNode(final Document xml){
		final NodeList nodeList = xml.getElementsByTagName("property");
		for(int i = 0; i < nodeList.getLength(); i++){
			final NodeList childList = nodeList.item(i).getChildNodes();
			for(int j = 0; j < childList.getLength(); j++){
				final Node item = childList.item(j);
				Node parent = item;
				while(parent != null && !parent.getNodeName().equals("qos_library")){
					parent = parent.getParentNode();
				}
				if(parent == null || !parent.getAttributes().getNamedItem("name").getNodeValue().equals("RapidQosLibrary"))
					continue;
				
				if(item.getNodeName().equals("value")){
					final Element element = xml.createElement("element");
					final Element name = xml.createElement("name");
					name.appendChild(xml.createTextNode("dds.transport.UDPv4.builtin.public_address"));
					final Element value = xml.createElement("value");
					value.appendChild(xml.createTextNode("00.00.000.00"));
					element.appendChild(name);
					element.appendChild(value);
					item.appendChild(element);
					break;
				}
			}
		}
	}
	
	public void removeIPNode(final Document xml){
		final NodeList nodeList = xml.getElementsByTagName("element");
		for(int i = 0; i < nodeList.getLength(); i++){
			final NodeList childList = nodeList.item(i).getChildNodes();
			for(int j = 0; j < childList.getLength(); j++){
				final Node item = childList.item(j);
				if(item.getNodeName().equals("name")){
					final String text = item.getTextContent();
					if(text.equals("dds.transport.UDPv4.builtin.public_address")){
						Node parent = item;
						while(parent != null && !parent.getNodeName().equals("qos_library")){
							parent = parent.getParentNode();
						}
						if(parent == null || !parent.getAttributes().getNamedItem("name").getNodeValue().equals("RapidQosLibrary"))
							continue;
						nodeList.item(i).getParentNode().removeChild(nodeList.item(i));
						break;
					}
				}
			}
		}
	}
	
	class UpdateInputDialog extends InputDialog{

		private final boolean REMOVE_BUTTON_ENABLE;

		public UpdateInputDialog(final Shell parentShell, final String dialogTitle, final String dialogMessage, final boolean removeButtonEnabled, final String initialValue,
				final IInputValidator validator) {
			super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
			REMOVE_BUTTON_ENABLE = removeButtonEnabled;
		}
		
		@Override
		protected void createButtonsForButtonBar(final Composite parent) {
			final Button removeButton = createButton(parent, IDialogConstants.ABORT_ID,
	                "Remove Current Proxy IP", true);
			removeButton.setEnabled(REMOVE_BUTTON_ENABLE);
			removeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					super.widgetSelected(e);
					removeIPNode(xml);
					try{
				    	// write the content into xml file
						final TransformerFactory transformerFactory = TransformerFactory.newInstance();
						final Transformer transformer = transformerFactory.newTransformer();
						final DOMSource source = new DOMSource(xml);
						final StreamResult result = new StreamResult(qosFile);
						transformer.transform(source, result);
			    	}
			    	catch(final Exception ee){
			    		
			    	}
					close();
				}
			});
			// TODO Auto-generated method stub
			super.createButtonsForButtonBar(parent);
		}
	}
}
