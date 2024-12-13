import admin from "firebase-admin";
import { serviceAccount } from "../services/service-account-key.json";


admin.initializeApp({
  credential: admin.credential.cert(serviceAccount as admin.ServiceAccount),
  storageBucket: "gs://bangkit-project-123.firebasestorage.app",
});

export const db = admin.firestore();
export const auth = admin.auth();
export const bucket = admin.storage().bucket();
