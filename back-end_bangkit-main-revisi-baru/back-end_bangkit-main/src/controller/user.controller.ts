import { Request, Response } from "express";
import { db, bucket } from "../config/firebaseConfig";

export const getAllUsers = async (req: Request, res: Response) => {
  try {
    const snapshot = await db.collection("users").get();
    const users: any[] = [];
    snapshot.forEach((doc) => {
      users.push({ id: doc.id, ...doc.data() });
    });
    res.status(200).json(users);
  } catch (error: any) {
    res.status(500).send({ message: error.message });
  }
};

export const addNewUser = async (req: Request, res: Response) => {
  try {
    const userData = req.body;
    const docRef = await db.collection("users").add(userData);
    res.status(201).json({ id: docRef.id, ...userData });
  } catch (error: any) {
    res.status(500).send({status:false, statusCode:500, message: error.message });
  }
};

export const updateUser = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const userData = req.body;
    await db.collection("users").doc(id).update(userData);
    res.status(200).send({status:true, statusCode:200, message: "User updated successfully" });
  } catch (error: any) {
    res.status(500).send({status:false, statusCode:500, message: error.message });
  }
};

export const deleteUser = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    await db.collection("users").doc(id).delete();
    res.status(200).send({status:true, statusCode:200, message: "User deleted successfully" });
  } catch (error: any) {
    res.status(500).send({status:false, statusCode:500, message: error.message });
  }
};

export const getProfileData = async (req: Request, res: Response) => {
  try {
    const userId: string = req.user.id; // Assuming authMiddleware attaches user info to req.user

    // Check if a file is provided
    if (!req.file) {
      return res.status(400).send({
        status: false,
        statusCode: 400,
        message: "No file uploaded",
      });
    }

    // Generate unique filename
    const filename = `profile_photos/${userId}-${Date.now()}-${
      req.file.originalname
    }`;

    // Upload file to Firebase Storage
    const file = bucket.file(filename);
    await file.save(req.file.buffer, {
      contentType: req.file.mimetype,
      public: true, // Make file publicly accessible
    });

    // Get public URL of the uploaded file
    const photoUrl = `https://storage.googleapis.com/${bucket.name}/${filename}`;
    console.log("Photo URL saved to Firestore:", photoUrl);

    // Update user's photo URL in Firestore
    await db.collection("users").doc(userId).update({
      photo_url: photoUrl,
    });

    return res.status(200).send({
      status: true,
      statusCode: 200,
      message: "Profile photo updated successfully",
      photo_url: photoUrl,
    });
  } catch (error) {
    console.error("Error updating profile photo:", error);
    res.status(500).send({
      status: false,
      statusCode:500,
      message: "Internal server error",
    });
  }
};
