import { Request, Response } from "express";
import { bucket, db } from "../config/firebaseConfig";
import { profileType } from "../types/profileType";

export const getProfile = async (req: Request, res: Response): Promise<void> => {
  try {
    const userId: string = req.user?.id;
    if (!userId) {
      res.status(400).send({
        status: false,
        statusCode: 400,
        message: "User ID is missing in token",
      });
      return;
    }

    const userDoc = await db.collection("users").doc(userId).get();
    if (!userDoc.exists) {
      res.status(404).send({
        status: false,
        statusCode: 404,
        message: "User not found",
      });
      return;
    }

    const userData = userDoc.data();

    res.status(200).send({
      name: userData?.displayName,
      email: userData?.email,
      skinType: userData?.skinType || "Unknown",
      skinProblems: userData?.skinIssues || [], // Pastikan konsisten dengan skinIssues
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      error: error.message,
    });
  }
};

export const updateProfile = async (req: Request, res: Response): Promise<void> => {
  const { name, skin_type, skin_issues, treatment_goal }: profileType = req.body;

  if (!name && !skin_type && !skin_issues && !treatment_goal) {
    res.status(400).send({
      status: false,
      statusCode: 400,
      message: "At least one field is required to update the profile",
    });
    return;
  }

  try {
    const userId: string = req.user?.id;
    if (!userId) {
      res.status(400).send({
        status: false,
        statusCode: 400,
        message: "User ID is missing in token",
      });
      return;
    }

    const userRef = db.collection("users").doc(userId);
    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      res.status(404).send({
        status: false,
        statusCode: 404,
        message: "User not found",
      });
      return;
    }

    const updatedData: any = {};
    if (name) updatedData.displayName = name;
    if (skin_type) updatedData.skinType = skin_type;
    if (skin_issues) updatedData.skinIssues = skin_issues;
    if (treatment_goal) updatedData.treatmentGoal = treatment_goal;

    console.log("Updated data:", updatedData);

    await userRef.update(updatedData);

    res.status(200).send({
      status: true,
      statusCode: 200,
      message: "Profile updated successfully",
    });
  } catch (error: any) {
    res.status(500).send({
      status: false,
      statusCode: 500,
      message: error.message,
    });
  }
};

// Untuk mengambil data profil pengguna.
export const getProfileData = async (req: Request, res: Response) => {
  try {
    // Ambil userId dari token JWT
    const userId: string = req.user?.id;
    if (!userId) {
      res
        .status(400)
        .send({ status: false, statusCode:400, message: "User ID is missing in token" });
    }

    // Ambil data pengguna dari Firestore
    const userDoc = await db.collection("users").doc(userId).get();
    if (!userDoc.exists) {
      res.status(404).send({ status: false, statusCode:404, message: "User not found" });
    }

    const userData = userDoc.data();

    // Format response
    const response = {
      status: "success",
      data: {
        name: userData?.name || "N/A",
        email: userData?.email || "N/A",
        skin_type: userData?.skinType || "Unknown",
        skin_issues: userData?.skinIssues || [],
        treatment_goal: userData?.treatmentGoal || "N/A",
        photo_url: userData?.photoUrl || "N/A",
      },
    };
    res.status(201).send({status:true, statusCode:201, message:"get profile data success", data:response});
  } catch (error: any) {
    res.status(400).send({ status:false, statusCOde:400, message: error.message });
  }
};

export const updateProfileData = async (req: Request, res: Response) => {
  try {
    // Ambil userId dari token JWT
    const userId: string = req.user?.id;
    if (!userId) {
      res
        .status(400)
        .send({ status: false, statusCode:400, message: "User ID is missing in token" });
    }

    // Validasi data input
    const { name, skin_type, skin_issues, treatment_goal } = req.body;
    if (!name || !skin_type || !Array.isArray(skin_issues) || !treatment_goal) {
      res.status(400).send({
        status: false,
        stutusCode:400,
        message: "Invalid request body. Please provide all required fields.",
      });
    }

    // Perbarui data pengguna di Firestore
    const userRef = db.collection("users").doc(userId);
    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      res
        .status(404)
        .send({ status: false, statusCode:404, message: "User not found" });
    }

    await userRef.update({
      displayName: name,
      skinType: skin_type,
      skinIssues: skin_issues,
      treatmentGoal: treatment_goal,
    });

    res
      .status(200)
      .send({ status: true, statusCode:200, message: "Profile updated successfully" });
  } catch (error: any) {
    res.status(500).send({ status: false, statusCode:500, message: error.message });
  }
};

export const updateFotoProfile = async (req: any, res: Response) => {
       try {
         const userId = req.user.id; // Extract user ID from middleware

         // Validate file upload
         if (!req.file) {
          res.status(400).send({
             status: false,
             statusCode: 400,
             message: "No file uploaded. Please provide a JPEG or PNG image.",
           });
         }

         // Generate unique filename for the uploaded photo
         const filename = `users/${userId}/profile-${Date.now()}-${
           req.file.originalname || "photo"}`;

         // Upload the file to Firebase Storage
         const file = bucket.file(filename);
         await file.save(req.file.buffer, {
           contentType: req.file.mimetype,
           public: true,
         });

         // Generate public URL for the uploaded photo
         const photoUrl = `https://storage.googleapis.com/${bucket.name}/${filename}`;

         // Update user's profile photo URL in Firestore
         const userRef = db.collection("users").doc(userId);
         await userRef.update({ photo_url: photoUrl });

         // Respond with success
        res.status(200).send({
           status: true,
           statusCode: 200,
           message: "Profile photo updated successfully",
           photo_url: photoUrl,
         });
       } catch (error) {
         console.error("Error updating profile photo:", error);
        res.status(500).send({
           status: false,
            statusCode: 500,
           message: "Failed to update profile photo.",
         });
       }
     };
