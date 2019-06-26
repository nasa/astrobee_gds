package gov.nasa.rapid.v2.ui.e4;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;

import gov.nasa.dds.rti.system.IParticipantCustomization;
import gov.nasa.dds.rti.system.PropertyQosStore;

public class PublicAddressParticipantCustomization implements
		IParticipantCustomization {
	private static final String PUBLIC_ADDRESS_PROPERTY = "dds.transport.UDPv4.builtin.public_address";

	private final String m_publicAddress;

	public PublicAddressParticipantCustomization(final String address) {
		m_publicAddress = address;
	}

	@Override
	public boolean customizeFactory(String participantId,
			DomainParticipantFactoryQos factoryQos) {
		return false;
	}

	@Override
	public boolean customizePreCreation(String participantId,
			DomainParticipantQos participantQos) {
		PropertyQosStore qosStore = new PropertyQosStore(participantQos.property.value);
		qosStore.put(PUBLIC_ADDRESS_PROPERTY, m_publicAddress);
		qosStore.assign(participantQos.property.value);
		return false;
	}

	@Override
	public boolean customizePostCreation(String participantId,
			DomainParticipant participant) {
		return false;
	}

}
