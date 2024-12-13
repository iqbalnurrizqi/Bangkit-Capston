import { Router } from "express";
import { authenticateToken } from "../middleware/authMiddleware";
import { historyController } from "../controller/history.controller";

const router = Router();

// Get Scan History Endpoint
router.get("/scan/history", authenticateToken, historyController);
