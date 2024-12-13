import { db } from "../config/firebaseConfig";
import { Request, Response } from "express";


export const homeController = async (req: Request, res: Response) => {
  try {
    // Simulasi pengambilan produk rekomendasi dari Firestore
    const productsSnapshot = await db
      .collection("products")
      .limit(2)
      .get();
    const recommendedProducts = productsSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    // Simulasi pengambilan rutinitas dari Firestore
    const routinesSnapshot = await db
      .collection("routines")
      .limit(5)
      .get();
    const routines = routinesSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.status(200).json({
      status: "success",
      recommended_products: recommendedProducts,
      routines,
    });
  } catch (error: any) {
    res.status(500).json({
      status: "error",
      message: error.message,
    });
  }
};
