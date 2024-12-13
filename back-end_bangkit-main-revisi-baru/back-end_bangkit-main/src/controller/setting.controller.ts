import { Request, Response } from "express";
import { db } from "../config/firebaseConfig";

export const settingController = async (req:Request, res: Response) => {
  try {
    const userId:string = req.user?.id;

    if (!userId) {
      res
        .status(400)
        .send({ status: false, statusCode:400, message: "User ID is missing in token" });
    }

    // Ambil pengaturan pengguna dari Firestore
    const settingsDoc = await db.collection("settings").doc(userId).get();

    if (!settingsDoc.exists) {
      res.status(404).send({
        status: false,
        statusCode:404,
        message: "Settings not found for this user",
      });
    }

    // Ambil data pengaturan
    const settingsData = settingsDoc.data();

    res.status(200).send({
      status: true,
      statusCode:200,
      data: {
        dark_mode: settingsData?.dark_mode || false,
        notifications_enabled: settingsData?.notifications_enabled || false,
      },
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode:500,
      message: error.message,
    });
  }
};

export const updateSettingController = async (req: Request, res: Response) => {
  try {
    const userId: string = req.user?.id;

    if (!userId) {
       res
        .status(400)
        .send({ status: false, statusCode:400, message: "User ID is missing in token" });
    }

    const { dark_mode, notifications_enabled, language }: any = req.body;

    // Validasi input
    if (
      dark_mode === undefined ||
      notifications_enabled === undefined ||
      !language
    ) {
      res.status(400).send({
        status: false,
        statusCode: 400,
        message:
          "All fields (dark_mode, notifications_enabled) are required",
      });
    }

    // Perbarui pengaturan pengguna di Firestore
    const settingsRef = db.collection("settings").doc(userId);
    await settingsRef.set(
      { dark_mode, notifications_enabled },
      { merge: true } // Menggabungkan dengan data yang sudah ada
    );

    res.status(200).send({
      status: true,
      statusCode: 200,
      message: "Settings updated successfully",
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: error.message,
    });
  }
};