

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.DataReaderImpl;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.topic.TypeSupportImpl;

// ===========================================================================

/**
* A reader for the StateMachineState user type.
*/
public class StateMachineStateDataReader extends DataReaderImpl {
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------

    public void read(StateMachineStateSeq received_data, SampleInfoSeq info_seq,
    int max_samples,
    int sample_states, int view_states, int instance_states) {
        read_untyped(received_data, info_seq, max_samples, sample_states,
        view_states, instance_states);
    }

    public void take(StateMachineStateSeq received_data, SampleInfoSeq info_seq,
    int max_samples,
    int sample_states, int view_states, int instance_states) {
        take_untyped(received_data, info_seq, max_samples, sample_states,
        view_states, instance_states);
    }

    public void read_w_condition(StateMachineStateSeq received_data, 
    SampleInfoSeq info_seq,
    int max_samples,
    ReadCondition condition) {
        read_w_condition_untyped(received_data, info_seq, max_samples,
        condition);
    }

    public void take_w_condition(StateMachineStateSeq received_data, 
    SampleInfoSeq info_seq,
    int max_samples,
    ReadCondition condition) {
        take_w_condition_untyped(received_data, info_seq, max_samples,
        condition);
    }

    public void read_next_sample(StateMachineState received_data, SampleInfo sample_info) {
        read_next_sample_untyped(received_data, sample_info);
    }

    public void take_next_sample(StateMachineState received_data, SampleInfo sample_info) {
        take_next_sample_untyped(received_data, sample_info);
    }

    public void read_instance(StateMachineStateSeq received_data, SampleInfoSeq info_seq,
    int max_samples, InstanceHandle_t a_handle, int sample_states,
    int view_states, int instance_states) {

        read_instance_untyped(received_data, info_seq, max_samples, a_handle,
        sample_states, view_states, instance_states);
    }

    public void take_instance(StateMachineStateSeq received_data, SampleInfoSeq info_seq,
    int max_samples, InstanceHandle_t a_handle, int sample_states,
    int view_states, int instance_states) {

        take_instance_untyped(received_data, info_seq, max_samples, a_handle,
        sample_states, view_states, instance_states);
    }

    public void read_instance_w_condition(StateMachineStateSeq received_data,
    SampleInfoSeq info_seq, int max_samples,
    InstanceHandle_t a_handle, ReadCondition condition) {

        read_instance_w_condition_untyped(received_data, info_seq, 
        max_samples, a_handle, condition);
    }

    public void take_instance_w_condition(StateMachineStateSeq received_data,
    SampleInfoSeq info_seq, int max_samples,
    InstanceHandle_t a_handle, ReadCondition condition) {

        take_instance_w_condition_untyped(received_data, info_seq, 
        max_samples, a_handle, condition);
    }

    public void read_next_instance(StateMachineStateSeq received_data,
    SampleInfoSeq info_seq, int max_samples,
    InstanceHandle_t a_handle, int sample_states, int view_states,
    int instance_states) {

        read_next_instance_untyped(received_data, info_seq, max_samples,
        a_handle, sample_states, view_states, instance_states);
    }

    public void take_next_instance(StateMachineStateSeq received_data,
    SampleInfoSeq info_seq, int max_samples,
    InstanceHandle_t a_handle, int sample_states, int view_states,
    int instance_states) {

        take_next_instance_untyped(received_data, info_seq, max_samples,
        a_handle, sample_states, view_states, instance_states);
    }

    public void read_next_instance_w_condition(StateMachineStateSeq received_data,
    SampleInfoSeq info_seq, int max_samples,
    InstanceHandle_t a_handle, ReadCondition condition) {

        read_next_instance_w_condition_untyped(received_data, info_seq, 
        max_samples, a_handle, condition);
    }

    public void take_next_instance_w_condition(StateMachineStateSeq received_data,
    SampleInfoSeq info_seq, int max_samples,
    InstanceHandle_t a_handle, ReadCondition condition) {

        take_next_instance_w_condition_untyped(received_data, info_seq, 
        max_samples, a_handle, condition);
    }

    public void return_loan(StateMachineStateSeq received_data, SampleInfoSeq info_seq) {
        return_loan_untyped(received_data, info_seq);
    }

    public void get_key_value(StateMachineState key_holder, InstanceHandle_t handle){
        get_key_value_untyped(key_holder, handle);
    }

    public InstanceHandle_t lookup_instance(StateMachineState key_holder) {
        return lookup_instance_untyped(key_holder);
    }

    // -----------------------------------------------------------------------
    // Package Methods
    // -----------------------------------------------------------------------

    // --- Constructors: -----------------------------------------------------

    /*package*/  StateMachineStateDataReader (long native_reader, DataReaderListener listener,
    int mask, TypeSupportImpl data_type) {
        super(native_reader, listener, mask, data_type);
    }

}

