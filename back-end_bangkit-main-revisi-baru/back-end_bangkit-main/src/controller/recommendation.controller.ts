import { Request, Response } from "express";
import { db } from "../config/firebaseConfig";
import { recomendationType } from '../types/recomendationType';
import { productController } from "./product.controller";

export const recommendationController = async (req: Request, res: Response) => {
  try {
    const { skin_type, skin_issues }: recomendationType = req.body;

    // Validasi input
    if (!skin_type || !Array.isArray(skin_issues)) {
      res.status(400).send({
        status: false,
        statusCode: 400,
        message: "skin_type (string) and skin_issues (array) are required",
      });
    }

    // Ambil produk yang direkomendasikan dari Firestore (contoh koleksi: "products")
    const productsSnapshot = await db
      .collection("scan_results")
      .where("skin_type", "==", skin_type)
      .get();

    const getProducts = await db.collection("scan_results").get();

    const recommendedProducts: any = [];
    getProducts.forEach((doc) => {
      const product = doc.data();
      recommendedProducts.push(product);
    });

    console.log(recommendedProducts);

    // Ambil klinik yang direkomendasikan dari Firestore (contoh koleksi: "hospital")
    const clinicsSnapshot = await db.collection("hospital").get();
    const recommendedClinics: any = [];
    clinicsSnapshot.forEach((doc) => {
      const clinic = doc.data();
      recommendedClinics.push(clinic);
    });


    // Kirim respons dengan data rekomendasi
    res.status(200).send({
      status: true,
      statusCode: 200,
      data: {
        recommended_products: recommendedProducts,
        recommended_clinics: recommendedClinics,
      },
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: error.message,
    });
  }
};

export const getScanResult = async (req: Request, res: Response) => {
  try {

    const getProducts = await db.collection("scan_results").get();
    const recommendedProducts: any = [];
    getProducts.forEach((doc) => {
      const product = doc.data();
      recommendedProducts.push(product);
    });

    res.status(200).send({
      status: true,
      statusCode: 200,
      data: {
        recommended_products: recommendedProducts
      },
      message: "Scan result retrieved successfully",
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      error: error.message,
    });
  }
};
