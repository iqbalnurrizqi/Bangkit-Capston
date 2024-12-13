import { Request, Response } from "express";
import { db } from "../config/firebaseConfig";
import { scanType } from "../types/scanType";

export const scanController = async (req: Request, res: Response): Promise<void> => {
  try {
    console.log("Received request for scan history");

    const userId = req.user?.id;
    console.log("User ID from token:", userId); // Debugging User ID

    if (!userId) {
      console.log("Error: User ID is missing in token");
      res.status(400).json({ status: "error", message: "User ID is missing in token" });
      return; // Ensure the function exits after sending the response
    }

    // Fetch user's scan history
    console.log("Fetching scan history for user ID:", userId);
    const scansSnapshot = await db
      .collection("users")
      .where("userId", "==", userId)
      .orderBy("date", "desc")
      .get();

    if (scansSnapshot.empty) {
      console.log("No scan history found for user ID:", userId);
      res.status(404).json({
        status: "success",
        history: [], // Return empty array if no data
      });
      return; // Exit after sending the response
    }

    // Map the scan history to an array
    const history = scansSnapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));
    console.log("Scan history retrieved:", history);

    res.status(200).json({
      status: "success",
      history,
    });
  } catch (error: any) {
    console.error("Error fetching scan history:", error);
    res.status(500).json({
      status: "error",
      message: error.message,
    });
  }
};


export const addScanController = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId: string = req.user?.id; // Assuming `authMiddleware` attaches `user` to `req`
    const { image_uri, skin_type, skin_issues, product_type, product_name }: scanType = req.body;

    console.log("Received scan data:", {
      image_uri,
      skin_type,
      skin_issues,
      product_type,
      product_name,
    });

    // Validate the request body
    if (!skin_type || !skin_issues) {
      console.log("Error: Invalid request body. 'skin_type' and 'skin_issues' are required.");
      res.status(400).send({
        status: false,
        statusCode: 400,
        message: "Invalid request body. 'skin_type' and 'skin_issues' are required.",
      });
      return; // Exit the function after sending the response
    }

    // Save the scan result to Firestore
    const scanData = {
      userId,
      image_uri: image_uri || null, // Optional image URI
      skin_type,
      skin_issues,
      product_type: product_type || null, // Optional product type
      product_name: product_name || null, // Optional product
      date: new Date().toISOString(), // Save the timestamp
    };
    console.log("Saving scan data to Firestore:", scanData);

    await db.collection("scan_results").add(scanData);

    // Respond with success
    res.status(200).send({
      status: true,
      statusCode: 200,
      message: "Analysis result uploaded successfully.",
    });
  } catch (error: any) {
    console.error("Error uploading scan result:", error);
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: "Failed to upload analysis result.",
    });
  }
};
