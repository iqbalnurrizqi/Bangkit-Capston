import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { addNewUser, deleteUser, getAllUsers, updateUser } from "../controller/user.controller";
import { getProfileData, updateFotoProfile, updateProfile, updateProfileData } from "../controller/profile.controller";
import multer from "multer";



const router = Router();

// Get All  (DONE)
router.get("/", authenticateToken, getAllUsers);

// Add New User (DONE)
router.post("/", addNewUser);

// Update User
router.put("/:id", updateUser);

// Delete User
router.delete("/:id", deleteUser);


// Get Profile Data
router.get("/profile",authenticateToken, getProfileData );

// Untuk memperbarui data profil pengguna
router.put("/profile",authenticateToken, updateProfile);

// Multer setup for file upload
const upload = multer({
    limits: { fileSize: 5 * 1024 * 1024 }, // Max file size: 5MB
    fileFilter: (req, file, cb) => {
        if (file.mimetype === "image/jpeg" || file.mimetype === "image/png") {
            cb(null, true);
        } else {
            cb(new Error("Only JPEG or PNG images are allowed."));
        }
    },
});

/**
 * POST /user/profile/photo
 * Upload or update the user's profile photo.
 */
router.post(
  "/user/profile/photo",
  authenticateToken,
  upload.single("photo"),
  updateFotoProfile
);

export default router;
