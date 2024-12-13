import {  Router } from "express";
import { forgotPassword, loginAuth, registerAuth } from "../controller/auth.controller";
import { authenticateToken } from "../middleware/authMiddleware";


const router = Router();

// Register User (DONE)
router.post("/register", registerAuth);

// Login User (DONE)
router.post("/login", loginAuth);

// Forgot Password Endpoint (DONE)
router.post("/forget-password", forgotPassword);


export default router;
