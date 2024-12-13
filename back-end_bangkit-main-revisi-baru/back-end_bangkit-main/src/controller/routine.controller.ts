import { db } from "../config/firebaseConfig";
import { Request, Response } from "express";

export const routineController = async (req: Request, res: Response) => {
  try {
    const userId: string = req.user?.id;

    if (!userId) {
      res
        .status(400)
        .json({ status: false, statusCode:400, message: "User ID is missing in token" });
    }

    // Ambil rutinitas dari Firestore berdasarkan userId
    const routinesSnapshot = await db
      .collection("routines")
      .where("userId", "==", userId)
      .orderBy("time", "asc")
      .get();

    if (routinesSnapshot.empty) {
      res.status(200).send({
        status: true,
        statusCode:200,
        routines: [], // Jika tidak ada rutinitas, kembalikan array kosong
      });
    }

    // Format data rutinitas ke dalam array
    const routines = routinesSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.status(200).send({
      status: true,
      statusCode:200,
      routines,
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode:500,
      message: error.message,
    });
  }
};

export const routineDetailController = async (req: Request, res: Response) => {
  try {
    const userId: string = req.user?.id;

    if (!userId) {
        res
          .status(400)
          .send({ status: false, statusCode:400, message: "User ID is missing in token" });
    }

    const { product_id, time } = req.body;

    if (!product_id || !time) {
      res.status(400).send({
        status: false,
        statusCode: 400,
        message: "Product ID and time are required",
      });
    }

    // Verifikasi keberadaan produk di database
    const productDoc = await db.collection("products").doc(product_id).get();
    if (!productDoc.exists) {
      res.status(404).send({
        status: false,
        statusCode: 404,
        message: "Product not found",
      });
    }

    // Ambil detail produk
    const productData = productDoc.data();

    // Buat entri baru di koleksi routines
    const routineRef = await db.collection("routines").add({
      userId,
      product_id,
      product_name: productData?.product_name || "Unknown Product",
      time,
      createdAt: new Date().toISOString(),
    });

    res.status(201).send({
      status: true,
      statusCode: 201,
      message: "Product added to routine successfully",
      routineId: routineRef.id,
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: error.message,
    });
  }
};
