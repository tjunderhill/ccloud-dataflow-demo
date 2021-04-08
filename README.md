# ccloud-dataflow-demo
A simple example of Confluent Cloud &amp; Dataflow Integration using Google KMS for CC keys &amp; secrets

## Example Usage

```
set GOOGLE_APPLICATION_CREDENTIALS=<gcp credentials file>
```

Create Google KMS Keyring and add username and password entries

Add your Confluent Cloud API Key and secret to Google KMS Keyring

```
mvn package
```

```
java -jar target\ccloud-dataflow-demo-1.0-SNAPSHOT.jar
  --project=<GCP Project>
  --region=<Region>
  --stagingLocation=<GCS Staging Location>
  --gcpTempLocation=<GCS Temp Location>
  --runner=DataflowRunner 
  --kmsUsernameKeyId=<Path to Google KMS keyring/Confluent Cloud Username ID> 
  --confluentCloudEncryptedUsername=<Ciphertext for Confluent Cloud Username>
  --kmsPasswordKeyId=<Path to Google KMS keyring/Confluent Cloud Password ID>  
  --confluentCloudEncryptedPassword=<Ciphertext for Confluent Cloud Password>
```