import { Request, Response } from "express";
import { db } from "../config/firebaseConfig";


export const historyController = async (req: Request, res: Response) => {
  try {
    // Ambil userId dari token JWT
    const userId: string = req.user?.id;
    if (!userId) {
      res.status(400).send({status:false, statusCode:400, message: "User ID is missing in token" });
    }

    // Ambil data riwayat scan dari koleksi Firestore
    const scanHistoryRef = db
      .collection("scanHistory")
      .where("userId", "==", userId);
    const snapshot = await scanHistoryRef.get();

    if (snapshot.empty) {
      res.status(404).send({status:false, statusCode:404, message: "history is empty", history: [] });
    }

    // Format data riwayat scan
    const history = snapshot.docs.map((doc, index) => ({
      id: index + 1,
      date: doc.data().date,
      result: doc.data().result,
    }));

    res.status(200).send({status: true, statusCode:200, message: "history is success", data: history });
  } catch (error: any) {
    res.status(401).send({status:false, statusCode:401, messaga: error.message });
  }
};
