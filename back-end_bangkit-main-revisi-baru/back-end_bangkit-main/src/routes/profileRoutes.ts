
// Endpoint yang memerlukan autentikasi
import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { getProfile, updateProfile } from "../controller/profile.controller";

const router = Router();

// Get Profile Endpoint
router.get("/", authenticateToken, getProfile
);

// Update Profile Endpoint
router.put("/", authenticateToken, updateProfile);

export default router;

