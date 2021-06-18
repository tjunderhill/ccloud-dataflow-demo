package com.tunderhill;


import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface CustomPipelineOptions extends DataflowPipelineOptions {
    @Description("Confluent Cloud Encrypted Username")
    @Default.String("")
    String getConfluentCloudEncryptedUsername();
    void setConfluentCloudEncryptedUsername(String ConfluentCloudEncryptedUsername);

    @Description("Confluent Cloud Encrypted Password")
    @Default.String("")
    String getConfluentCloudEncryptedPassword();
    void setConfluentCloudEncryptedPassword(String ConfluentCloudEncryptedPassword);

    @Description("Google KMS Confluent Cloud Password Key Id")
    @Default.String("")
    String getkmsUsernameKeyId();
    void setkmsUsernameKeyId(String kmsUsernameKeyId);

    @Description("Google KMS Confluent Cloud Password Key Id")
    @Default.String("")
    String getkmsPasswordKeyId();
    void setkmsPasswordKeyId(String kmsPasswordKeyId);

    @Description("Google KMS Key Ring")
    @Default.String("")
    String getkeyRing();
    void setkeyRing(String keyRing);
}