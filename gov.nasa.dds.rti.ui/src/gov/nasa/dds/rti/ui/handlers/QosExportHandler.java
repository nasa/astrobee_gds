/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.dds.rti.ui.handlers;

import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.util.DiscoveredParticipants;
import gov.nasa.dds.rti.util.DiscoveredParticipants.ParticipantInfo;
import gov.nasa.dds.rti.util.QosXmlPrinter;
import gov.nasa.util.ui.LastPath;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.DataWriterSeq;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherSeq;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.DataReaderSeq;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberSeq;

public class QosExportHandler extends AbstractHandler {
    private static final Logger logger = Logger.getLogger(QosExportHandler.class);

    protected final QosXmlPrinter qosPrinter = new QosXmlPrinter();

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            String lastPath = LastPath.get(this);
            DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
            dialog.setFilterPath(lastPath);
            dialog.setText("QoS Export Directory");
            dialog.setMessage("Select a directory in which the QoS files will be created");

            if(dialog.open() != null) {
                final String filePath = dialog.getFilterPath();
                LastPath.set(this, filePath);
                File dir = new File(filePath);

                for(String participantId : DdsEntityFactory.getValidParticipantIds()) {
                    DomainParticipant dp = DdsEntityFactory.getParticipant(participantId);
                    DomainParticipantQos dpQos = new DomainParticipantQos();
                    dp.get_qos(dpQos);
                    writeQos(participantId+".participant_qos.xml", dir, dpQos);

                    PublisherSeq  publishers  = new PublisherSeq();
                    SubscriberSeq subscribers = new SubscriberSeq();
                    DataWriterSeq dataWriters = new DataWriterSeq();
                    DataReaderSeq dataReaders = new DataReaderSeq();
                    DataWriterQos writerQos = new DataWriterQos();
                    DataReaderQos readerQos = new DataReaderQos();

                    dp.get_publishers(publishers);
                    for(Object publisherObj : publishers) {
                        Publisher publisher = (Publisher)publisherObj;
                        publisher.get_all_datawriters(dataWriters);
                        for(Object dataWriterObj : dataWriters) {
                            DataWriter dataWriter = (DataWriter)dataWriterObj;
                            dataWriter.get_qos(writerQos);
                            String filename = participantId+".datawriter_qos."+dataWriter.get_topic().get_name()+".xml";
                            writeQos(filename, dir, writerQos);
                        }
                    }
                    dp.get_subscribers(subscribers);
                    for(Object subscriberObj : subscribers) {
                        Subscriber subscriber = (Subscriber)subscriberObj;
                        subscriber.get_all_datareaders(dataReaders);
                        for(Object dataReaderObj : dataReaders) {
                            DataReader dataReader = (DataReader)dataReaderObj;
                            dataReader.get_qos(readerQos);
                            String filename = participantId+".datareader_qos."+dataReader.get_topicdescription().get_name()+".xml";
                            writeQos(filename, dir, readerQos);
                        }
                    }
                }

                logger.debug("Discovered Participants:");
                for(String participantId : DdsEntityFactory.getValidParticipantIds()) {
                    Map<String,ParticipantInfo> dps = DiscoveredParticipants.getDiscoveredParticipants(participantId);
                    logger.debug(participantId+":");
                    for(String participant : dps.keySet()) {
                        String[] addresses = dps.get(participant).locatorStrings;
                        logger.debug("    "+participant);
                        for(String addr : addresses) {
                            logger.debug("        "+addr);
                        }
                    }
                }
            }
        }
        catch(Throwable t) {
            throw new ExecutionException("oops", t);
        }

        return null;
    }

    protected void writeQos(String filename, File dir, Object qos) {
        String qosFilename = dir.getAbsolutePath()+File.separator+filename;
        try {
            String qosString   = qosPrinter.toString(qos);
            FileOutputStream   fos = new FileOutputStream(qosFilename);
            fos.write(qosString.getBytes());
            fos.close();
            logger.debug("Wrote "+qosFilename);
        }
        catch(Throwable t) {
            logger.warn("Could not write "+qosFilename);
        }
    }
}
