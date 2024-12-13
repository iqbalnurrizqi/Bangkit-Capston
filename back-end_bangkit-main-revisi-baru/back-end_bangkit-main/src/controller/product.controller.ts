import { db } from "../config/firebaseConfig";
import { Request, Response } from "express";

export const productController = async (req: Request, res: Response) => {
  try {
    // Mengambil daftar produk dari Firestore
    const productsSnapshot = await db
      .collection("products")
      // .orderBy("name", "asc")
      .get();

    if (productsSnapshot.empty) {
      res.status(404).send({
        status: false,
        statusCode: 404,
        products: [], // Kembalikan array kosong jika tidak ada produk
      });
    }

    // Format data produk ke dalam array
    const products = productsSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.status(200).send({
      status: true,
      statusCode: 200,
      products,
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: error.message,
    });
  }
};

export const productDetailController = async (req: Request, res: Response) => {
  try {
    const { product_id } = req.params;

    // Ambil data produk dari Firestore berdasarkan ID
    const productDoc = await db.collection("products").doc(product_id).get();

    if (!productDoc.exists) {
    res.status(404).send({
        status: false,
        statusCode: 404,
        message: "Product not found",
      });
    }

    // Data produk yang ditemukan
    const productData = productDoc.data();

    res.status(200).send({
      status: true,
      statusCode: 200,
      data: {
        name: productData?.name,
        description: productData?.description,
        usage_instructions:
          productData?.usage_instructions || "No instructions provided",
        warnings: productData?.warnings || "No warnings provided",
        image_url: productData?.image_url || "No image available",
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
