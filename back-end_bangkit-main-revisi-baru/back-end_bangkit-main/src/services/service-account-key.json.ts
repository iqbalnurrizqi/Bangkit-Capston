import dotenv from "dotenv";
dotenv.config();

export const serviceAccount = {
  type: "service_account",
  project_id: "bangkit-project-123",
  private_key_id: process.env.private_key_id,
  private_key: process.env.private_key,
  client_email:process.env.client_email,
  client_id: "112134047264642882433",
  auth_uri: "https://accounts.google.com/o/oauth2/auth",
  token_uri: "https://oauth2.googleapis.com/token",
  auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs",
  client_x509_cert_url:
    "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-57zs7%40bangkit-project-123.iam.gserviceaccount.com",
  universe_domain: "googleapis.com",
};
// service-account-key.json
