# ccloud-dataflow-demo
A simple example of Confluent Cloud &amp; Dataflow Integration using Google KMS for CC keys &amp; secrets

## Example Usage

```
set GOOGLE_APPLICATION_CREDENTIALS=<gcp credentials file>
```

Create Google KMS Keyring and add username and password entries

Add your Confluent Cloud API Key and secret to Google KMS Keyring

Authenticate in your terminal and get the access token

```
gcloud auth application-default login
export accesstoken=$(gcloud auth application-default print-access-token)
```

Encrypt your Confluent Cloud API Key and Secret using a `curl` request

```
curl "https://cloudkms.googleapis.com/v1/projects/partner-engineering/locations/<region>/keyRings/<key ring ID>/cryptoKeys/<username/password key ID>:encrypt" \
  --request "POST" \
  --header "authorization: Bearer $accesstoken" \
  --header "content-type: application/json" \
  --data "{\"plaintext\": \"<base64 username/password>"}"
```

Compile and run your pipeline

```
mvn package
```

```
java -jar target/ccloud-dataflow-demo-1.0-SNAPSHOT.jar
  --project=<GCP Project> \
  --region=<Region> \
  --stagingLocation=<GCS Staging Location>\
  --gcpTempLocation=<GCS Temp Location> \
  --runner=DataflowRunner \
  --keyRing=<KMS keyring ID> \
  --kmsUsernameKeyId=<Path to Google KMS keyring/Confluent Cloud Username ID> \
  --confluentCloudEncryptedUsername=<Ciphertext for Confluent Cloud Username> \
  --kmsPasswordKeyId=<Path to Google KMS keyring/Confluent Cloud Password ID> \
  --confluentCloudEncryptedPassword=<Ciphertext for Confluent Cloud Password>
```