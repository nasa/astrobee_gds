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
package gov.nasa.rapid.v2.framestore.tree.visitors;

import java.io.IOException;
import java.io.OutputStream;

import gov.nasa.rapid.v2.framestore.tree.FrameTree;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

/**
 * Visitor to print Frames of a tree to an OutputStream
 *
 * The visitor can be configured to print to the desired OutputStream, 
 * with our without the transform details and potentially using a 
 * indented format.
 */
public class PrintFrameVisitor implements IFrameVisitor {

    protected OutputStream m_stream;
    protected boolean m_printDetails;
    protected boolean m_prettyPrint;
    protected String m_indent = "+-";
    
    static protected byte[] s_newLine = System.getProperty("line.separator").getBytes();
    
    /**
     * Default constructor.
     */
    public PrintFrameVisitor() {
        this(System.out, false, false);
    }
            
    /**
     * Constructor with selection of only the OutputStream
     * @param stream    OutputStream to use
     */
    public PrintFrameVisitor(OutputStream stream) {
        this(stream, false, false);
    }
    
    /**
     * Constructor with only selection of the output stream and prettyPrint 
     * @param stream        OutputStream to use
     * @param printDetails  print the transform matrix or not  
     */
    public PrintFrameVisitor(OutputStream stream, boolean printDetails) {
        this(stream, printDetails, false);
    }
    
    /**
     * General constructor
     * @param stream        OutputStream to use
     * @param printDetails  print the details of the transform or not
     * @param prettyPrint   print using indentation or not
     */
    public PrintFrameVisitor(OutputStream stream, boolean printDetails, boolean prettyPrint) {
        m_stream = stream;
        m_printDetails = printDetails;
        m_prettyPrint = prettyPrint;
    }

    /**
     * Writes the frame information for the given node to the output stream
     */
    public boolean visit(FrameTreeNode node) {
        try {
            if ( m_prettyPrint ) {
                m_stream.write(m_indent.getBytes());
            }
            m_stream.write(FrameTree.getFullNameOf(node).getBytes());
            m_stream.write(s_newLine);
            if ( m_printDetails ) {
                m_stream.write(node.getFrame().getTransform().toString().getBytes());
                m_stream.write(s_newLine);
            }
//            if ( m_prettyPrint ) {
//                m_stream.write("|".getBytes());
//                m_stream.write(s_newLine);
//            }
        } catch (IOException e) {
            System.err.println("PrintFrameVisitor could not write to the stream!" + e);
        }
        return false;
    }

    public void down(FrameTreeNode node) {
        m_indent += "--";
    }

    public void up(FrameTreeNode node) {
        int l = m_indent.length();
        if ( l > 3 ) {
            m_indent = m_indent.substring(0, l-2);
        }
    }

}
