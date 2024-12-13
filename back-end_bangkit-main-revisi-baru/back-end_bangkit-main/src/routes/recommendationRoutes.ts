import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { db } from "../config/firebaseConfig";
import { getScanResult, recommendationController } from "../controller/recommendation.controller";

const router = Router();

router.post("/",authenticateToken, recommendationController);
router.get("/",authenticateToken, getScanResult);``

export default router;
